package com.sjianjun.retrofit.simple.http

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sjianjun.okhttp3.interceptor.HttpLoggingInterceptor
import com.sjianjun.retrofit.converter.GsonCharsetCompatibleConverter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class HttpClient private constructor(private val retrofit: Retrofit, val gson: Gson) {
    val http = retrofit.create(HttpInterface::class.java)

    /**
     * @see request
     */
    suspend inline fun <reified T> get(
        url: String,
        queryMap: Map<String, String> = emptyMap(),
        header: Map<String, String> = emptyMap()
    ): T {
        return request(T::class.java, url, queryMap, header, true)
    }

    /**
     * @see request
     */
    suspend inline fun <reified T> post(
        url: String,
        fieldMap: Map<String, String> = emptyMap(),
        header: Map<String, String> = emptyMap()
    ): T {
        return request(T::class.java, url, fieldMap, header, false)
    }

    /**
     * @see request
     */
    @JvmOverloads
    fun <T> get(
        typeOfT: Type,
        url: String,
        queryMap: Map<String, String> = emptyMap(),
        header: Map<String, String> = emptyMap()
    ): T = runBlocking {
        return@runBlocking request<T>(typeOfT, url, queryMap, header, true)
    }

    /**
     * @see request
     */
    @JvmOverloads
    fun <T> post(
        typeOfT: Type,
        url: String,
        fieldMap: Map<String, String> = emptyMap(),
        header: Map<String, String> = emptyMap()
    ): T = runBlocking {
        return@runBlocking request<T>(typeOfT, url, fieldMap, header, false)
    }

    /**
     *  @param typeOfT closs or [Type][com.google.gson.reflect.TypeToken.getType] see [Gson.fromJson][com.google.gson.Gson.fromJson]
     */
    suspend fun <T> request(
        typeOfT: Type,
        url: String,
        paramsMap: Map<String, String>,
        header: Map<String, String>, isGet: Boolean
    ): T {
        val resp = if (isGet) {
            http.get(url, paramsMap, header).await()
        } else {
            http.post(url, paramsMap, header).await()
        }

        if (typeOfT == String::class.java) {
            return resp as T
        }
        return gson.fromJson<T>(resp, typeOfT)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        @JvmName("create")
        operator fun invoke(level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY): HttpClient {
            return Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(level)
                )
                .addConverterFactory(GsonCharsetCompatibleConverter.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }

    }

    class Builder {

        var gson: Gson? = null

        var baseUrl = "https://github.com/SJJ-dot/"

        var clientBuilder: OkHttpClient.Builder? = null

        var retrofitBuilder: Retrofit.Builder? = null

        private var interceptors: MutableList<Interceptor>? = null
        private var converterFactorys: MutableList<Converter.Factory>? = null
        private var callAdapterFactorys: MutableList<CallAdapter.Factory>? = null

        fun addInterceptor(interceptor: Interceptor): Builder {
            if (interceptors == null) {
                interceptors = mutableListOf()
            }
            interceptors!!.add(interceptor)
            return this
        }

        fun addConverterFactory(factory: Converter.Factory): Builder {
            if (converterFactorys == null) {
                converterFactorys = mutableListOf()
            }
            converterFactorys?.add(factory)
            return this
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory): Builder {
            if (callAdapterFactorys == null) {
                callAdapterFactorys = mutableListOf()
            }
            callAdapterFactorys?.add(factory)
            return this
        }

        fun build(): HttpClient {
            val clientBuilder = clientBuilder ?: OkHttpClient.Builder()

            interceptors?.forEach {
                clientBuilder.addInterceptor(it)
            }


            val builder: Retrofit.Builder = retrofitBuilder ?: Retrofit.Builder()

            builder.baseUrl(baseUrl)

            builder.client(clientBuilder.build())

            callAdapterFactorys?.forEach {
                builder.addCallAdapterFactory(it)
            }
            converterFactorys?.forEach {
                builder.addConverterFactory(it)
            }

            return HttpClient(builder.build(), gson ?: Gson())
        }
    }

}