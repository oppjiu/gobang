package restful.interceptor;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.annotation.GobangPower;
import restful.database.EM;
import restful.entity.User;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

/**
 * @author lwz
 * @create 2021-11-25 14:23
 * @description: 拦截未登录和需管理员身份
 */
public class LoginInterceptor implements PreProcessInterceptor {
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethodInvoker resourceMethodInvoker) throws Failure, WebApplicationException {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String htmlStr = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>五子棋-404</title>\n" +
                "    <style>\n" +
                "        img{\n" +
                "            position: absolute;\n" +
                "            top: 50%;\n" +
                "            left: 50%;\n" +
                "            transform: translate(-50%, -50%);\n" +
                "        }\n" +
                "\n" +
                "        body{\n" +
                "            background-color: white;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<img src=\"" + basePath + "/images/system/404-image.png\" alt=\"404没有权限\">\n" +
                "</body>\n" +
                "</html>";

        Method method = resourceMethodInvoker.getMethod();
        if (method.isAnnotationPresent(GobangPower.class)) {
            GobangPower annotation = method.getAnnotation(GobangPower.class);
            int[] boxPowerValues = annotation.value();
            for (int boxPowerValue : boxPowerValues) {
                //拦截需要登录访问的页面
                if (boxPowerValue == GobangPower.LOGIN) {
                    System.out.println("拦截到需要登录访问页面");
                    //如果没有登录则
                    User user = (User) request.getSession().getAttribute("user");
                    if (user == null) {
                        System.out.println("没有登录无权访问");
                        return new ServerResponse(htmlStr, 404, new Headers<>());
                    }
                }
                if (boxPowerValue == GobangPower.ADMIN) {    //拦截需要管理员权限访问的页面
                    System.out.println("拦截到需要管理员权限访问的页面");
                    EM em = new EM();
                    try {
                        User user = (User) request.getSession().getAttribute("user");
                        if (user != null) {
                            EntityManager entityManager = em.getEntityManager();
                            em.begin();
                            User u = entityManager.createNamedQuery("findUserByName", User.class)
                                    .setParameter("name", user.getName())
                                    .getSingleResult();
                            em.commit();
                            //用户是否是管理员，不是则跳转
                            if (!u.isAdmin()) {
                                System.out.println("非管理员用户无权访问");
                                return new ServerResponse(htmlStr, 404, new Headers<>());
//                                response.sendRedirect(request.getContextPath() + "/gobang/noPermissionPage");
                            }
                        } else {
                            System.out.println("没有登录无权访问");
                            return new ServerResponse(htmlStr, 404, new Headers<>());
//                            response.sendRedirect(request.getContextPath() + "/gobang/noPermissionPage");
                        }
                    } finally {
                        em.close();
                    }
                }
            }
        }
        return null;
    }
}
