package com.dharshi.purely.services;

import com.dharshi.purely.exceptions.UserNotFoundException;
import com.dharshi.purely.modals.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String Email);

    User findByEmail(String email) throws UserNotFoundException;


}
