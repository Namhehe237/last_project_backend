package com.example.demo.examOnline.repository;

import com.example.demo.examOnline.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelCommentsByPostId(@Param("postId") Integer postId);

    List<Comment> findByPostPostId(Integer postId);

    List<Comment> findByParentCommentCommentId(Integer parentCommentId);

    List<Comment> findByUserUserId(Integer userId);
}

