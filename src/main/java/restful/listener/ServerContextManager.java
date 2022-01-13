package restful.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.database.EM;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author lwz
 * @create 2021-11-24 22:09
 * @description: 监听服务器启动和关闭
 */
public class ServerContextManager implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("#####服务器启动#####");
        //预加载数据库驱动连接
        EM em = new EM();
        em.close();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("#####服务器关闭#####");
    }
}
