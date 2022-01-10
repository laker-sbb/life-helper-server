package com.inlym.lifehelper.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inlym.lifehelper.auth.core.SimpleAuthentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

@Service
public class JWTService {
    /**
     * 在 JWT 中存储用户 ID 的字段名
     */
    private final String USER_ID_FIELD = "uid";

    /**
     * 在 JWT 中存储权限信息的字段名
     */
    private final String AUTHORITIES_FIELD = "aut";

    /**
     * JWT 发行人，即项目名称
     */
    private final String issuer = "lifehelper-server";

    /**
     * JWT 算法
     */
    private final Algorithm algorithm;

    public JWTService(JWTProperties jwtProperties) {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    /**
     * 生成 JWT 字符串
     *
     * @param userId 用户 ID
     * @param roles  角色列表
     *
     * @return JWT 字符串
     */
    public String create(int userId, String[] roles) {
        Assert.isTrue(userId > 0, "用户 ID 应大于 0");

        JWTCreator.Builder jwt = JWT
            .create()
            .withIssuer(issuer)
            .withIssuedAt(new Date())
            .withClaim(USER_ID_FIELD, userId);

        if (roles != null && roles.length > 0) {
            String rolesStr = String.join(",", roles);
            jwt.withClaim(AUTHORITIES_FIELD, rolesStr);
        }

        return jwt.sign(algorithm);
    }

    /**
     * 生成 JWT 字符串（不包含权限信息）
     *
     * @param userId 用户 ID
     *
     * @return JWT 字符串
     */
    public String create(int userId) {
        return this.create(userId, null);
    }

    /**
     * 解析 JWT 字符串
     *
     * @param token JWT 字符串
     */
    public SimpleAuthentication parse(String token) {
        JWTVerifier verifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build();

        DecodedJWT jwt = verifier.verify(token);

        int userId = jwt
            .getClaim(USER_ID_FIELD)
            .asInt();

        String rolesStr = jwt
            .getClaim(AUTHORITIES_FIELD)
            .asString();

        if (rolesStr == null) {
            return new SimpleAuthentication(userId);
        } else {
            String[] roles = rolesStr.split(",");
            return new SimpleAuthentication(userId, roles);
        }
    }
}
