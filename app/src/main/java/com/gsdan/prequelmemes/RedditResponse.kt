package com.gsdan.prequelmemes

class RedditRandomResponseItem(val data: RedditResponseItemData)

class RedditResponseItemData(
        val children: List<RedditChildrenResponse>,
        val after: String?,
        val before: String?
)

class RedditChildrenResponse(val data: Meme)

class Meme(
        val author: String,
        val title: String,
        val is_video: Boolean,
        val permalink: String,
        val created_utc: Long,
        val ups: Int,
        val downs: Int,
        val url: String
)