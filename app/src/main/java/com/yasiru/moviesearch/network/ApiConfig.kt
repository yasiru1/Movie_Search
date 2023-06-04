package com.yasiru.moviesearch.network

class ApiConfig private constructor(val apiKey: String) {

    val endpointUrl = "https://imdb-api.com/en/API/"

    class Factory {
        fun create(apiKey: String): ApiConfig {
            return ApiConfig( apiKey)
        }
    }
}