package restful.bean;

/**
 * @author lwz
 * @create 2021-11-19 20:39
 * @description: 通用json返回类型
 */
public class CommonResult {
    /**
     * CODE_SUCCESS: 成功
     * CODE_WRONG: 错误
     * CODE_ERROR: 异常
     * CODE_VALIDATE_CODE_ERROR: 验证码不匹配
     * CODE_HAS_LOGIN_ERROR: 已经登录
     */
    public static final int CODE_SUCCESS = 10;
    public static final int CODE_WRONG = 0;
    public static final int CODE_ERROR = -10;
    public static final int CODE_VALIDATE_CODE_ERROR = -20;
    public static final int CODE_HAS_LOGIN_ERROR = -30;

    private int code;
    private String message;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
