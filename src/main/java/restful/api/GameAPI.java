package restful.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.annotation.GobangPower;
import restful.bean.CommonResult;
import restful.database.EM;
import restful.entity.GameRoom;
import restful.entity.Player;
import restful.entity.User;
import restful.utils.GameSocketUtil;
import restful.utils.JsonUtil;
import restful.websocket.GameSocket;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lwz
 * @create 2021-12-07 22:50
 * @description:
 */
@Path("/gobang")
public class GameAPI {
    @Context
    private HttpServletRequest request;

    /**
     * 查找存在该玩家的房间，或者是开始对局的房间
     *
     * @param name
     * @return
     */
    @GobangPower({GobangPower.LOGIN})
    @POST
    @Produces("application/json;charset=UTF-8")
    @Path("/getMyRoom")
    public CommonResult getMyRoom(@FormParam("name") String name) {
        JSONArray jsonArray = new JSONArray();
        /*从数据库中查询所有房间的信息*/
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<GameRoom> findRoomByID = entityManager.createNamedQuery("findAllRooms", GameRoom.class).getResultList();
            for (GameRoom gameRoom : findRoomByID) {
                /*用户是房主和玩家的房间，观战者不算*/
                if (gameRoom.getRoomMaster().equals(name) || gameRoom.getPlayer().equals(name)) {
                    JSONObject dataJson = new JSONObject();
                    for (Player player : gameRoom.getPlayers()) {
                        if (player.getName().equals(gameRoom.getRoomMaster())) {  //是房主
                            dataJson.put("roomMasterNickname", player.getName());
                            dataJson.put("roomMasterGender", player.getGender());
                            dataJson.put("roomMasterLevel", player.getLevel());
                            dataJson.put("roomMasterKind", player.getKind());
                            dataJson.put("roomMasterImgUrl", player.getUser().getImgUrl());
                        } else if (player.getName().equals(gameRoom.getPlayer())) {   //是玩家
                            dataJson.put("playerNickname", player.getName());
                            dataJson.put("playerGender", player.getGender());
                            dataJson.put("playerLevel", player.getLevel());
                            dataJson.put("playerKind", player.getKind());
                            dataJson.put("playerImgUrl", player.getUser().getImgUrl());
                        }
                    }
                    dataJson.put("roomID", gameRoom.getId());
                    dataJson.put("stepTime", gameRoom.getStepTime());
                    dataJson.put("gameTime", gameRoom.getGameTime());
                    dataJson.put("status", gameRoom.getStatus());

                    /*同时同步到服务器中*/
                    GameSocketUtil.onlineRooms.put(gameRoom.getId(), gameRoom);
                    jsonArray.add(dataJson);
                }
            }
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", jsonArray);
    }

    /**
     * 获取其他在线用户
     *
     * @return CommonResult类型的json
     */
    @POST
    @Path("/getOnlinePlayers")
    @Produces("application/json;charset=UTF-8")
    public CommonResult getOnlinePlayers() {
        JSONArray jsonArray = new JSONArray();
        Map<String, GameSocket> onlinePlayers = GameSocketUtil.onlinePlayers;
        User self = (User) request.getSession().getAttribute("user");

        for (String onlineUserName : onlinePlayers.keySet()) {
            //除了自己的其他用户
            if (!onlineUserName.equals(self.getName())) {
                User user = (User) onlinePlayers.get(onlineUserName).getHttpSession().getAttribute("user");
                jsonArray.add(JSONObject.fromObject(user));
            }
        }
        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", jsonArray);
    }

    /**
     * 邀请列表模糊查询其他用户
     *
     * @return CommonResult类型的json
     */
    @POST
    @Path("/findOnlinePlayers")
    @Produces("application/json;charset=UTF-8")
    public CommonResult findOnlinePlayers(@FormParam("username") String username) {
        JSONArray jsonArray = new JSONArray();
        Map<String, GameSocket> onlinePlayers = GameSocketUtil.onlinePlayers;
        User self = (User) request.getSession().getAttribute("user");


        for (String onlineUserName : onlinePlayers.keySet()) {
            /*使用正则表达式进行模糊查询*/
            Pattern pattern = Pattern.compile(username);
            Matcher matcher = pattern.matcher(onlineUserName);
            /*字符和容器中的名字的字符相匹配而且该名字不是玩家自身*/
            if (matcher.find() && !onlineUserName.equals(self.getName())) {
                User user = (User) onlinePlayers.get(onlineUserName).getHttpSession().getAttribute("user");
                jsonArray.add(JSONObject.fromObject(user));
                System.out.println("通过字符:="+username+"= 模糊查询到了:="+user.getName()+"=");
            }
        }
        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", jsonArray);
    }

