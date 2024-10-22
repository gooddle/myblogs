package com.teamsparta.myblog.domain.comment.dto

import com.teamsparta.myblog.domain.comment.model.Comment
import java.time.LocalDateTime

data class CreateCommentResponse(
    val id :Long,
    val title :String,
    val content :String,
    val createdAt : LocalDateTime
){
    companion object{
        fun fromCreateComment(comment: Comment): CreateCommentResponse{
            return CreateCommentResponse(
                id = comment.id!!,
                title = comment.title,
                content = comment.content,
                createdAt = comment.createdAt
            )
        }
    }
}