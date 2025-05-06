package com.kk.common.utils;

import com.kk.common.model.LoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @Author：kk
 * @Date：2025/4/23 22:08
 */
public class jwtUtils {
    // 密钥，用于签名和验证 JWT
    private static final String SECRET_KEY = "2b65e17e6d86e95b6fdd0d489dd85ee6f834fded12773c5b33f8b88d685b28d1";
    // 有效期，设置为 20 小时
    private static final long EXPIRATION_TIME = 20 * 60 * 60 * 1000;

    /**
     * 生成 JWT
     * @param username
     * @return
     */
    public static LoginDto generateToken(String username,String secretKey,long expirationTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        String token  =Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(token);
        loginDto.setUserName(username);
        loginDto.setLoginTime(now);
        loginDto.setExpireTime(expiration);
        return loginDto;
    }

    /**
     * 验证 JWT
     * @param token
     * @return
     */
    public static Claims validateToken(String token,String secretKey) {

            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

    }
}
