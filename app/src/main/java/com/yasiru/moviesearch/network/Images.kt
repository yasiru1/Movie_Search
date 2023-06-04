package com.yasiru.moviesearch.network

import com.google.gson.annotations.SerializedName
// poster response
data class ImagesResponse(
    @SerializedName("imDbId") val id: String,
    @SerializedName("items") val items: List<ImageItem>
)

data class ImageItem(
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String
)
