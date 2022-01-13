import org.junit.Test;
import restful.bean.CommonResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lwz
 * @create 2021-11-28 1:28
 * @description:
 */
public class TestJson {
    @Test
    public void test01() {
        CommonResult commonResult = new CommonResult();

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "张三");
        commonResult.setCode(0);
        commonResult.setMessage("");
        commonResult.setData(hashMap);

        System.out.println(commonResult);
    }
}
