package com.questionnaire.demo.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;

public class Token {
    private static final String SECRET = "6MNSobBRCHGIO0fS6MNSobBRCHGIO0fS";
    private static final long EXPIRE_TIME = 1000 * 1200; //1200s超时

    public static String buildToken(String userid) {
        // 设置过期时间
        Date expried = new Date(new Date().getTime() + EXPIRE_TIME);
        // 设置头部信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("Type", "Jwt");
        header.put("alg", "HS256");
        // 返回token字符串
        return JWT.create()
                .withHeader(header)
                .withClaim("userid", userid)
                .withExpiresAt(expried)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String decode(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("userid").asString();
    }
}
