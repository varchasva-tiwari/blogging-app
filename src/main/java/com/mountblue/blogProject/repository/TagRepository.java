package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {
    Tag findTagByName(String name);

    @Query("SELECT t.id FROM Tag t WHERE t.name = :name")
    int findIdByName(@Param("name") String name);
}
