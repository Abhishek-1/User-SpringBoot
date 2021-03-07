package com.projects.waterloo.app.ws.io.repository;

//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.projects.waterloo.app.ws.io.entity.UserEntity;

//CrudRepository was replaced by PagingAndSortingRepository to use paging functionality provided by SpringData JPA
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
}
