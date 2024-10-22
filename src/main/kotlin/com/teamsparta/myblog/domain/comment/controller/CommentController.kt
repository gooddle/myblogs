package com.teamsparta.myblog.domain.comment.controller

import com.teamsparta.myblog.domain.comment.dto.CommentRequest
import com.teamsparta.myblog.domain.comment.dto.CreateCommentResponse
import com.teamsparta.myblog.domain.comment.dto.UpdateCommentResponse
import com.teamsparta.myblog.domain.comment.service.CommentService
import com.teamsparta.myblog.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/feed/{feedId}/comments")
@RestController
class CommentController(
    val commentService: CommentService,
) {

    @PostMapping
    fun createComment(@PathVariable feedId:Long,
                      @RequestBody request: CommentRequest,
                      @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CreateCommentResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(commentService.createCommentAtFeed(feedId,request,userPrincipal.id))
    }

    @PutMapping("/{commentId}")
    fun updateComment(@PathVariable feedId: Long,
                      @PathVariable commentId: Long,
                      @RequestBody request: CommentRequest,
                      @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<UpdateCommentResponse>{
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentService.updateCommentAtFeed(feedId,commentId,request,userPrincipal.id))
    }


    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable feedId: Long,
                      @PathVariable commentId: Long,
                      @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(commentService.deleteCommentAtFeed(feedId, commentId, userPrincipal.id))

    }



}