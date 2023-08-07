package com.kk.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class RSAUtil {
    // 加密数据和秘钥的编码方式
    public static final String UTF_8 = "UTF-8";

    // 填充方式
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String RSA_ALGORITHM_NOPADDING = "RSA";
    //私钥
    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKwPWAjvQHFi6OE3Dw2lpUpFeRxGh+h5Rs30OPmbeLeH1cAhLouSMjTUviJ5GlMecwTOhdRjxrb+y8N///G4e4MmVkE20JcrgM/HnHjMd1RYOMUalaUxDOGypCV+zS2Q3mA/T3mDbsvSSL82tG4GmDE/WrZ0cqkvfuwEXxc1Gl53AgMBAAECgYEAogyhgWi0bRYW92Z/yv6julvMQRE8l3sBcJ//uTbwbwqECrw1tkYu+wsTOCyO2pHnCjPoX6zJTziSeMJpMCPsToCm3+EO38Ki/12RSJ51DgSl7C4R8leoFMZAmExQO4L1rVae2dJS80dvfpLeNiquJw4y7xjXIBUYbQfu+mNKHoECQQDic2F3UMm05M1d/POQHqsuMohoDhwVM0N8/SdbA4fpYFFOahMWdmuFclYbkgTwfYBdGMH/UHLsCGWVv8z48jKHAkEAwoMID93DzTsZWqxAoyGDg7GGdNsMPvYErRxwcOVNpdQXv8F7IAXOslLTvJF542dplCCNzLgyupwxMPlMLuZAkQJANpcoHPJt3dz2oTzUnp62F6n49lTIclfsYhpJPYipYBpnH2c0+MpNe1sn5Peblzo6ErdgNSN4wOv5SVN2n2ELywJAGjyiccFwD9bQ7LIfZeG3Y6QmhsylMjjtGIylfhTwDFY3fd4TRZaC8vrJJL5aupnQW/KoLd0KurEm0XxPEmRsgQJARKNghvpoce39bD/BbC2AojY5Ea6afLzW+GllxHGNGkgGu+x3c4PIUDAt8f60021WoENtonEQpEjzbIU5XpefJA==";
    //公钥
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsD1gI70BxYujhNw8NpaVKRXkcRofoeUbN9Dj5m3i3h9XAIS6LkjI01L4ieRpTHnMEzoXUY8a2/svDf//xuHuDJlZBNtCXK4DPx5x4zHdUWDjFGpWlMQzhsqQlfs0tkN5gP095g27L0ki/NrRuBpgxP1q2dHKpL37sBF8XNRpedwIDAQAB";
    /**
     * 非对称加密，根据公钥和原始内容产生加密内容
     */
    private static String encryptRSA(Key key, String plainText)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(plainText.getBytes(UTF_8))).toString();
    }

    /**
     * 根据私钥和加密内容产生原始内容
     */
    private static String decryptRSA(Key key, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, DecoderException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] contentArry = content.getBytes();
        return new String(cipher.doFinal(Base64.decodeBase64(contentArry)), UTF_8);
    }

    /**
     * 创建RSA的公钥和私钥示例 将生成的公钥和私钥用Base64编码后打印出来
     * @throws NoSuchAlgorithmException
     */
    public static void createKeyPairs() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        System.out.println("公钥"+Base64.encodeBase64(publicKey.getEncoded()));
        System.out.println("私钥"+Base64.encodeBase64(privateKey.getEncoded()));
    }

    /**
     *  Description:默认的RSA解密方法 一般用来解密 参数 小数据
     */
    public static String decryptRSADefault(String privateKeyStr,String data) {
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NOPADDING);
            byte[] privateKeyArray = privateKeyStr.getBytes();
            byte[] dataArray = data.getBytes();
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyArray));
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decodeBase64(dataArray)), UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * rsa 加密
     * @param input
     * @return
     * @throws Exception
     */
    public static String encryptRSAStr(String input) throws Exception {
        byte[] pubdecoded = Base64.decodeBase64(PUBLIC_KEY);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubdecoded));
        String output = encryptRSA(pubKey,input) ;
        return output;
    }

    /**
     * rsa 解密
     * @param input
     * @return
     * @throws Exception
     */
    public static String decryptRSAStr(String input) throws Exception {
        byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        String output = URLDecoder.decode(decryptRSA(priKey,input) ,"UTF-8");
        return output;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, DecoderException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException {
        String messageEn = "";
        //base64编码的私钥
        Date d = new Date();
        byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
        PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Date d_prik = new Date();
        System.out.printf((d_prik.getTime()-d.getTime())+"\n");
        String s = URLDecoder.decode(RSAUtil.decryptRSA(priKey,messageEn) ,"UTF-8");
        System.out.println(s);
        Date d_pri = new Date();
        System.out.printf((d_pri.getTime()-d.getTime())+"\n");

        byte[] pubdecoded = Base64.decodeBase64(PUBLIC_KEY);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubdecoded));
        String s1 = RSAUtil.encryptRSA(pubKey,"xxxxxx") ;
        Date d_pub = new Date();
        System.out.println(s1);
        System.out.printf((d_pub.getTime()-d_pri.getTime())+"\n");

    }
}