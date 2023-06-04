package com.yasiru.moviesearch.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception
import kotlin.jvm.Throws


interface ApiInterface {

    suspend fun search( query: String): SearchResponse
//  to handle API daily limit
    @Throws(KnownIssue.ApiLimitException::class)
    suspend fun getMovieDetail(id: String): MovieResponse
}

class ApiInterfaceImpl constructor(
    private val config: ApiConfig
) : ApiInterface {

    private val api by lazy {
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .build()
            )
            .baseUrl(config.endpointUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    override suspend fun search( query: String): SearchResponse {
        try {
            val response = api.search( config.apiKey, query)
            if (response.errorMessage.isApiLimit()) {
                throw KnownIssue.ApiLimitException
            }
            return response
        } catch (e: Throwable) {
            handleError(e)
        }
    }

//  send request to get movie posters (images) and list of actors
    override suspend fun getMovieDetail(id: String): MovieResponse {
        try {
            val response = api.movieDetail(
                config.apiKey,
                id,
                options = "FullActor,Images"
            )
            if (response.errorMessage.isApiLimit()) {
                throw KnownIssue.ApiLimitException
            }
            return response
        } catch (e: Throwable) {
            handleError(e)
        }
    }
//  when error happened
    private fun handleError(e: Throwable): Nothing = when (e) {
        is HttpException -> {
            throw KnownIssue.ApiException(e.code())
        }
        is IOException -> {
            throw KnownIssue.NetworkException(e)
        }
        else -> throw e
    }
//  to identify api limit
    private fun String?.isApiLimit(): Boolean {
        return this.orEmpty().contains("Maximum usage")
    }
}

sealed class KnownIssue(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    object ApiLimitException : KnownIssue()

    data class ApiException(val code: Int) : KnownIssue("errorCode=$code")

    data class NetworkException(val issue: Throwable) : KnownIssue(cause = issue)
}
