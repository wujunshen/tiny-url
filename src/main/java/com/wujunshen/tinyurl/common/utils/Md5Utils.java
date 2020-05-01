package com.wujunshen.tinyurl.common.utils;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * @author frank woo(吴峻申) <br>
 *     email:<a href="mailto:frank_wjs@hotmail.com">frank_wjs@hotmail.com</a> <br>
 * @date 2020/5/1 2:46 下午 <br>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Md5Utils {

  public static String encode(byte[] data) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
    md5.update(data, 0, data.length);
    return new String(Hex.encodeHex(md5.digest()));
  }

  public static String encode(String data) {
    return encode(data.getBytes(StandardCharsets.UTF_8));
  }
}
