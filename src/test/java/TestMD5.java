import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lwz
 * @create 2021-11-30 16:16
 * @description:
 */
public class TestMD5 {
    public static String stringMD5(String inputString) {
        System.out.println(inputString);
        try {
            // 拿到一个MD5转换器
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = inputString.getBytes();

            for (int i = 0; i < inputByteArray.length; i++) {
                byte b = inputByteArray[i];
                System.out.print(b + " ");
            }
            System.out.println();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            for (int i = 0; i < resultByteArray.length; i++) {
                System.out.print(resultByteArray[i] + "  ");
            }
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    //将字节数组换成成16进制的字符串
    public static String byteArrayToHex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100), 1, 3);
        }
        System.out.println(sb);
        return sb.toString();
    }

    @Test
    public void test01() {
        stringMD5("123456");
//		System.out.println(Integer.toHexString(-31));
//		System.out.println(Integer.toHexString(10));
//		System.out.println(Integer.toHexString(-36));
//		System.out.println(Integer.toHexString(57));System.out.println(Integer.toHexString(73));
        String slat = "&%5123***&&%%$$#@";
        String str = "123456";
        String base = str + "/" + slat;
        String md5 = DigestUtils.md5Hex(base.getBytes());
        System.out.println(md5);
    }
}
