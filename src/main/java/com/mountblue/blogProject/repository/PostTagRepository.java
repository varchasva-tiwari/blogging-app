package com.mountblue.blogProject.repository;

import com.mountblue.blogProject.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag,Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT into post_tags (post_id, tag_id) VALUES(:postId, :tagId)", nativeQuery = true)
    public void createPostTag(@Param("postId") int postId, @Param("tagId") int tagId);

    @Query("SELECT pt FROM PostTag pt WHERE pt.postId  = :postId")
    List<PostTag> readTagsByPost(@Param("postId") int postId);

    @Modifying
    @Transactional
    @Query(value = "DELETE from post_tags pt WHERE pt.post_id = :postId", nativeQuery = true)
    void deleteById(@Param("postId") int postId);
}
