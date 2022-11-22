package com.boot.junit.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByLastName(String lastName);

    UserEntity findByUserId(String userId);

    // Does the same as @Query method below
    List<UserEntity> findByEmailEndsWith(String email);

    @Query("select user from UserEntity user where user.email like %:emailDomain")
    List<UserEntity> findUsersWithEmailEndingWith(@Param("emailDomain") String emailDomain);
}
