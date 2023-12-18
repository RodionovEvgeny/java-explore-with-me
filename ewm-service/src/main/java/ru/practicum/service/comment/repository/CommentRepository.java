package ru.practicum.service.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {


}
