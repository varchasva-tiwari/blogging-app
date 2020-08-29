package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("SELECT u.id FROM User u WHERE u.name = :name")
    int getId(@Param("name") String name);

    User findByName(String username);

    boolean existsByName(String username);

    boolean existsByEmail(String email);
}
