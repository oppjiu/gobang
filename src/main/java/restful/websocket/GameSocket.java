package restful.websocket;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.bean.ChatData;
import restful.bean.CommonResult;
import restful.database.EM;
import restful.entity.GameRoom;
import restful.entity.Player;
import restful.entity.User;
import restful.utils.GameSocketUtil;
import restful.utils.JsonUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lwz
 * @create 2021-12-07 22:28
 * @description:
 */
@ServerEndpoint(value = "/ws/game", configurator = GetHttpSessionConfigurator.class)
public class GameSocket {
    private HttpSession httpSession;
    private Session session;

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        this.session = session;
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());

        //存入onlineUsers集合中
        User user = (User) httpSession.getAttribute("user");
        GameSocketUtil.onlinePlayers.put(user.getName(), this);
        /*将连接的user存放到容器中*/
        System.out.println(user.getName() + " player上线了");
        GameSocketUtil.onlineUserLogin(user);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        /*前端传输的数据*/
        JSONObject jsonObject = JSONObject.fromObject(message);
        int code = (int) jsonObject.get("code");
        String msg = (String) jsonObject.get("msg");
        Object data = jsonObject.get("data");
        System.out.println("onMessage的jsonObject的内容为" + jsonObject);

        /*进行对弈的不同方法*/
        if (code == GameSocketUtil.FUN_CHAT) {
            /*聊天*/
            chat(data);
        } else if (code == GameSocketUtil.FUN_CREATE_GAME_ROOM) {
            /*创建房间*/
            createRoom(data);
        } else if (code == GameSocketUtil.FUN_INVITE) {
            /*请求邀请*/
            invite(data);
        } else if (code == GameSocketUtil.FUN_CONFIRM_INVITE) {
            /*是否接受邀请*/
            confirmInvite(data);
        } else if (code == GameSocketUtil.FUN_KICK_OUT) {
            /*踢出玩家*/
            kickOut(data);
        } else if (code == GameSocketUtil.FUN_START_GAME) {
            /*开始游戏*/
            startGame(data);
        } else if (code == GameSocketUtil.FUN_PLAY_GAME) {
            /*游戏对战*/
            playGame(data);
        } else if (code == GameSocketUtil.FUN_CLOSE_ROOM) {
            /*关闭房间*/
            closeRoom(data);
        } else if (code == GameSocketUtil.FUN_GIVE_UP) {
            /*放弃*/
            giveUp(data);
        } else if (code == GameSocketUtil.FUN_DRAW) {
            /*请求平局*/
            draw(data);
        } else if (code == GameSocketUtil.FUN_CONFIRM_DRAW) {
            /*是否同意平局*/
            confirmDraw(data);
        } else if (code == GameSocketUtil.FUN_REGRET_STEP) {
            /*请求悔棋*/
            regretStep(data);
        } else if (code == GameSocketUtil.FUN_CONFIRM_REGRET_STEP) {
            /*是否同意悔棋*/
            confirmRegretStep(data);
        } else if (code == GameSocketUtil.FUN_WATCH_GAME) {
            /*观棋*/
            watchGame(data);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws IOException {
        //TODO 暂停或者退出的时候棋局保存

        System.out.println("GameSocket closeReason: " + closeReason);
        //ChatSocket容器登出，并删除session
        User self = (User) httpSession.getAttribute("user");
        GameSocketUtil.onlineUserLogout(self);
        httpSession.removeAttribute("user");
    }

//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        log.info("GameSocket throwable: {}", throwable.toString());
//    }

    /**
     * 函数码200
     * 聊天功能
     *
     * @param data 前端数据
     * @throws IOException
     */
    public void chat(Object data) throws IOException {
        /*转换成后台聊天类型的标准格式*/
        ChatData chatData = (ChatData) JSONObject.toBean(JSONObject.fromObject(data), ChatData.class);
        User self = (User) this.httpSession.getAttribute("user");
        chatData.setFromWho(self.getName());
        System.out.println("chatData: " + chatData);

        //判断是否是广播
        if (!chatData.isBroadcast()) {
            GameSocket gameSocket = GameSocketUtil.onlinePlayers.get(chatData.getToWho());
            //如果发送对象在线
            if (gameSocket != null) {
                JSONObject msg = JsonUtil.setCommonResultJson(CommonResult.CODE_SUCCESS, "", chatData.getText());
                //给对方发送消息
                JSONObject jsonObject = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CHAT, "", msg);
                gameSocket.session.getBasicRemote().sendText(String.valueOf(jsonObject));
            } else {  //不在线
                JSONObject msg = JsonUtil.setCommonResultJson(CommonResult.CODE_WRONG, "", chatData.getText());
                //给自己发送消息
                JSONObject jsonObject = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CHAT, "", msg);
                this.session.getBasicRemote().sendText(String.valueOf(jsonObject));
            }

        } else {    //广播消息
            for (String onlinePlayer : GameSocketUtil.onlinePlayers.keySet()) {
                JSONObject dataJson = JsonUtil.setCommonResultJson(CommonResult.CODE_SUCCESS, "", chatData.getText());
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CHAT, "", dataJson);
                //异步或同步发送json数据到前端
                GameSocketUtil.onlinePlayers.get(onlinePlayer).getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
        }
    }

    /**
     * 函数码201
     * 创建房间
     *
     * @param data 前端数据
     */
    public void createRoom(Object data) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        /*获取session*/
        User self = (User) this.getHttpSession().getAttribute("user");

        int kind = Integer.parseInt((String) jsonObject.get("kind"));
        int stepTime = Integer.parseInt((String) jsonObject.get("stepTime"));
        int gameTime = Integer.parseInt((String) jsonObject.get("gameTime"));
        int chessX; /*= Integer.parseInt((String) jsonObject.get("chessX"));*/
        int chessY; /*= Integer.parseInt((String) jsonObject.get("chessY"));*/
        chessX = 16;
        chessY = 16;
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();

            /*创建该会话的session玩家的信息*/
            User user = entityManager.createNamedQuery("findUserByName", User.class).setParameter("name", self.getName()).getSingleResult();
            String roomMaster = user.getName();
            String gender = user.getGender();
            int level = user.getLevel();

            /*初始化房间*/
            GameRoom gameRoom = new GameRoom();
            gameRoom.setRoomMaster(roomMaster);
            gameRoom.setStepTime(stepTime);
            gameRoom.setGameTime(gameTime);
            /*初始化棋盘*/
            gameRoom.initChessData(chessX, chessY);
            gameRoom.setChessDataStr(gameRoom.saveChessDataAsString());
            gameRoom.setChessStepDataStr(gameRoom.saveChessStepDataAsString());
            /*房主*/
            Player player = new Player();
            player.setName(roomMaster);
            player.setGender(gender);
            player.setLevel(level);
            player.setKind(kind);
            player.setRestStepTime(stepTime);
            player.setRestGameTime(gameTime);
            /*建立双向关联关系*/
            player.setUser(user);
            player.setGameRoom(gameRoom);
            gameRoom.getPlayers().add(player);
            /*持久化保存*/
            entityManager.persist(gameRoom);
            entityManager.persist(player);
            em.commit();
            /*获取持久化后的id作为房间的标识符*/
            int id = gameRoom.getId();
            /*同时将数据保存在服务器中*/
            GameSocketUtil.onlineRooms.put(id, gameRoom);

            JSONObject dataJson = new JSONObject();
            dataJson.put("roomID", gameRoom.getId());
            dataJson.put("stepTime", gameRoom.getStepTime());
            dataJson.put("gameTime", gameRoom.getGameTime());
            dataJson.put("roomMasterNickname", player.getName());
            dataJson.put("roomMasterGender", player.getGender());
            dataJson.put("roomMasterLevel", player.getLevel());
            dataJson.put("roomMasterKind", player.getKind());
            dataJson.put("roomMasterImgUrl", player.getUser().getImgUrl());
