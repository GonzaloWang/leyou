package com.leyou.auth.test;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void test() throws InterruptedException {
        // 生成jwt
        String jwt = jwtUtils.createJwt(UserDetail.of(1L, "Jack"));
        System.out.println("jwt = " + jwt);

        // jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzZTg1NWVlYmFiN2I0NDM1YjY2NzFiMzhmNDcwM2E5ZSIsInVzZXIiOiJ7XCJpZFwiOjEsXCJ1c2VybmFtZVwiOlwi6ams5LqRXCJ9In0.gnedpS9LE0VjetKVTyD2Opvi4eSyROOG_rSwQP0kDC0";

        // 解析jwt
        Payload payload = jwtUtils.parseJwt(jwt);
        System.out.println("payload = " + payload);
    }
}
