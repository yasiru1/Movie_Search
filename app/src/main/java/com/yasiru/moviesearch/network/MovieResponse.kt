package com.yasiru.moviesearch.network

import com.google.gson.annotations.SerializedName
// required data list to show movie details
data class MovieResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("actorList") val actors: List<Actor>?,
    @SerializedName("imDbRating") val rating: String?,
    @SerializedName("errorMessage") val errorMessage: String?,
    @SerializedName("images") val images: ImagesResponse?

)



data class Actor(
    @SerializedName("id") val id: String,
    @SerializedName("image") val image: String,
    @SerializedName("name") val name: String,
    @SerializedName("asCharacter") val asCharacter: String
)
