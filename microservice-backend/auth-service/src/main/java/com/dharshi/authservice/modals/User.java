package com.dharshi.authservice.modals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")
@Builder
public class User {
    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private String verificationCode;

    private Date verificationCodeExpiryTime;

    private boolean enabled;

    private String profileImgUrl;

    private Set<Role> roles = new HashSet<>();
}
