package restful.entity;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import java.io.Serializable;

/**
 * @author lwz
 * @create 2021-11-24 18:29
 * @description:
 */


@Entity
@Table(name = "t_users")
@NamedQueries({
        @NamedQuery(name = "findAll", query = "SELECT user FROM User user"),
        @NamedQuery(name = "findUserByName", query = "SELECT user FROM User user where user.name = :name"),
        @NamedQuery(name = "findUserByNameLike", query = "SELECT user FROM User user where user.name like :name")
})
public class User implements Serializable {
    private static final long serialVersionUID = -8987463150232288933L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Integer id;
    @FormParam("username")
    @Column(name = "u_name")
    private String name;
    @FormParam("nickname")
    @Column(name = "u_nickname")
    private String nickname;
    @FormParam("password")
    @Column(name = "u_pwd")
    private String pwd;
    @FormParam("gender")
    @Column(name = "u_gender")
    private String gender;
    @FormParam("admin")
    @Column(name = "u_admin")
    private boolean admin;
    @FormParam("level")
    @Column(name = "u_level")
    private int level;
    @FormParam("imgUrl")
    @Column(name = "u_image_url")
    private String imgUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
