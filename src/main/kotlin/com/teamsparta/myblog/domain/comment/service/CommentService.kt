package com.teamsparta.myblog.domain.comment.service

import com.teamsparta.myblog.domain.comment.dto.CommentRequest
import com.teamsparta.myblog.domain.comment.dto.CreateCommentResponse
import com.teamsparta.myblog.domain.comment.dto.UpdateCommentResponse

interface CommentService {
    fun createCommentAtFeed(feedId: Long, request: CommentRequest, userId: Long): CreateCommentResponse
    fun updateCommentAtFeed(feedId: Long, commentId: Long, request: CommentRequest, userId: Long): UpdateCommentResponse
    fun deleteCommentAtFeed(feedId: Long, commentId: Long, userId: Long)
}