package com.teamsparta.myblog.domain.feed.service

import com.teamsparta.myblog.domain.exception.ModelNotFoundException
import com.teamsparta.myblog.domain.feed.dto.CreateFeedResponse
import com.teamsparta.myblog.domain.feed.dto.FeedRequest
import com.teamsparta.myblog.domain.feed.dto.PageFeedResponse
import com.teamsparta.myblog.domain.feed.dto.UpdateFeedResponse
import com.teamsparta.myblog.domain.feed.model.Feed
import com.teamsparta.myblog.domain.feed.model.FeedCategory
import com.teamsparta.myblog.domain.feed.repository.FeedRepository
import com.teamsparta.myblog.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class FeedServiceImpl(
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
) : FeedService {

    override fun getFeedList(
        pageable: Pageable,
        title: String?,
        firstDay: Long?,
        secondDay: Long?,
        category: FeedCategory?
    ): Page<PageFeedResponse> {
        val page = PageRequest.of(pageable.pageNumber, 5)
        val feeds = feedRepository.findByDeletedFalse(page, title, firstDay, secondDay, category)
        return feeds.map { feed -> PageFeedResponse.from(feed) }
    }

    override fun getFeedById(feedId: Long): UpdateFeedResponse {
        val feed = feedRepository.findByFeedIdWithComments(feedId) ?: throw ModelNotFoundException("Feed")
        if (feed.deleted) throw ModelNotFoundException("feed")
        return UpdateFeedResponse.fromUpdateFeed(feed)
    }

    @Transactional
    override fun createFeed(request: FeedRequest, category: FeedCategory, userId: Long): CreateFeedResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("User")
        val feed = Feed(
            title = request.title,
            content = request.content,
            user = user,
            feedCategory = category
        ).let { feedRepository.save(it) }
        return CreateFeedResponse.fromCreateFeed(feed)
    }

    @Transactional
    override fun updateFeed(
        feedId: Long,
        request: FeedRequest,
        category: FeedCategory,
        userId: Long
    ): UpdateFeedResponse {
        val feed = feedRepository.findByFeedIdWithComments(feedId) ?: throw ModelNotFoundException("Feed")
        if (feed.user.id != userId) throw IllegalStateException("권한이 없습니다.")
        if (feed.deleted) throw ModelNotFoundException("Feed")
        feed.feedCategory = category
        feed.createFeedRequest(request)
        return UpdateFeedResponse.fromUpdateFeed(feed)
    }

    @Transactional
    override fun deleteFeed(feedId: Long, userId: Long) {
        val feed = feedRepository.findByIdOrNull(feedId) ?: throw ModelNotFoundException("Feed")
        if (feed.user.id != userId) throw IllegalStateException("권한이 없습니다.")
        if (feed.deleted) throw ModelNotFoundException("Feed")

        feed.softDeleted()
    }

    @Transactional
    override fun recoverFeed(feedId: Long, userId: Long): UpdateFeedResponse {
        val feed = feedRepository.findByIdOrNull(feedId) ?: throw ModelNotFoundException("Feed")
        if (feed.user.id != userId) throw IllegalStateException("권한이 없습니다.")
        if (feed.deleted) feed.deleted = false
        else throw ModelNotFoundException("feed")

        feed.status()
        return UpdateFeedResponse.fromUpdateFeed(feed)
    }

}

