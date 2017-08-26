package com.github.telegram_bots.channels_feed.tg.service.processor

import com.github.telegram_bots.channels_feed.tg.domain.*
import com.github.telegram_bots.channels_feed.tg.domain.ProcessedPostGroup.Type

interface PostProcessor {
    val type: Type

    fun process(data: RawPostData): ProcessedPost
}
