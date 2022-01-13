package restful.entity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import restful.utils.GameSocketUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author lwz
 * @create 2021-12-07 22:23
 * @description:
 */

@Entity
@Table(name = "t_game_rooms")
@NamedQueries({
        @NamedQuery(name = "findAllRooms", query = "SELECT gameRoom FROM GameRoom gameRoom"),
        @NamedQuery(name = "findRoomByID", query = "SELECT gameRoom FROM GameRoom gameRoom where gameRoom.id = :id"),
})
public class GameRoom implements Serializable {
    private static final long serialVersionUID = -4374824379609313052L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "g_id")
    private int id;
    @Column(name = "g_roomMaster")
    private String roomMaster;
    @Column(name = "g_player")
    private String player;
    @Column(name = "g_stepTime")
    private int stepTime;
    @Column(name = "g_gameTime")
    private int gameTime;
    /*默认为未开始*/
    @Column(name = "g_status")
    private int status = GameSocketUtil.GAME_WAIT;
    @Column(name = "g_winner")
    private String winner;
    /*专门存储棋盘数据字符串*/
    @Column(name = "g_chessStepData")
    private String chessStepDataStr;
    /*专门存储落子信息的字符串*/
    @Column(name = "g_chessData")
    private String chessDataStr;
    @OneToMany(targetEntity = Player.class, mappedBy = "gameRoom", cascade = CascadeType.ALL)
    private final Set<Player> players = new HashSet<>();

    /*记录棋盘数据和落子信息*/
    @Transient
    private int[][] chessData;
    @Transient
    private List<Map<String, Integer>> chessStepData = new ArrayList<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomMaster() {
        return roomMaster;
    }

    public void setRoomMaster(String roomMaster) {
        this.roomMaster = roomMaster;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getStepTime() {
        return stepTime;
    }

    public void setStepTime(int stepTime) {
        this.stepTime = stepTime;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getChessStepDataStr() {
        return chessStepDataStr;
    }

    public void setChessStepDataStr(String chessStepDataStr) {
        this.chessStepDataStr = chessStepDataStr;
    }

    public String getChessDataStr() {
        return chessDataStr;
    }

    public void setChessDataStr(String chessDataStr) {
        this.chessDataStr = chessDataStr;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public int[][] getChessData() {
        return chessData;
    }

    public void setChessData(int[][] chessData) {
        this.chessData = chessData;
    }

    public List<Map<String, Integer>> getChessStepData() {
        return chessStepData;
    }

    public void setChessStepData(List<Map<String, Integer>> chessStepData) {
        this.chessStepData = chessStepData;
    }

    /**
     * @param x 棋盘x轴大小
     * @param y 棋盘y轴大小
     */
    public void initChessData(int x, int y) {
        this.chessData = new int[x][y];
    }

    /**
     * @param x     x轴坐标
     * @param y     y轴坐标
     * @param color 棋子颜色
     */
    public void updateChessData(int x, int y, int color) {
        this.chessData[x][y] = color;
    }

    /**
     * @param time  每步棋下的时间
     * @param x     每一步的落子x轴坐标
     * @param y     每一步的落子y轴坐标
     * @param color 落子颜色
     */
    public void updateChessStepData(int x, int y, int color, int time) {
        Map<String, Integer> map = new HashMap<>();
        map.put("stepX", x);
        map.put("stepY", y);
        map.put("color", color);
        map.put("time", time);
        this.chessStepData.add(map);
    }

    /**
     * 如果传入的index为-1则默认删除尾部元素
     * 如果传入的index为0~chessSteps.size()则删除index处的元素
     *
     * @param index 索引
     */
    public void regretStep(int index) {
        if (index == -1) {
            /*当棋盘有棋的时候悔棋*/
            if (this.chessStepData.size() >= 1) {
                this.chessStepData.remove(this.chessStepData.size() - 1);
            }
        } else {
            this.chessStepData.remove(index);
        }
    }

    /**
     * 将ChessData转为字符串
     *
     * @return String类型的ChessData
     */
    public String saveChessDataAsString() {
        return JSONArray.fromObject(this.chessData).toString();
    }

    /**
     * 将ChessStepData转为字符串
     *
     * @return String类型的
     */
    public String saveChessStepDataAsString() {
        return JSONArray.fromObject(this.chessStepData).toString();
    }

    /**
     * 将ChessData的字符串转为ChessData
     *
     * @return int[][]类型的ChessData
     */
    public int[][] transformChessData() {
        int[][] array = new int[0][];

        Object[] lists = JSONArray.fromObject(this.chessDataStr).toArray();
        for (int i = 0; i < lists.length; i++) {
            Object[] list = JSONArray.fromObject(lists[i]).toArray();
            for (int j = 0; j < list.length; j++) {
                if (i == 0 && j == 0) {
                    array = new int[lists.length][list.length];
                }
                array[i][j] = (int) list[j];
            }
        }
        return array;
    }

    /**
     * 将ChessStepData的字符串转为ChessStepData
     *
     * @return List<Map < String, Integer>>类型的ChessStepData
     */
    public List<Map<String, Integer>> transformChessStepData() {
        ArrayList<Map<String, Integer>> arrayListMap = new ArrayList<>();
        for (Object list : JSONArray.fromObject(this.chessStepDataStr)) {
            JSONObject jsonMap = JSONObject.fromObject(list);
            Map<String, Integer> map = new HashMap<>();
            map.put("stepX", (Integer) jsonMap.get("stepX"));
            map.put("stepY", (Integer) jsonMap.get("stepY"));
            map.put("color", (Integer) jsonMap.get("color"));
            map.put("time", (Integer) jsonMap.get("time"));
            arrayListMap.add(map);
        }
        return arrayListMap;
    }

    /**
     * 判断输赢算法
     *
     * @param x
     * @param y
     * @param chessArray
     * @return
     */
    public boolean chessJudgement(int x, int y, int[][] chessArray) {
        int count;
        boolean flag;
        int max = 0;
        int tempXIndex = x;
        int tempYIndex = y;

        // 三维数组记录横向，纵向，左斜，右斜的移动
        int[][][] dir = new int[][][]{
                // 横向
                {{-1, 0}, {1, 0}},
                // 竖着
                {{0, -1}, {0, 1}},
                // 左斜
                {{-1, -1}, {1, 1}},
                // 右斜
                {{1, -1}, {-1, 1}}};
        for (int i = 0; i < 4; i++) {
            count = 1;
            //j为0,1分别为棋子的两边方向，比如对于横向的时候，j=0,表示下棋位子的左边，j=1的时候表示右边
            for (int j = 0; j < 2; j++) {
                flag = true;
                /**
                 while语句中为一直向某一个方向遍历
                 有相同颜色的棋子的时候，Count++
                 否则置flag为false，结束该该方向的遍历
                 **/
                while (flag) {
                    tempXIndex = tempXIndex + dir[i][j][0];
                    tempYIndex = tempYIndex + dir[i][j][1];

                    //这里加上棋盘大小的判断，这里我设置的棋盘大小为16 具体可根据实际情况设置 防止越界
                    if (tempXIndex >= 0 && tempXIndex < 16 && tempYIndex >= 0 && tempYIndex < 16) {
                        if ((chessArray[tempXIndex][tempYIndex] == chessArray[x][y])) {
                            count++;
                        } else
                            flag = false;
                    } else {
                        flag = false;
                    }
                }
                tempXIndex = x;
                tempYIndex = y;
            }

            if (count >= 5) {
                max = 1;
                break;
            } else
                max = 0;
        }
        if (max == 1)
            return true;
        else
            return false;
    }
}