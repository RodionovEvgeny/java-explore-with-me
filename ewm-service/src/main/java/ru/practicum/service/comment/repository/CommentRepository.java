package ru.practicum.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.model.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndState(long eventId, CommentState commentState, Pageable pageable);

    @Query(value = "SELECT c " +
            "FROM Comment AS c " +
            "WHERE " +
            "(c.commentator.id = :userId OR :userId is null) AND " +
            "(c.event.id = :eventId OR :eventId is null) AND " +
            "(c.state = :commentState OR :commentState is null) AND " +
            "(c.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Comment> findComments(Long userId, Long eventId, CommentState commentState, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}
