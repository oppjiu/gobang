import org.junit.Test;
import restful.bean.CommonResult;
import restful.database.EM;
import restful.entity.User;
import restful.utils.JsonUtil;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author lwz
 * @create 2021-11-28 21:23
 * @description:
 */
public class EMDemo {
    @Test
    public CommonResult demo() {
        EM em = new EM();
        try {
            //数据库查询
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            List<User> userList = entityManager.createNamedQuery("findAll", User.class).getResultList();
            em.commit();

            return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", null);
        } catch (Exception e) {
            e.printStackTrace();

            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }
}
