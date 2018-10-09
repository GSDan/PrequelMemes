package com.gsdan.prequelmemes

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class RestAPI()
{
    private val redditApi: RedditApi

    init {

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com/r/prequelmemes/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        redditApi = retrofit.create(RedditApi::class.java)
    }

    fun getRandom(): Call<List<RedditRandomResponseItem>> {
        return redditApi.getRandom()
    }
}

