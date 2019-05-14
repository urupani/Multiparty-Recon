package com.demo.MultipartyRecon.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.demo.MultipartyRecon.Entity.LoginCredentialsEntity;

public interface LoginCredentialsRepository extends CrudRepository<LoginCredentialsEntity, Long> {

	@Query(value="select * from login_credentials_entity where username=?1 and password=?2", nativeQuery=true)
    public LoginCredentialsEntity  existsById(String username, String password);
}
