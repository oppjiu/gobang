package restful.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;

/**
 * @author lwz
 * @create 2021-11-22 19:54
 * @description: 请求过滤器，过滤每个请求路径
 */
public class PageFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
//        String url = containerRequestContext.getUriInfo().getPath();
//        System.out.println(url + " caught by ContainerRequestContext");
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
//        String url = containerRequestContext.getUriInfo().getPath();
//        System.out.println(url + " caught by containerResponseContext");
    }
}
