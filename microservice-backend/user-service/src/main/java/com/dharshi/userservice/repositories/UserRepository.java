package com.dharshi.userservice.repositories;

import com.dharshi.userservice.modals.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
