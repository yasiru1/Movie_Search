package com.yasiru.moviesearch.ui.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.yasiru.moviesearch.common.AbstractViewModel
import com.yasiru.moviesearch.ui.detail.usecase.GetMovieDetailsUseCase
import com.yasiru.moviesearch.ui.detail.usecase.MovieDetails
import com.yasiru.moviesearch.ui.detail.usecase.MovieDetailsResult
import kotlinx.coroutines.launch
import java.lang.Exception

class MovieDetailViewModel constructor(
    private val useCase: GetMovieDetailsUseCase
) : AbstractViewModel<MovieResponseState, MovieDetailSideEffect>(
    initialState = MovieResponseState.Loading
) {
    fun onEvent(event: MovieDetailEvent) = when (event) {
        MovieDetailEvent.BackPressed -> pushSideEffect(
            MovieDetailSideEffect.Back
        )
        is MovieDetailEvent.Initialize -> initialize(event.movieId)

    }

    private fun initialize(movieId: String) {
        viewModelScope.launch {
            val newState = when (val result = useCase.execute(movieId)) {
                MovieDetailsResult.Error.ApiLimit -> MovieResponseState.ApiLimit
                is MovieDetailsResult.Error.InternalIssue -> {
                    result.cause.message?.let { Log.e("", it) }
                    MovieResponseState.FailedToFetch
                }
                is MovieDetailsResult.Error.ApiIssue,
                MovieDetailsResult.Error.NetworkIssue -> MovieResponseState.FailedToFetch
                is MovieDetailsResult.Success -> {
                    MovieResponseState.Ready(result.details)
                }
            }
            pushState { newState }
        }
    }
}

sealed class MovieResponseState {
    data class Ready(val details: MovieDetails) : MovieResponseState()
    object Loading : MovieResponseState()
    object FailedToFetch : MovieResponseState()
    object ApiLimit : MovieResponseState()
}

sealed class MovieDetailEvent {
    data class Initialize(val movieId: String) : MovieDetailEvent()
    object BackPressed : MovieDetailEvent()
}

sealed class MovieDetailSideEffect {
    object Back : MovieDetailSideEffect()
}
