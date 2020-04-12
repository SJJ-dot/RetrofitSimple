package com.sjianjun.retrofit.simple.http

import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface HttpInterface {

    @GET
    fun get(@Url url: String, @QueryMap(encoded = true) queryMap: Map<String, String> = emptyMap(), @HeaderMap header: Map<String, String> = emptyMap()): Deferred<String>


    @FormUrlEncoded
    @POST
    fun post(@Url url: String = "", @FieldMap(encoded = true) fieldMap: Map<String, String> = emptyMap(), @HeaderMap header: Map<String, String> = emptyMap()): Deferred<String>

}