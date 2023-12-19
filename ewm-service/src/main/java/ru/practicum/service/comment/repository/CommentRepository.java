package ru.practicum.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.model.CommentState;

import java.awt.*;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndState(long eventId, CommentState commentState, Pageable pageable);

}
