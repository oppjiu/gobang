package restful.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lwz
 * @create 2021-12-15 2:31
 * @description:
 */
public class MD5Utils {
    public static String stringMD5(String inputString) {
        try {
            // 拿到一个MD5转换器
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = inputString.getBytes();
            for (int i = 0; i < inputByteArray.length; i++) {
                byte b = inputByteArray[i];
            }
            messageDigest.update(inputByteArray);

            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    //将字节数组换成成16进制的字符串
    public static String byteArrayToHex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub)
        String slat = "&%5123***&&%%$$#@";
        String str = "123456";
        String base = str + "/" + slat;
        String md5 = DigestUtils.md5Hex(base.getBytes());
        System.out.println(md5);
    }

}
