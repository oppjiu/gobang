package restful.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @author lwz
 * @create 2021-10-01 20:13
 */
public class FileSaveUtil {
    /**
     * 单个文件上传，也可更改为多个文件上传
     *
     * @param request      http请求
     * @param autoName     上传文件命名
     * @param savePathname 上传文件存放地址
     * @return 目标文件名称
     */
    public static String saveFile(HttpServletRequest request, boolean autoName, String savePathname) {
        //返回的文件名
        String filename = null;

        //1、创建文件项工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建解析请求 数据的ServletFileUpload对象
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            //3、解析请求数据 返回FileItem列表
            List<FileItem> list = upload.parseRequest(request);
            //4、解析获取每一个FileItem对象，可选择多个文件
            FileItem item = list.get(0);

            //验证当前FileItem是否是表单字段，如果false则取到的是文件
            item.isFormField();

            //5、文件名及路径处理
            //处理文件
            String uploadFilename = item.getName();
            //截取文件扩展名
            String extName = uploadFilename.substring(uploadFilename.lastIndexOf("."));
            //如果autoName == true则选择使用UUID作为文件名，否则选择使用源文件名称
            if (autoName) {
                filename = UUID.randomUUID() + extName;
            } else {
                filename = uploadFilename;
            }
            //获取服务器上自定义的存放文件的目录
            String rootPath = request.getSession().getServletContext().getRealPath(savePathname);
            //生成完整的文件路径
            String newPath = rootPath + "/" + filename;
            System.out.println("生成的文件路径：" + newPath + " 文件名称为" + filename);
            //6、文件写入
            item.write(new File(newPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
}
