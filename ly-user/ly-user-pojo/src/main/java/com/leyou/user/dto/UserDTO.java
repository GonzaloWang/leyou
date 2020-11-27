package com.leyou.user.dto;

import com.leyou.common.dto.BaseDTO;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {
    private Long id;
    private String phone;
    private String username;

    public UserDTO(BaseEntity entity) {
        super(entity);
    }

    public static <T extends BaseEntity> List<UserDTO> convertEntityList(Collection<T> list){
        return list.stream().map(UserDTO::new).collect(Collectors.toList());
    }
}