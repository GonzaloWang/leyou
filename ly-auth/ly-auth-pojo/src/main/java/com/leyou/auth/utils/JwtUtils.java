package com.leyou.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.auth.constants.RedisConstants;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class JwtUtils {

    /**
     * JWT解析器
     */
    private final JwtParser jwtParser;
    /**
     * 秘钥
     */
    private final SecretKey secretKey;

    private final StringRedisTemplate redisTemplate;

    private final static ObjectMapper mapper = new ObjectMapper();

    public JwtUtils(String key, StringRedisTemplate redisTemplate) {
        // 生成秘钥
        secretKey = Keys.hmacShaKeyFor(key.getBytes(Charset.forName("UTF-8")));
        // Redis工具
        this.redisTemplate = redisTemplate;
        // JWT解析器
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * 生成jwt，用默认的JTI
     *
     * @param userDetail 用户信息
     * @return JWT
     */
    public String createJwt(UserDetail userDetail) {
        return createJwt(userDetail, RedisConstants.TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 生成jwt，自己指定的JTI
     *
     * @param userDetail 用户信息
     * @return JWT
     */
    public String createJwt(UserDetail userDetail, int expireSeconds) {
        try {
            // 1.生成jti
            String jti = createJti();
            // 2.生成token
            String jwt = Jwts.builder().signWith(secretKey)
                    .setId(jti)
                    .claim("user", mapper.writeValueAsString(userDetail))
                    .compact();
            // 3.记录在redis中
            redisTemplate.opsForValue().set(RedisConstants.JTI_KEY_PREFIX + userDetail.getId(), jti,
                    expireSeconds, TimeUnit.SECONDS);
            return jwt;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析jwt，并将用户信息转为指定的Clazz类型
     *
     * @param jwt token
     * @return 载荷，包含JTI和用户信息
     */
    public Payload parseJwt(String jwt) {
        // 1.验证并解析jwt
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
        Claims claims = claimsJws.getBody();
        // 2.解析载荷数据
        Payload payload = new Payload();
        payload.setJti(claims.getId());
        UserDetail userDetail = null;
        try {
            userDetail = mapper.readValue(claims.get("user", String.class), UserDetail.class);
        } catch (IOException e) {
            throw new RuntimeException("用户信息解析失败！");
        }
        payload.setUserDetail(userDetail);

        // 3.验证是否过期
        // 3.1.取出redis中jti
        String cacheJti = redisTemplate.opsForValue().get(RedisConstants.JTI_KEY_PREFIX + userDetail.getId());
        // 3.2.获取jwt中的jti
        String jti = payload.getJti();
        // 3.3.比较
        if(cacheJti == null){
            throw new RuntimeException("登录已经过期!");
        }
        if(!cacheJti.equals(jti)){
            // 有jti，但是不是当前jwt，说明其它人登录把当前jwt踢下去了
            throw new RuntimeException("用户可能在其它设备登录!");
        }
        return payload;
    }

    private String createJti() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    /**
     * 刷新jwt有效期
     * @param userId 用户id
     */
    public void refreshJwt(Long userId){
        refreshJwt(userId, RedisConstants.TOKEN_EXPIRE_SECONDS);
    }
    /**
     * 刷新jwt有效期
     * @param userId 用户id
     * @param expireSeconds 有效期
     */
    public void refreshJwt(Long userId, int expireSeconds){
        redisTemplate.expire(RedisConstants.JTI_KEY_PREFIX + userId, expireSeconds, TimeUnit.SECONDS);
    }
}