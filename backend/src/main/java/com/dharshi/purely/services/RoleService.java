package com.dharshi.purely.services;

import com.dharshi.purely.enums.ERole;
import com.dharshi.purely.modals.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    Role findByName(ERole eRole);
}