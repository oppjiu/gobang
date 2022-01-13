package restful.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.annotation.GobangPower;
import restful.bean.CommonResult;
import restful.database.EM;
import restful.entity.User;
import restful.utils.FileSaveUtil;
import restful.utils.JsonUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * @author lwz
 * @create 2021-11-21 14:35
 * @description: 文件上传API
 */
@Path("/gobang")
public class UploadAPI {
    @Context
    HttpServletRequest request;

    /**
     * @param name 用户名
     * @return CommonResult类型的json对象message属性为文件的url
     */
    @GobangPower(GobangPower.LOGIN)
    @POST
    @Path("/uploadImg/{name}")
    @Consumes("multipart/form-data")
    @Produces("application/json;charset=UTF-8")
    public CommonResult uploadImg(@PathParam("name") String name) throws UnsupportedEncodingException {
        //转码
        name = URLDecoder.decode(name, "utf-8");
        //照片存放地址
        String savePathname = "/images/headIcons/";
        //获取服务器地址
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        String uploadFilename = FileSaveUtil.saveFile(request, true, savePathname);
        String url = basePath + savePathname + uploadFilename;
        System.out.println(name + "的文件上传地址为：" + url);

        //存入数据库中
        EM em = new EM();
        try {
            EntityManager entityManager = em.getEntityManager();
            em.begin();
            User modifyUser = entityManager.createNamedQuery("findUserByName", User.class)
                    .setParameter("name", name)
                    .getSingleResult();

            if (modifyUser != null) {
                //将图片的url存入数据库中
                modifyUser.setImgUrl(url);
                entityManager.persist(modifyUser);
                em.commit();

                //将url信息发送给前端
                HashMap<String, String> map = new HashMap<>();
                map.put("url", url);
                return JsonUtil.setCommonResult(CommonResult.CODE_SUCCESS, "", map);
            }

            return JsonUtil.setCommonResult(CommonResult.CODE_WRONG, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.setCommonResult(CommonResult.CODE_ERROR, "", null);
        } finally {
            em.close();
        }
    }
}
