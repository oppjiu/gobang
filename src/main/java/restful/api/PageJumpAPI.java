package restful.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.annotation.GobangPower;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * @author lwz
 * @create 2021-11-22 19:28
 * @description: 页面跳转API
 * 给iframe设置src实现页面跳转功能需要使用@GET ?
 */
@Path("/gobang")
public class PageJumpAPI {
    @Context
    private HttpServletRequest request;

    //导航栏页面跳转
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/homePage")
    public Response jump2HomePage() {
        return Response.temporaryRedirect(URI.create("/jsp/homePage.jsp")).build();
    }

    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/registerPage")
    public Response jump2RegisterPage() {
        return Response.temporaryRedirect(URI.create("/jsp/registerPage.jsp")).build();
    }

    @GobangPower(value = {GobangPower.AUTO_LOGIN})
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/loginPage")
    public Response jump2LoginPage() {
        return Response.temporaryRedirect(URI.create("/jsp/loginPage.jsp")).build();
    }

    /**
     * 跳转至管理用户页面，需要登录和管理员权限
     */
    @GobangPower(value = {GobangPower.LOGIN, GobangPower.ADMIN})
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/userManagePage")
    public Response jump2UserManagePage() {
        return Response.temporaryRedirect(URI.create("/jsp/userManagePage.jsp")).build();
    }

    @GobangPower(GobangPower.LOGIN)
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/gamePage")
    public Response jump2GamePage() {
        return Response.temporaryRedirect(URI.create("/jsp/gamePage.jsp")).build();
    }

    @GobangPower(GobangPower.LOGIN)
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/watchGamePage")
    public Response jump2WatchGamePage() {
        return Response.temporaryRedirect(URI.create("/jsp/watchGamePage.jsp")).build();
    }

    @GobangPower({GobangPower.LOGIN})
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/gameVideoPage")
    public Response jump2GameVideoPage() {
        return Response.temporaryRedirect(URI.create("/jsp/gameVideoPage.jsp")).build();
    }

    @GobangPower({GobangPower.LOGIN})
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/userInformationPage")
    public Response jump2UserInformationPage() {
        return Response.temporaryRedirect(URI.create("/jsp/userInformationPage.jsp")).build();
    }

    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/errorPage404")
    public Response jump2ErrorPage404() {
        return Response.temporaryRedirect(URI.create("/jsp/404.jsp")).build();
    }


    //临时页面
    @GobangPower(GobangPower.LOGIN)
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/chatRoomPage")
    public Response jump2ChatRoomPage() {
        return Response.temporaryRedirect(URI.create("/jsp/chatRoomPage.jsp")).build();
    }

    @GobangPower(GobangPower.LOGIN)
    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("/welcomePage")
    public Response jump2WelcomePage() {
        return Response.temporaryRedirect(URI.create("/jsp/welcomePage.jsp")).build();
    }
}
