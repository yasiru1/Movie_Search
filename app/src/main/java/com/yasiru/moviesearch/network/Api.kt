package com.yasiru.moviesearch.network

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
//  Api to search movies by title
    @GET("SearchMovie/{apiKey}/{query}")
    suspend fun search(
        @Path("apiKey") apikey: String,
        @Path("query") query: String
    ): SearchResponse

    //  Api to get movie details by id
    @GET("Title/{apiKey}/{id}/{options}")
    suspend fun movieDetail(
        @Path("apiKey") apikey: String,
        @Path("id") id: String,
        @Path("options") options: String,
    ): MovieResponse

}