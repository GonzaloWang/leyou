package com.leyou.auth.dto;

import lombok.Data;

/**
 * 保存载荷数据
 */
@Data
public class Payload {
    private String jti;
    private UserDetail userDetail;
}
