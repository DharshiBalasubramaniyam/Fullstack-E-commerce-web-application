package com.dharshi.authservice.services;

import com.dharshi.authservice.enums.ERole;
import com.dharshi.authservice.modals.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    Role findByName(ERole eRole);
}