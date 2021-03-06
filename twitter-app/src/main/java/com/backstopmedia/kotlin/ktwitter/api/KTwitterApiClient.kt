package com.backstopmedia.kotlin.ktwitter.api

import com.backstopmedia.kotlin.ktwitter.entities.UserIdList
import com.twitter.sdk.android.core.Callback
import com.backstopmedia.kotlin.ktwitter.entities.UsersList
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable

class KTwitterApiClient(val session: TwitterSession) : TwitterApiClient(session) {

    val kTwitterApi: KTwitterApi by lazy { getService(KTwitterApi::class.java) }
}

interface KTwitterApi {

    @GET("/1.1/statuses/home_timeline.json")
    fun homeTimeline(@Query("count") count: Int = 20,
                     @Query("trim_user") trimUser: Boolean = true,
                     @Query("exclude_replies") excludeReplies: Boolean = true,
                     @Query("contributor_details") contributorDetails: Boolean = true,
                     @Query("include_entities") includeEntities: Boolean = true): Observable<List<Tweet>>

    @GET("/1.1/statuses/user_timeline.json")
    fun userTimeline(@Query("user_id") user: Long,
                     @Query("count") count: Int? = null): Observable<List<Tweet>>

    @GET("/1.1/users/show.json")
    fun getUser(@Query("screen_name") screenName: String, @Query("include_entities") includeEntities: Boolean = true): Observable<User>

    @GET("/1.1/favorites/list.json")
    fun getFaves(@Query("user_id") user: Long,
                 @Query("count") count: Int? = null): Observable<List<Tweet>>

    @GET("/1.1/friends/ids.json")
    fun getFollowing(@Query("user_id") user: Long): Observable<UserIdList>

    @GET("/1.1/followers/list.json")
    fun getFollowers(@Query("user_id") user: Long, @Query("count") count: Int? = null): Observable<UsersList>

    @POST("/1.1/friendships/create.json")
    fun follow(@Query("user_id") user: Long, @Query("follow") follow: Boolean): Observable<User>
}

fun KTwitterApi.getRetweets(user: Long, count: Int? = null): Observable<List<Tweet>> {
    return userTimeline(user, count).map { it.map { it.retweetedStatus }.filterNotNull() }
}

fun KTwitterApi.getFollowingIds(user: Long): Observable<Set<Long>> {
    return getFollowing(user).map { it.ids.toSet() }
}