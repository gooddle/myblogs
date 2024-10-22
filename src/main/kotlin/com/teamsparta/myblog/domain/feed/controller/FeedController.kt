package com.teamsparta.myblog.domain.feed.controller

import com.teamsparta.myblog.domain.feed.dto.CreateFeedResponse
import com.teamsparta.myblog.domain.feed.dto.FeedRequest
import com.teamsparta.myblog.domain.feed.dto.PageFeedResponse
import com.teamsparta.myblog.domain.feed.dto.UpdateFeedResponse
import com.teamsparta.myblog.domain.feed.model.FeedCategory
import com.teamsparta.myblog.domain.feed.service.FeedService
import com.teamsparta.myblog.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("api/v1/feeds")
@RestController
class FeedController(
    private var feedService: FeedService,

    ) {

    @GetMapping
    fun getFeedList(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) title: String?,
        firstDay: Long?,
        secondDay: Long?,
        category: FeedCategory?
    ): ResponseEntity<Page<PageFeedResponse>> {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(feedService.getFeedList(pageable, title, firstDay, secondDay, category))
    }

    @GetMapping("{feedId}")
    fun getFeedById(@PathVariable feedId: Long): ResponseEntity<UpdateFeedResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(feedService.getFeedById(feedId))
    }

    @PostMapping
    fun createFeed(
        @RequestBody request: FeedRequest,
        category: FeedCategory,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CreateFeedResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(feedService.createFeed(request, category, userPrincipal.id))
    }

    @PutMapping("/{feedId}")
    fun updateFeedById(
        @PathVariable feedId: Long,
        @RequestBody request: FeedRequest,
        category: FeedCategory,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<UpdateFeedResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(feedService.updateFeed(feedId, request, category, userPrincipal.id))
    }

    @DeleteMapping("/{feedId}")
    fun deleteFeedById(
        @PathVariable feedId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(feedService.deleteFeed(feedId, userPrincipal.id))

    }

    @PutMapping("/recover/{feedId}")
    fun recoverFeed(
        @PathVariable feedId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<UpdateFeedResponse> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(feedService.recoverFeed(feedId, userPrincipal.id))
    }
}
