package vip.rory.dht.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author zhanghangtian
 * @date 2019年7月8日 下午4:48:36
 */
public class GenerateUtils {

    public static byte[] generateNodeId() {
        return DigestUtils.sha1(RandomStringUtils.random(10));
    }

    public static String generateNodeIdHex() {
        return DigestUtils.sha1Hex(RandomStringUtils.random(10));
    }

    public static String generateTransactionId() {
        return RandomStringUtils.random(8, "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public static String generateToken() {
        return RandomStringUtils.random(2, "abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}
