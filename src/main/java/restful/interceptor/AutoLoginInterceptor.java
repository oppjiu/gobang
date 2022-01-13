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
import restful.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

/**
 * @author lwz
 * @create 2021-11-22 19:40
 * @description: 自动登录监听
 */
public class AutoLoginInterceptor implements PreProcessInterceptor {
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethodInvoker resourceMethodInvoker) throws Failure, WebApplicationException {
        Method method = resourceMethodInvoker.getMethod();
        if (method.isAnnotationPresent(GobangPower.class)) {
            GobangPower annotation = method.getAnnotation(GobangPower.class);
            int[] boxPowerValues = annotation.value();
            for (int boxPowerValue : boxPowerValues) {
                if (boxPowerValue == GobangPower.AUTO_LOGIN) {
                    System.out.println("拦截到登录页");
                    //拦截到登录页
                    try {
                        HttpSession session = request.getSession();
                        User user = (User) session.getAttribute("user");
                        /*验证是否自动登录*/
                        if (user != null) {
                            System.out.println("AutoLoginInterceptor: 自动登录成功");
                            /*status设置为307重定向，所以前端使用error接收参数，result.responseText*/
                            return new ServerResponse("autoLogin", 307, new Headers<>());
                        }
                        System.out.println("AutoLoginInterceptor: 自动登录失败，需要登录");
                    } catch (NullPointerException e) {
                        System.out.println("AutoLoginInterceptor: 未登录");
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
