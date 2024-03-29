package sideproject.puddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sideproject.puddy.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