//            dataJson.put("playerImgUrl", "未知");
//            dataJson.put("playerNickname", "未知");
//            dataJson.put("playerGender", "未知");
//            dataJson.put("playerKind", "未知");
//            dataJson.put("playerLevel", "未知");
            /*只发给自己更新表单*/
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CREATE_GAME_ROOM, "", dataJson);
            GameSocketUtil.onlinePlayers.get(self.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码202
     * 邀请玩家
     *
     * @param data
     * @throws IOException
     */
    public void invite(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        /*获取创建该会话的session，获取点击邀请的房主姓名*/
        User self = (User) this.getHttpSession().getAttribute("user");

        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        String invitedPlayerName = (String) jsonObject.get("invitedPlayer");

        /*寻找被邀请的玩家*/
        for (String onlinePlayer : GameSocketUtil.onlinePlayers.keySet()) {
            GameSocket gameSocket = GameSocketUtil.onlinePlayers.get(onlinePlayer);
            User username = (User) gameSocket.getHttpSession().getAttribute("user");
            /*找到则将消息发送给对方*/
            if (username.getName().equals(invitedPlayerName)) {
                System.out.println(roomID + "号房间的房主" + self.getName() + "邀请了" + invitedPlayerName);

                JSONObject dataJson = new JSONObject();
                dataJson.put("roomMaster", self.getName());
                dataJson.put("roomID", roomID);
                /*异步或同步发送json数据到前端更新房间*/
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_INVITE, "", dataJson);
                gameSocket.getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
        }
    }

    /**
     * 函数码203
     * 是否同意邀请
     *
     * @param data
     * @throws IOException
     */
    public void confirmInvite(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);

        /*获取创建该会话的session对象*/
        User invitedUser = (User) this.getHttpSession().getAttribute("user");

        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        boolean isAgree = (boolean) jsonObject.get("isAgree");

        JSONObject dataJson = new JSONObject();
        /*判断是否同意*/
        if (isAgree) {
            /*找寻房间的信息*/
            /*存储数据到数据库中*/
            EM em = new EM();
            try {
                //数据库查询
                EntityManager entityManager = em.getEntityManager();
                em.begin();
                GameRoom gameRoom = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
                /*添加玩家*/
                Player roomMaster = null;
                Player player = new Player();
                player.setName(invitedUser.getName());
                player.setGender(invitedUser.getGender());
                player.setLevel(invitedUser.getLevel());
                /*根据房主设置被邀请玩家的棋子颜色*/
                for (Player roomMasterPlayer : gameRoom.getPlayers()) {
                    /*查找房主*/
                    if (roomMasterPlayer.getName().equals(gameRoom.getRoomMaster())) {
                        roomMaster = roomMasterPlayer;
                        break;
                    }
                }
                /*如果房主执黑棋，玩家执白棋，反之亦然*/
                if (roomMaster.getKind() == GameSocketUtil.CHESS_BLACK) {
                    player.setKind(GameSocketUtil.CHESS_WHITE);
                } else {
                    player.setKind(GameSocketUtil.CHESS_BLACK);
                }
                player.setRestStepTime(gameRoom.getStepTime());
                player.setRestGameTime(gameRoom.getGameTime());
                gameRoom.setPlayer(invitedUser.getName());
                /*建立双向关联关系*/
                player.setUser(invitedUser);
                player.setGameRoom(gameRoom);
                gameRoom.getPlayers().add(player);
                entityManager.persist(gameRoom);
                entityManager.persist(player);
                /*服务器存储*/
                GameSocketUtil.onlineRooms.put(gameRoom.getId(), gameRoom);
                /*发送消息*/
                dataJson.put("isAgree", true);
                dataJson.put("roomID", gameRoom.getId());
                dataJson.put("stepTime", gameRoom.getStepTime());
                dataJson.put("gameTime", gameRoom.getGameTime());
                dataJson.put("roomMasterNickname", roomMaster.getName());
                dataJson.put("roomMasterGender", roomMaster.getGender());
                dataJson.put("roomMasterLevel", roomMaster.getLevel());
                dataJson.put("roomMasterKind", roomMaster.getKind());
                dataJson.put("roomMasterImgUrl", roomMaster.getUser().getImgUrl());
                dataJson.put("playerNickname", player.getName());
                dataJson.put("playerGender", player.getGender());
                dataJson.put("playerLevel", player.getLevel());
                dataJson.put("playerKind", player.getKind());
                dataJson.put("playerImgUrl", player.getUser().getImgUrl());
                /*将更新的房间的信息发送给两位玩家*/
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_INVITE, "", dataJson);
                System.out.println(invitedUser.getName() + "接受了" + roomMaster.getName() + "的邀请");
                GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                GameSocketUtil.onlinePlayers.get(gameRoom.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                em.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            dataJson.put("isAgree", false);
            dataJson.put("invitedPlayer", invitedUser.getName());
            Player roomMaster = null;
            EM em = new EM();
            try {
                //数据库查询
                EntityManager entityManager = em.getEntityManager();
                em.begin();
                GameRoom gameRoom = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
                /*找到房主*/
                for (Player roomMasterPlayer : gameRoom.getPlayers()) {
                    /*查找房主*/
                    if (roomMasterPlayer.getName().equals(gameRoom.getRoomMaster())) {
                        roomMaster = roomMasterPlayer;
                    }
                }
                /*向房主发送拒绝消息*/
                System.out.println(invitedUser.getName() + "拒绝了" + roomMaster.getName() + "的邀请");
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_INVITE, "", dataJson);
                GameSocketUtil.onlinePlayers.get(roomMaster.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                em.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
    }

    /**
     * 函数码204
     * 踢出玩家
     *
     * @param data
     * @throws IOException
     */
    public void kickOut(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            /*利用hibernate缓存更新gameRoom*/
            GameRoom gameRoom = entityManager
                    .createNamedQuery("findRoomByID", GameRoom.class)
                    .setParameter("id", roomID)
                    .getSingleResult();
            String playerName = gameRoom.getPlayer();

            for (Player gameRoomPlayer : gameRoom.getPlayers()) {
                if (gameRoomPlayer.getName().equals(playerName)) {
                    gameRoom.getPlayers().remove(gameRoomPlayer);
                    gameRoom.setPlayer(null);
                    entityManager.remove(gameRoomPlayer);
                    entityManager.persist(gameRoom);
                    break;
                }
            }
            em.commit();

            JSONObject dataJson = new JSONObject();
            dataJson.put("roomID", roomID);
            dataJson.put("kickOutPlayer", playerName);
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_KICK_OUT, "", dataJson);
            /*告知玩家被踢出房间*/
            GameSocket player = GameSocketUtil.onlinePlayers.get(playerName);
            GameSocket roomMaster = GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster());
            if (player != null) {
                player.getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
            /*房主界面更新*/
            if (roomMaster != null) {
                roomMaster.getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码205
     * 开始游戏
     *
     * @param data
     * @throws IOException
     */
    public void startGame(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        /*持久化*/
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom gameRoom = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            /*设置房间开始*/
            gameRoom.setStatus(GameSocketUtil.GAME_START);
            entityManager.persist(gameRoom);
            /*告诉两位玩家游戏开始*/
            JSONObject dataJson = new JSONObject();
            /*先手*/
            dataJson.put("roomID", roomID);
            for (Player player : gameRoom.getPlayers()) {
                if (player.getKind() == GameSocketUtil.CHESS_BLACK) {
                    dataJson.put("startPlayer", player.getName());
                }
            }
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_START_GAME, "", dataJson);
            GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            GameSocketUtil.onlinePlayers.get(gameRoom.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));

            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码206
     * 落子判断
     * 不连接数据库使用服务器存储的数据进行判断
     *
     * @param data
     * @throws IOException
     */
    public void playGame(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = (int) jsonObject.get("roomID");
        int stepX = (int) jsonObject.get("stepX");
        int stepY = (int) jsonObject.get("stepY");
        int color = (int) jsonObject.get("color");
        int time = (int) jsonObject.get("time");
        /*获取创建该会话的session对象*/
        User self = (User) this.getHttpSession().getAttribute("user");

        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom singleResult = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            singleResult.setChessData(singleResult.transformChessData());
            singleResult.setChessStepData(singleResult.transformChessStepData());
            singleResult.updateChessData(stepX, stepY, color);
            singleResult.updateChessStepData(stepX, stepY, color, time);

            /*判断胜负*/
            boolean win = singleResult.chessJudgement(stepX, stepY, singleResult.getChessData());
            JSONObject dataJson = new JSONObject();
            /*如果下棋者胜利则*/
            if (win) {
                /*房间关闭*/
                singleResult.setStatus(GameSocketUtil.GAME_END);
                /*用户等级加一*/
                User user = entityManager.createNamedQuery("findUserByName", User.class).setParameter("name", self.getName()).getSingleResult();
                user.setLevel(user.getLevel() + 1);
                singleResult.setWinner(user.getName());
                entityManager.persist(user);

                dataJson.put("isWin", true);
                dataJson.put("winner", self.getName());
                dataJson.put("roomID", roomID);
                for (Player player : singleResult.getPlayers()) {
                    JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_PLAY_GAME, "", dataJson);
                    GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                }
            } else {
                dataJson.put("isWin", false);
                dataJson.put("chessPlayer", self.getName());
                dataJson.put("roomID", roomID);
                dataJson.put("stepX", stepX);
                dataJson.put("stepY", stepY);
                dataJson.put("color", color);
                dataJson.put("time", time);
                /*告诉所有玩家落子情况*/
                for (Player player : singleResult.getPlayers()) {
                    JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_PLAY_GAME, "", dataJson);
                    GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                }
            }
            /*保存棋局为字符串*/
            singleResult.setChessDataStr(singleResult.saveChessDataAsString());
            singleResult.setChessStepDataStr(singleResult.saveChessStepDataAsString());
            /*持久化保存棋局*/
            entityManager.persist(singleResult);
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码207
     * 关闭房间
     *
     * @param data
     * @throws IOException
     */
    public void closeRoom(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        /*数据库处理*/
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            /*数据库删除房间*/
            GameRoom gameRoom = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CLOSE_ROOM, roomID + "号码房间关闭了", roomID);
            for (Player player : gameRoom.getPlayers()) {
                GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
            entityManager.remove(gameRoom);
            em.commit();
            /*从服务器中移除房间*/
            GameSocketUtil.onlineRooms.remove(roomID);
            System.out.println(roomID + "号房间关闭了");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码208
     * 认输
     *
     * @param data
     * @throws IOException
     */
    public void giveUp(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");

        EM em = new EM();
        try {
            GameRoom onlineGameRoom = GameSocketUtil.onlineRooms.get(roomID);
            onlineGameRoom.setStatus(GameSocketUtil.GAME_END);
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom findRoomByID = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            User roomMaster = entityManager.createNamedQuery("findUserByName", User.class).setParameter("name", findRoomByID.getRoomMaster()).getSingleResult();
            User player = entityManager.createNamedQuery("findUserByName", User.class).setParameter("name", findRoomByID.getPlayer()).getSingleResult();
            /*胜者等级加一*/
            if (self.getName().equals(roomMaster.getName())) {
                player.setLevel(player.getLevel() + 1);
            } else {
                roomMaster.setLevel(roomMaster.getLevel() + 1);
            }
            findRoomByID.setStatus(GameSocketUtil.GAME_END);
            entityManager.persist(findRoomByID);
            /*判断认输者*/
            if (self.getName().equals(findRoomByID.getRoomMaster())) {
                findRoomByID.setWinner(findRoomByID.getPlayer());
                onlineGameRoom.setWinner(findRoomByID.getPlayer());
            } else {
                findRoomByID.setWinner(findRoomByID.getRoomMaster());
                onlineGameRoom.setWinner(findRoomByID.getRoomMaster());
            }

            JSONObject dataJson = new JSONObject();
            dataJson.put("roomID", roomID);
            dataJson.put("giveUpPlayer", self.getName());
            /*告诉所有玩家某玩家认输*/
            for (Player roomPlayer : findRoomByID.getPlayers()) {
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_GIVE_UP, "", dataJson);
                GameSocketUtil.onlinePlayers.get(roomPlayer.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码209
     * 请求平局
     *
     * @param data
     * @throws IOException
     */
    public void draw(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");

        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        GameRoom gameRoom = GameSocketUtil.onlineRooms.get(roomID);

        JSONObject dataJson = new JSONObject();
        dataJson.put("drawPlayer", self.getName());
        dataJson.put("roomID", roomID);
        JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_DRAW, "", dataJson);
        /*向另一名玩家发送请求平局消息*/
        if (gameRoom.getRoomMaster().equals(self.getName())) {
            GameSocketUtil.onlinePlayers.get(gameRoom.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));
        } else if (gameRoom.getPlayer().equals(self.getName())) {
            GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
        }
    }

    /**
     * 函数码210
     * 是否同意平局
     *
     * @param data
     * @throws IOException
     */
    public void confirmDraw(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = (int) jsonObject.get("roomID");
        boolean isAgree = (boolean) jsonObject.get("isAgree");
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");

        JSONObject dataJson = new JSONObject();
        GameRoom gameRoom = GameSocketUtil.onlineRooms.get(roomID);
        if (isAgree) {
            /*同意平局则保存对局信息*/
            dataJson.put("roomID", roomID);
            dataJson.put("isAgree", true);
            gameRoom.setStatus(GameSocketUtil.GAME_END);
            EM em = new EM();
            try {
                //数据库查询
                EntityManager entityManager = em.getEntityManager();
                em.begin();
                /*保存房间的信息*/
                GameRoom singleResult = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
                /*将数组数据转化成字符串数据进行持久化保存*/
                singleResult.setStatus(GameSocketUtil.GAME_END);
                singleResult.setWinner(null);
                entityManager.persist(singleResult);
                em.commit();

                /*告诉所有玩家游戏平局*/
                for (Player player : singleResult.getPlayers()) {
                    JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_DRAW, "", dataJson);
                    GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        } else {
            /*玩家拒绝平局*/
            dataJson.put("roomID", roomID);
            dataJson.put("isAgree", false);
            dataJson.put("refusePlayer", self.getName());
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_DRAW, "", dataJson);
            /*给请求平局的玩家发送消息*/
            if (self.getName().equals(gameRoom.getRoomMaster())) {
                GameSocketUtil.onlinePlayers.get(gameRoom.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            } else if (self.getName().equals(gameRoom.getPlayer())) {
                GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            }
        }
    }

    /**
     * 函数码211
     * 请求悔棋
     *
     * @param data
     * @throws IOException
     */
    public void regretStep(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");

        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        GameRoom gameRoom = GameSocketUtil.onlineRooms.get(roomID);

        JSONObject dataJson = new JSONObject();
        dataJson.put("regretPlayer", self.getName());
        dataJson.put("roomID", roomID);
        JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_REGRET_STEP, "", dataJson);
        /*向另一名玩家发送请求平局消息*/
        if (gameRoom.getRoomMaster().equals(self.getName())) {
            GameSocketUtil.onlinePlayers.get(gameRoom.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));
        } else if (gameRoom.getPlayer().equals(self.getName())) {
            GameSocketUtil.onlinePlayers.get(gameRoom.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
        }
    }

    /**
     * 函数码212
     * 是否同意悔棋
     *
     * @param data
     * @throws IOException
     */
    public void confirmRegretStep(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = (int) jsonObject.get("roomID");
        boolean isAgree = (boolean) jsonObject.get("isAgree");
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");
        EM em = new EM();
        try {
            //数据库查询
            JSONObject dataJson = new JSONObject();
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom singleResult = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            if (isAgree) {
                int[][] array = singleResult.transformChessData();
                List<Map<String, Integer>> mapList = singleResult.transformChessStepData();
                Map<String, Integer> stringIntegerMap = mapList.get(mapList.size() - 1);
                /*获取最后一步信息*/
                Integer stepX = stringIntegerMap.get("stepX");
                Integer stepY = stringIntegerMap.get("stepY");
                Integer color = stringIntegerMap.get("color");
                Integer time = stringIntegerMap.get("time");
                /*悔棋*/
                singleResult.setChessData(array);
                singleResult.setChessStepData(mapList);
                singleResult.regretStep(-1);
                array[stepX][stepY] = GameSocketUtil.CHESS_NULL;
                /*持久化*/
                singleResult.setChessDataStr(singleResult.saveChessDataAsString());
                singleResult.setChessStepDataStr(singleResult.saveChessStepDataAsString());
                entityManager.persist(singleResult);
                /*发送悔棋消息*/
                dataJson.put("roomID", roomID);
                dataJson.put("isAgree", true);
                dataJson.put("stepX", stepX);
                dataJson.put("stepY", stepY);
                dataJson.put("color", color);
                dataJson.put("time", time);
                dataJson.put("agreePlayer", self.getName());
                /*告诉所有玩家游戏悔棋信息*/
                for (Player player : singleResult.getPlayers()) {
                    JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_REGRET_STEP, "", dataJson);
                    GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                }
            } else {
                /*玩家拒绝悔棋*/
                dataJson.put("roomID", roomID);
                dataJson.put("isAgree", false);
                dataJson.put("refusePlayer", self.getName());
                JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_CONFIRM_REGRET_STEP, "", dataJson);
                /*给请求平局的玩家发送消息*/
                if (self.getName().equals(singleResult.getRoomMaster())) {
                    GameSocketUtil.onlinePlayers.get(singleResult.getPlayer()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                } else if (self.getName().equals(singleResult.getPlayer())) {
                    GameSocketUtil.onlinePlayers.get(singleResult.getRoomMaster()).getSession().getBasicRemote().sendText(String.valueOf(msg));
                }
            }
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * 函数码213
     * 观棋
     *
     * @param data
     * @throws IOException
     */
    public void watchGame(Object data) throws IOException {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int roomID = Integer.parseInt((String) jsonObject.get("roomID"));
        /*获取创建该会话的session*/
        User self = (User) this.getHttpSession().getAttribute("user");
        GameRoom gameRoom = GameSocketUtil.onlineRooms.get(roomID);
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom singleResult = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", roomID).getSingleResult();
            //是否要持久化观战玩家信息? 需要，两名玩家落子信息需要发给房间中的玩家，观战玩家存入房间即可接受玩家下棋信息
            Player player = new Player();
            player.setName(self.getName());
            player.setGender(self.getGender());
            player.setKind(GameSocketUtil.PLAYER_WATCH_GAME);
            player.setLevel(self.getLevel());
            player.setGameRoom(singleResult);
            singleResult.getPlayers().add(player);
            /*持久化保存观战玩家信息*/
            entityManager.persist(player);
            entityManager.persist(singleResult);

            gameRoom.getPlayers().add(player);
            /*将房间对局信息发送给观战玩家*/
            List<Map<String, Integer>> maps = singleResult.transformChessStepData();

            JSONObject dataJson = new JSONObject();
            dataJson.put("roomID", roomID);
            dataJson.put("watchGamePlayer", self.getName());
            dataJson.put("gameList", JSONArray.fromObject(maps));
            JSONObject msg = JsonUtil.setCommonResultJson(GameSocketUtil.FUN_WATCH_GAME, "", dataJson);
            GameSocketUtil.onlinePlayers.get(player.getName()).getSession().getBasicRemote().sendText(String.valueOf(msg));
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
