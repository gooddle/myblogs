package com.teamsparta.myblog.domain.comment.dto


import java.time.LocalDateTime

data class UpdateCommentResponse(
    val id :Long,
    val title :String,
    val content :String,
    val createdAt : LocalDateTime,
    val updatedAt :LocalDateTime?
)

