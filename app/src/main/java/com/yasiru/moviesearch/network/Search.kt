package com.yasiru.moviesearch.network


import com.google.gson.annotations.SerializedName
import java.util.*
// required data  to show search results
data class SearchResponse(
    @SerializedName("expression") val expression: String,
    @SerializedName("results") val results: List<SearchResult>?,
    @SerializedName("errorMessage") val errorMessage: String?
)

data class SearchResult(
    @SerializedName("id") val id: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)


