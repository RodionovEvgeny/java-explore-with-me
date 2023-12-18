package ru.practicum.service.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllEventsComments(@RequestParam(name = "eventId") long eventId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllEventsComments(eventId, from, size); // todo return only published
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable long commentId) {
        return commentService.getCommentById(commentId); // todo return only published
    }
}
