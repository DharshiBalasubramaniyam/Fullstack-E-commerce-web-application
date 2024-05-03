package com.dharshi.userservice.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private String userId;
    private String email;
    private String username;
}
