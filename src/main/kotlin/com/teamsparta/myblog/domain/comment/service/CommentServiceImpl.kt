package com.teamsparta.myblog.domain.comment.service

import com.teamsparta.myblog.domain.comment.dto.CommentRequest
import com.teamsparta.myblog.domain.comment.dto.CreateCommentResponse
import com.teamsparta.myblog.domain.comment.dto.UpdateCommentResponse
import com.teamsparta.myblog.domain.comment.model.Comment
import com.teamsparta.myblog.domain.comment.repository.CommentRepository
import com.teamsparta.myblog.domain.exception.ModelNotFoundException
import com.teamsparta.myblog.domain.feed.repository.FeedRepository
import com.teamsparta.myblog.domain.user.repository.UserRepository
import com.teamsparta.myblog.infra.aop.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CommentServiceImpl(
    private val feedRepository: FeedRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) : CommentService {

    @Transactional
    override fun createCommentAtFeed(feedId: Long, request: CommentRequest, userId: Long): CreateCommentResponse {
        val feed = feedRepository.findByIdOrNull(feedId) ?: throw ModelNotFoundException("Feed not found", feedId)
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundException("User not found")
        if (feed.deleted) throw IllegalStateException("삭제된 게시글입니다.")
        val comment = Comment(
            title = request.title,
            content = request.content,
            feed = feed,
            user = user,
        ).let { commentRepository.save(it) }
        return CreateCommentResponse.fromCreateComment(comment)
    }

    @Transactional
    override fun updateCommentAtFeed(
        feedId: Long,
        commentId: Long,
        request: CommentRequest,
        userId: Long
    ): UpdateCommentResponse {
        val feedWithComment = commentRepository.findByFeedIdAndId(feedId, commentId) ?: throw ModelNotFoundException(
            "Feed not found",
            feedId
        )

        if (feedWithComment.user.id != userId) throw IllegalStateException("권한이 없습니다.")
        if (feedWithComment.feed.deleted) throw NotFoundException("삭제된 게시글입니다.")

        feedWithComment.updateCommentRequest(request)
        return UpdateCommentResponse.fromUpdateComment(feedWithComment)
    }

    @Transactional
    override fun deleteCommentAtFeed(feedId: Long, commentId: Long, userId: Long) {
        val feedWithComment = commentRepository.findByFeedIdAndId(feedId, commentId) ?: throw ModelNotFoundException(
            "Feed not found",
            feedId
        )
        if (feedWithComment.user.id != userId) throw IllegalStateException("권한이 없습니다.")
        if (feedWithComment.feed.deleted) throw NotFoundException("삭제된 게시글입니다.")

        commentRepository.delete(feedWithComment)

    }

}