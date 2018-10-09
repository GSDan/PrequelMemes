package com.gsdan.prequelmemes

import retrofit2.Call
import retrofit2.http.GET

interface  RedditApi {
    @GET("random.json")
    fun getRandom() : Call<List<RedditRandomResponseItem>>
}