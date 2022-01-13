package restful.utils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.bean.CommonResult;
import restful.entity.GameRoom;
import restful.entity.User;
import restful.websocket.GameSocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwz
 * @create 2021-12-07 22:27
 * @description:
 */
public class GameSocketUtil {
    /**
     * GameSocket方法码
     */
    public final static int FUN_CHAT = 200;
    public final static int FUN_CREATE_GAME_ROOM = 201;
    public final static int FUN_INVITE = 202;
    public final static int FUN_CONFIRM_INVITE = 203;
    public final static int FUN_KICK_OUT = 204;
    public final static int FUN_START_GAME = 205;
    public final static int FUN_PLAY_GAME = 206;
    public final static int FUN_CLOSE_ROOM = 207;
    public final static int FUN_GIVE_UP = 208;
    public final static int FUN_DRAW = 209;
    public final static int FUN_CONFIRM_DRAW = 210;
    public final static int FUN_REGRET_STEP = 211;
    public final static int FUN_CONFIRM_REGRET_STEP = 212;
    public final static int FUN_WATCH_GAME = 213;

    /**
     * 玩家类型和棋子类型
     */
    public final static int CHESS_NULL = 0;
    public final static int CHESS_BLACK = 1;
    public final static int CHESS_WHITE = 2;
    public final static int PLAYER_WATCH_GAME = 3;

    /**
     * 房间状态码
     */
    public final static int GAME_WAIT = 101;
    public final static int GAME_START = 102;
    public final static int GAME_END = 103;

    /**
     * onlineRooms用于存储在线房间
     * onlineGameSockets用于存储websocket
     */
    public static final Map<Integer, GameRoom> onlineRooms = new ConcurrentHashMap<>();
    public static final Map<String, GameSocket> onlinePlayers = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(GameSocketUtil.class);

    /**
     * 用户登录广播
     *
     * @param user 用户
     * @throws IOException
     */
    public static void onlineUserLogin(User user) throws IOException {
        //将消息推送给所有的客户端
        for (String onlinePlayerName : GameSocketUtil.onlinePlayers.keySet()) {
            //除了自己
            if (!onlinePlayerName.equals(user.getName())) {
                log.info("{}收到广播：{}上线了", onlinePlayerName, user.getName());

                JSONObject jsonObject = JsonUtil.setCommonResultJson(CommonResult.CODE_SUCCESS, user.getName() + "已上线", null);
                //异步或同步发送json数据到前端
                GameSocketUtil.onlinePlayers.get(onlinePlayerName).getSession().getBasicRemote().sendText(String.valueOf(jsonObject));
            }
        }
    }

    /**
     * 用户登出广播
     *
     * @param user 用户
     * @throws IOException
     */
    public static void onlineUserLogout(User user) throws IOException {
        GameSocketUtil.onlinePlayers.remove(user.getName());
        log.info("{}已离线", user);

        /*广播*/
        for (String onlinePlayerName : GameSocketUtil.onlinePlayers.keySet()) {
            JSONObject jsonObject = JsonUtil.setCommonResultJson(CommonResult.CODE_SUCCESS, user.getName() + "已离线", null);
            //异步或同步发送json数据到前端
            GameSocketUtil.onlinePlayers.get(onlinePlayerName).getSession().getBasicRemote().sendText(String.valueOf(jsonObject));
        }
    }
}
