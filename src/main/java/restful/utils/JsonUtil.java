package restful.utils;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.bean.CommonResult;

/**
 * @author lwz
 * @create 2021-11-27 21:20
 * @description: Json工具类，发送json
 */
public class JsonUtil {
    /**
     * 生成CommonResult
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     * @return CommonResult类型
     */
    public static CommonResult setCommonResult(int code, String message, Object data) {
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(code);
        commonResult.setMessage(message);
        commonResult.setData(data);

        System.out.println("CommonResult: " + commonResult);

        return commonResult;
    }

    /**
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     * @return JSONObject类型
     */
    public static JSONObject setCommonResultJson(int code, String message, Object data) {
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(code);
        commonResult.setMessage(message);
        commonResult.setData(data);

        JSONObject jsonObject = JSONObject.fromObject(commonResult);
        System.out.println("jsonObject: " + jsonObject);
        return jsonObject;
    }
}
