package restful.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author lwz
 * @create 2021-11-22 22:05
 * @description: 生成验证码图片
 */
@Path("/gobang")
public class ValidateCodeAPI {
    /**
     * 返回生成的验证码图片
     *
     * @param request  request请求
     * @param response response响应
     * @throws IOException
     */
    @GET
    @Path("/validateCode")
    @Produces("image/jpeg")
    public void validateCode(@Context HttpServletRequest request,
                             @Context HttpServletResponse response) throws IOException {
        //获取验证码图片和验证码
        Map<String, Object> resultMap = createValidateCode(100, 50);
        BufferedImage bufferedImage = (BufferedImage) resultMap.get("bufferedImage");
        String code = (String) resultMap.get("code");
        //设置响应为图片
        response.setContentType("image/jpeg");
        //将验证码存入session中
        request.getSession().setAttribute("validateCode", code);
        //输出
        ImageIO.write(bufferedImage, "jpg", response.getOutputStream());
    }

    /**
     * 生成验证码图片
     *
     * @param width  验证码图片宽度
     * @param height 验证码图片高度
     * @return HashMap<String, Object> key为bufferedImage和code，value为验证码图片和验证码
     */
    public Map<String, Object> createValidateCode(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = bufferedImage.getGraphics();
        //画底板
        g.setColor(Color.pink);
        g.fillRect(0, 0, width, height);
        //画边框
        g.setColor(Color.blue);
        g.drawRect(0, 0, width - 1, height - 1);
        g.setFont(new Font("TimesRoman", Font.BOLD, 16));   //设置字体样式

        //打印随机字符
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            char ch = str.charAt(random.nextInt(str.length()));
            g.drawString(ch + "", width / 5 * i, height / 2);
            //记录生成的验证码
            code.append(ch);
        }

        //画干扰线
        g.setColor(Color.green);
        for (int i = 0; i < 6; i++) {
            int x1 = random.nextInt(width);
            int x2 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bufferedImage", bufferedImage);
        result.put("code", code.toString());
        return result;
    }
}
