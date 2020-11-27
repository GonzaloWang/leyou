package com.leyou.user.web;

import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.UserContext;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/info")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 校验手机号或用户名是否存在
     * @param data 用户名或手机号
     * @param type 数据类型：1是用户名；2是手机；其它是参数有误
     * @return true：可以使用; false：不可使用
     */
    @GetMapping("/exists/{data}/{type}")
    public ResponseEntity<Boolean> exists(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        return ResponseEntity.ok(userService.exists(data, type));
    }

    /**
     * 发送短信验证码
     * @return 无
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册用户
     * @param user 用户信息
     * @param code 验证码
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据手机号和密码查询用户
     * @param username 手机号
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping
    public ResponseEntity<UserDTO> queryUserByPhoneAndPassword(
            @RequestParam("username") String username, @RequestParam("password") String password){
        return ResponseEntity.ok(userService.queryUserByPhoneAndPassword(username, password));
    }

    /**
     * 获取当前登录的用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetail> me(){
        return ResponseEntity.ok(UserContext.getUser());
    }
}