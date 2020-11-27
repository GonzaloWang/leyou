package com.leyou.auth.service.serviceimpl;

import com.leyou.auth.constants.RedisConstants;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.service.UserAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.constants.JwtConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final UserClient userClient;

    private final JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public UserAuthServiceImpl(UserClient userClient, JwtUtils jwtUtils) {
        this.userClient = userClient;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void login(String username, String password, HttpServletResponse response) {
        try {
            // 1.授权中心携带用户名密码，到用户中心查询用户
            UserDTO user = userClient.queryUserByUsernameAndPassword(username, password);

            // 2.校验查询结果
            if (user == null) {
                throw new LyException(400, "用户名或密码错误！");
            }

            // 3.如果正确，生成JWT凭证，查询错误则返回400
            // 3.1.准备用户信息
            UserDetail userDetails = UserDetail.of(user.getId(), user.getUsername());
            // 3.2.生成jwt
            String jwt = jwtUtils.createJwt(userDetails);
            
            // 4.把JWT写入用户cookie
            writeCookie(response, jwt);

        } catch (FeignException e) {
            // 把远程调用异常转换抛出
            throw new LyException(e.status(), e.contentUTF8());
        }
    }

    private void writeCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JwtConstants.COOKIE_NAME, token);
        // cookie的作用域
        cookie.setDomain(JwtConstants.DOMAIN);
        // 是否禁止JS操作cookie，避免XSS攻击
        cookie.setHttpOnly(true);
        // cookie有效期，-1就是跟随当前会话，浏览器关闭就消失
        cookie.setMaxAge(-1);
        // cookie作用的路径，/代表一切路径
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1.获取用户cookie
        String jwt = CookieUtils.getCookieValue(request, "LY_TOKEN");
        // 2.校验cookie中的token的有效性
        Payload payload = null;
        try {
            payload = jwtUtils.parseJwt(jwt);
        } catch (Exception e) {
            // 3.如果无效，什么都不做
            return;
        }
        // 4.如果有效，删除cookie（重新写一个cookie，maxAge为0）
        CookieUtils.deleteCookie(JwtConstants.COOKIE_NAME, JwtConstants.DOMAIN, response);

        // 5.删除redis中的JTI
        // 5.1.获取用户信息
        UserDetail userDetail = payload.getUserDetail();
        // 5.2.删除redis数据
        redisTemplate.delete(RedisConstants.JTI_KEY_PREFIX + userDetail.getId());
    }
}
