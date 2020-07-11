 /*****************************
* Copyright 2018 360游戏艺术*
* **************************/
package moster.infras.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

 /***
  *
  * @author JackLei
  * @Date 2018年5月25日 下午12:12:16
  */
 public class SecurityUtils {

     public enum MessageDigestType {
         SHA("SHA"), MD5("MD5");

         private String algorithm;

         MessageDigestType(String algorithm) {
             this.algorithm = algorithm;
         }

         public String getAlgorithm() {
             return algorithm;
         }
     }

     public static final String KEY_SHA = "SHA";
     public static final String KEY_MD5 = "MD5";

     private static final char[] UPPER_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
             'F' };
     private static final char[] LOWER_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
             'f' };

     public static byte[] digest(MessageDigestType messageDigestType, byte[] input) throws NoSuchAlgorithmException {
         MessageDigest messageDigest = MessageDigest.getInstance(messageDigestType.getAlgorithm());
         return messageDigest.digest(input);
     }

     public static byte[] digest(MessageDigestType messageDigestType, String input) throws NoSuchAlgorithmException {
         MessageDigest messageDigest = MessageDigest.getInstance(messageDigestType.getAlgorithm());
         return messageDigest.digest(input.getBytes());
     }

     public static String byte2Hex(byte[] bytes) {
         return byte2Hex(bytes, true, "");
     }

     public static String byte2Hex(byte[] bytes, boolean upper, String prefix) {
         char[] byte2Char = upper ? UPPER_HEX : LOWER_HEX;
         StringBuilder sb = new StringBuilder();
         for (byte b : bytes) {
             sb.append(prefix);
             sb.append(byte2Char[(b & 0xF0) >> 4]);
             sb.append(byte2Char[b & 0xF]);
         }
         return sb.toString();
     }
 }
