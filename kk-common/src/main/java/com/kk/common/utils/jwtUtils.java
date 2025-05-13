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

    /**
     * 过期 JWT
     * @param token
     * @param secretKey
     */
    public static void expireToken(String token,String secretKey) {

         Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);

    }
}
