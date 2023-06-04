package com.yasiru.moviesearch.ui.detail.usecase

import com.yasiru.moviesearch.network.Actor
import com.yasiru.moviesearch.network.ApiInterface
import com.yasiru.moviesearch.network.ImageItem
import com.yasiru.moviesearch.network.KnownIssue
import com.yasiru.moviesearch.network.MovieResponse
import org.intellij.lang.annotations.Language


interface GetMovieDetailsUseCase {
    suspend fun execute(movieId: String): MovieDetailsResult
}

class GetMovieDetailsUseCaseImpl(private val clientImpl: ApiInterface) : GetMovieDetailsUseCase {
    override suspend fun execute(movieId: String): MovieDetailsResult {
        try {
            val response = clientImpl.getMovieDetail(movieId)

            val details = MovieDetails(
                metadata = buildMetadata(response),
                images = response.images?.items.orEmpty(),
                actors = response.actors.orEmpty()
            )
            return MovieDetailsResult.Success(details)
        } catch (e: Exception) {
            return when (e) {
                is KnownIssue -> when (e) {
                    is KnownIssue.ApiException -> {
                        MovieDetailsResult.Error.ApiIssue(e.code)
                    }
                    KnownIssue.ApiLimitException -> {
                        MovieDetailsResult.Error.ApiLimit
                    }
                    is KnownIssue.NetworkException -> {
                        MovieDetailsResult.Error.NetworkIssue
                    }
                }
                else -> {
                    MovieDetailsResult.Error.InternalIssue(e)
                }
            }
        }
    }
//  Build movie description
    @Language("HTML")
    fun buildMetadata(response: MovieResponse): String {
        val ratingLine = " <b>IMDB  Rating</b>: <br>".plus(response.rating.orEmpty())
        val releaseDateLine = "<br> <b>Release  Date</b>: <br>".plus(response.releaseDate.orEmpty())
        val plotLine = "<br> <b>Plot</b>: <br>".plus(response.plot.orEmpty())

        return ratingLine + releaseDateLine + plotLine
    }




}

sealed class MovieDetailsResult {
    data class Success(val details: MovieDetails) : MovieDetailsResult()
    sealed class Error : MovieDetailsResult() {
        object NetworkIssue : Error()
        object ApiLimit : Error()
        data class ApiIssue(val code: Int) : Error()
        data class InternalIssue(val cause: Exception) : Error()
    }
}

data class MovieDetails(
    @Language("HTML")
    val metadata: String,
    val images: List<ImageItem>,
    val actors: List<Actor>
)

