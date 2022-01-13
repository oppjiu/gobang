package restful.api;

import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.bean.CommonResult;
import restful.database.EM;
import restful.entity.User;
import restful.utils.GameSocketUtil;
import restful.utils.JsonUtil;
import restful.websocket.GameSocket;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

/**
 * @author lwz
 * @create 2021-11-18 22:26
 * @description: 有关用户的API
 */
@Path("/gobang")
public class UserAPI {
    @Context
    HttpServletRequest request;
    @Context
    HttpServletResponse response;

    /**
     * 用户登录
     *
     * @param user              用户表单信息 用户名，密码
     * @param rememberFormValue 记住我选项
     * @param validateCode      验证码
     * @return CommonResult类型的json
     */
    @POST
    @Path("/login")
    @Produces("application/json;charset=UTF-8")
    public CommonResult login(@Form User user,
                              @FormParam("rememberForm") String rememberFormValue,
                              @FormParam("validateCode") String validateCode) {
        //获取验证码
        String code = (String) request.getSession().getAttribute("validateCode");
        System.out.println("前端的验证码："+validateCode+" 后端的验证码："+code);
        //TODO 恢复暂停的验证码功能
//        //忽略大小写验证码不匹配则返回
//        if (!code.equalsIgnoreCase(validateCode)) {
//            return JsonUtil.setCommonResult(CommonResult.CODE_VALIDATE_CODE_ERROR, "", null);
//        }

        //获取登录用户
        Map<String, GameSocket> onlineUsers = GameSocketUtil.onlinePlayers;
        for (String onlineUser : onlineUsers.keySet()) {
            //如果已经登录
            if (onlineUser.equals(user.getName())) {
                return JsonUtil.setCommonResult(CommonResult.CODE_HAS_LOGIN_ERROR, "", null);
            }
        }

        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<User> userList = entityManager.createNamedQuery("findAll", User.class).getResultList();
            em.commit();
            for (User userData : userList) {
                //用户名和密码匹配成功
                if (userData.getName().equals(user.getName()) && userData.getPwd().equals(user.getPwd())) {
                    //设置cookie
                    Cookie usernameCookie = new Cookie("username", user.getName());
                    Cookie rememberFormCookie = new Cookie("rememberForm", rememberFormValue);
                    usernameCookie.setMaxAge(60 * 60 * 24 * 30);
                    rememberFormCookie.setMaxAge(60 * 60 * 24 * 30);
                    usernameCookie.setPath("/");
                    rememberFormCookie.setPath("/");
                    response.addCookie(usernameCookie);
                    response.addCookie(rememberFormCookie);
                    //将数据库读取的user对象存入session中
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", userData);
                    System.out.println(userData.getName()+"登录成功");
                    System.out.println("用户信息为"+userData);

                    return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
                }
            }

            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * 注册
     * 确认密码在前端处理
     *
     * @param user 用户表单信息 用户名，昵称，密码，验证码
     * @return CommonResult类型的json
     */
    @POST
    @Path("/register")
    @Produces("application/json;charset=UTF-8")
    public CommonResult register(@Form User user) {
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<User> userList = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", user.getName()).getResultList();
            for (User u : userList) {
                //如果重名则注册失败
                if (u.getName().equals(user.getName())) {
                    return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
                }
            }
            //如果没有重名则注册成功
            //持久化user对象
            user.setImgUrl("http://localhost:8080/images/system/默认头像.png");
            entityManager.persist(user);
            em.commit();

            return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
        } catch (Exception e) {
            e.printStackTrace();

            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * 注销
     *
     * @return CommonResult类型的json
     */
    @POST
    @Path("/logout")
    @Produces("application/json;charset=UTF-8")
    public CommonResult logout() {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            //如果已经登录，则删除session
            if (user != null) {
                for (String username : GameSocketUtil.onlinePlayers.keySet()) {
                    if (user.getName().equals(username)) {
                        session.removeAttribute("user");
                        GameSocketUtil.onlineUserLogout(user);
                        System.out.println(user.getName()+"用户已登出");
                        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
                    }
                }
            }
            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        }
    }

    /**
     * 获取用户密码
     *
     * @param user 用户表单信息, 密码
     * @return CommonResult类型的json
     */
    @POST
    @Path("/getUserInfoAuto")
    @Produces("application/json;charset=UTF-8")
    public CommonResult getUserInfoAuto(@Form User user) {
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            User u = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", user.getName())
                    .getSingleResult();
            em.commit();
            if (u != null) {
                if (u.getName().equals(user.getName())) {
                    return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", u);
                }
            }
            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * @param username    用户名
     * @param password    密码
     * @param newPassword 新密码
     * @return CommonResult类型的json
     */
    @POST
    @Path("/modifyUserPwd")
    @Produces("application/json;charset=UTF-8")
    public CommonResult modifyUserPwd(@FormParam("username") String username,
                                      @FormParam("password") String password,
                                      @FormParam("newPassword") String newPassword) {
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
            if (modifyUser != null) {
                if (modifyUser.getName().equals(username)) {
                    //如果输入密码正确
                    if (password.equals(modifyUser.getPwd())) {
                        //将新密码存储到持久态
                        modifyUser.setPwd(newPassword);
                        entityManager.persist(modifyUser);
                        em.commit();
                        return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
                    }
                }
            }
            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * 修改用户信息
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param gender   性别
     * @param isAdmin  权限
     * @return CommonResult类型的json
     */
    @POST
    @Path("/modifyUserInfo")
    @Produces("application/json;charset=UTF-8")
    public CommonResult modifyUserInfo(@FormParam("username") String username,
                                       @FormParam("nickname") String nickname,
                                       @FormParam("gender") String gender,
                                       @FormParam("isAdmin") boolean isAdmin) {
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
            if (modifyUser != null) {
                if (modifyUser.getName().equals(username)) {
                    //将前端传来的信息存储到持久态
                    modifyUser.setNickname(nickname);
                    modifyUser.setGender(gender);
                    modifyUser.setAdmin(isAdmin);
                    entityManager.persist(modifyUser);
                    em.commit();
                    return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
                }
            }
            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * 删除用户
     *
     * @param user 表单数据
     * @return CommonResult类型的json
     */
    @POST
    @Path("/deleteUser")
    @Produces("application/json;charset=UTF-8")
    public CommonResult deleteUser(@Form User user) {
        User self = (User) request.getSession().getAttribute("user");
        //如果删除的对象是自己
        if (self.getName().equals(user.getName())) {
            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "不能删除自己", null);
        }

        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<User> userList = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", user.getName())
                    .getResultList();
            for (User modifyUser : userList) {
                if (modifyUser.getName().equals(user.getName())) {
                    //删除用户
                    entityManager.remove(modifyUser);
                    em.commit();
                    return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
                }
            }

            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * @param username 用户名
     * @return CommonResult类型的json，键值data为user对象
     */
    @POST
    @Path("/getSelfInfo")
    @Produces("application/json;charset=UTF-8")
    public CommonResult getSelfInfo(@FormParam("username") String username) {
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", username)
                    .getSingleResult();
            em.commit();
            if (modifyUser != null) {
                return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", modifyUser);
            } else {
                return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }

    /**
     * 查询数据库中所有用户的信息
     *
     * @return CommonResult类型的json，键值data为user对象
     */
    @POST
    @Path("/findAllUsers")
    @Produces("application/json;charset=UTF-8")
    public CommonResult findAllUsers() {
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<User> userList = entityManager.createNamedQuery("findAll", User.class).getResultList();
            em.commit();
            return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", userList);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }
}
