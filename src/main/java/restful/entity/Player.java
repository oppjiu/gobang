package restful.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lwz
 * @create 2021-12-07 22:42
 * @description:
 */

@Entity
@Table(name = "t_players")
@NamedQueries({
        @NamedQuery(name = "findAllPlayers", query = "SELECT player FROM Player player"),
        @NamedQuery(name = "findPlayerByNameWithRoomID", query = "SELECT player FROM Player player where player.name = :name and player.gameRoom.id = :id"),
        @NamedQuery(name = "findPlayerByNameLikeWithRoomID", query = "SELECT player FROM Player player where player.name like :name and player.gameRoom.id = :id ")
})
public class Player implements Serializable {
    private static final long serialVersionUID = -8286781702844938089L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private int id;
    @Column(name = "p_name")
    private String name;
    @Column(name = "p_gender")
    private String gender;
    @Column(name = "p_kind")
    private int kind;
    @Column(name = "p_level")
    private int level;
    @Column(name = "p_restStepTime")
    private int restStepTime;
    @Column(name = "p_restGameTime")
    private int restGameTime;

    @ManyToOne(targetEntity = GameRoom.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "gameRoom_id")
    private GameRoom gameRoom;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRestStepTime() {
        return restStepTime;
    }

    public void setRestStepTime(int restStepTime) {
        this.restStepTime = restStepTime;
    }

    public int getRestGameTime() {
        return restGameTime;
    }

    public void setRestGameTime(int restGameTime) {
        this.restGameTime = restGameTime;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
