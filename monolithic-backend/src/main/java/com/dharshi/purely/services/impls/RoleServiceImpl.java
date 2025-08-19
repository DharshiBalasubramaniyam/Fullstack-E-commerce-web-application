package com.dharshi.purely.services.impls;

import com.dharshi.purely.enums.ERole;
import com.dharshi.purely.modals.Role;
import com.dharshi.purely.repositories.RoleRepository;
import com.dharshi.purely.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByName(ERole eRole) {
        return roleRepository.findByName(eRole)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
    }
}
