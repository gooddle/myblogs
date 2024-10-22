package com.teamsparta.myblog.domain.feed.dto


import com.teamsparta.myblog.domain.comment.dto.UpdateCommentResponse
import com.teamsparta.myblog.domain.feed.model.Feed
import java.time.LocalDateTime

data class UpdateFeedResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deleted: Boolean,
    val category: String,
    val comments: List<UpdateCommentResponse>
) {
    companion object {
        fun fromUpdateFeed(feed: Feed): UpdateFeedResponse {
            return UpdateFeedResponse(
                id = feed.id!!,
                title = feed.title,
                content = feed.content,
                createdAt = feed.createdAt,
                updatedAt = feed.updatedAt,
                deleted = false,
                category = feed.feedCategory.name,
                comments = feed.comments.map { UpdateCommentResponse.fromUpdateComment(it) },
            )
        }
    }
}
