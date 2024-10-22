package com.teamsparta.myblog.domain.feed.dto


import com.teamsparta.myblog.domain.feed.model.Feed
import java.time.LocalDateTime

data class CreateFeedResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val deleted: Boolean,
    val category: String,
) {
    companion object {
        fun fromCreateFeed(feed: Feed): CreateFeedResponse {
            return CreateFeedResponse(
                id = feed.id!!,
                title = feed.title,
                content = feed.content,
                createdAt = feed.createdAt,
                deleted = false,
                category = feed.feedCategory.name,
            )
        }
    }
}