    /**
     * 获取正在对战的房间
     *
     * @return CommonResult类型的json
     */
    @GobangPower({GobangPower.LOGIN})
    @POST
    @Produces("application/json;charset=UTF-8")
    @Path("/getPlayingRoom")
    public CommonResult getPlayingRoom() {
        JSONArray jsonArray = new JSONArray();
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<GameRoom> findAllRooms = entityManager.createNamedQuery("findAllRooms", GameRoom.class).getResultList();
            for (GameRoom findAllRoom : findAllRooms) {
                if (findAllRoom.getStatus() == GameSocketUtil.GAME_START) {
                    JSONObject dataJson = new JSONObject();
                    for (Player player : findAllRoom.getPlayers()) {

                        if (player.getName().equals(findAllRoom.getRoomMaster())) {  //是房主
                            dataJson.put("roomMasterNickname", player.getName());
                            dataJson.put("roomMasterGender", player.getGender());
                            dataJson.put("roomMasterLevel", player.getLevel());
                            dataJson.put("roomMasterKind", player.getKind());
                            dataJson.put("roomMasterImgUrl", player.getUser().getImgUrl());
                        } else if (player.getName().equals(findAllRoom.getPlayer())) {   //是玩家
                            dataJson.put("playerNickname", player.getName());
                            dataJson.put("playerGender", player.getGender());
                            dataJson.put("playerLevel", player.getLevel());
                            dataJson.put("playerKind", player.getKind());
                            dataJson.put("playerImgUrl", player.getUser().getImgUrl());
                        }
                    }
                    dataJson.put("roomID", findAllRoom.getId());
                    dataJson.put("stepTime", findAllRoom.getStepTime());
                    dataJson.put("gameTime", findAllRoom.getGameTime());
                    jsonArray.add(dataJson);
                }
            }
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", jsonArray);

    }

    /**
     * 查找存在该玩家的房间，并且是对局结束的房间
     *
     * @param name
     * @return
     */
    @GobangPower({GobangPower.LOGIN})
    @POST
    @Produces("application/json;charset=UTF-8")
    @Path("/getPlayedRoom")
    public CommonResult getPlayedRoom(@FormParam("name") String name) {
        JSONArray jsonArray = new JSONArray();
        /*从数据库中查询所有房间的信息*/
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<GameRoom> findRoomByID = entityManager.createNamedQuery("findAllRooms", GameRoom.class).getResultList();
            for (GameRoom gameRoom : findRoomByID) {
                /*查找存在该玩家的房间，并且是对局结束的房间*/
                if ((gameRoom.getRoomMaster().equals(name) || gameRoom.getPlayer().equals(name)) && gameRoom.getStatus() == GameSocketUtil.GAME_END) {
                    JSONObject dataJson = new JSONObject();
                    for (Player player : gameRoom.getPlayers()) {
                        if (player.getName().equals(gameRoom.getRoomMaster())) {  //是房主
                            dataJson.put("roomMasterNickname", player.getName());
                            dataJson.put("roomMasterGender", player.getGender());
                            dataJson.put("roomMasterLevel", player.getLevel());
                            dataJson.put("roomMasterKind", player.getKind());
                            dataJson.put("roomMasterImgUrl", player.getUser().getImgUrl());
                        } else if (player.getName().equals(gameRoom.getPlayer())) {   //是玩家
                            dataJson.put("playerNickname", player.getName());
                            dataJson.put("playerGender", player.getGender());
                            dataJson.put("playerLevel", player.getLevel());
                            dataJson.put("playerKind", player.getKind());
                            dataJson.put("playerImgUrl", player.getUser().getImgUrl());
                        }
                    }
                    dataJson.put("roomID", gameRoom.getId());
                    dataJson.put("stepTime", gameRoom.getStepTime());
                    dataJson.put("gameTime", gameRoom.getGameTime());

                    /*同时同步到服务器中*/
                    GameSocketUtil.onlineRooms.put(gameRoom.getId(), gameRoom);
                    jsonArray.add(dataJson);
                }
            }
            em.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", jsonArray);
    }

    /**
     * 获取复盘房间对局数据
     *
     * @param roomID
     * @return
     */
    @GobangPower({GobangPower.LOGIN})
    @POST
    @Produces("application/json;charset=UTF-8")
    @Path("/getPlayedGameStepData")
    public CommonResult getPlayedGameStepData(@FormParam("roomID") String roomID) {
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            GameRoom singleResult = entityManager.createNamedQuery("findRoomByID", GameRoom.class).setParameter("id", Integer.parseInt(roomID)).getSingleResult();
            List<Map<String, Integer>> maps = singleResult.transformChessStepData();

            JSONObject dataJson = new JSONObject();
            dataJson.put("winner", singleResult.getWinner());
            dataJson.put("gameStepData", maps);
            em.commit();
            return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", dataJson);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }
}
