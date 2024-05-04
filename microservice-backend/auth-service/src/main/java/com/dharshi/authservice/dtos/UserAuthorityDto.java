package com.dharshi.authservice.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class UserAuthorityDto {

    private String userId;

    private List<String> authorities;

}
