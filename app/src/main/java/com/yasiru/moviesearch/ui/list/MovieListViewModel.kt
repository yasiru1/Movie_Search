package com.yasiru.moviesearch.ui.list

import android.util.Log
import com.yasiru.moviesearch.common.AbstractViewModel
import com.yasiru.moviesearch.network.KnownIssue
import androidx.lifecycle.viewModelScope
import com.yasiru.moviesearch.network.SearchResult
import com.yasiru.moviesearch.ui.list.interactor.SearchMoviesInteractor
import kotlinx.coroutines.launch


class MovieListViewModel constructor(
    private val searchMoviesInteractor: SearchMoviesInteractor
) : AbstractViewModel<MovieListViewState, MovieListSideEffect>(
    initialState = MovieListViewState(
        query = "",
        items = emptyList(),
        contentState = ContentState.Idle
    )
) {

    fun onEvent(event: MovieListEvent) = when (event) {
        MovieListEvent.Initialize -> initialize()
        is MovieListEvent.QueryChange -> onQueryChanged(event.newQuery)
        is MovieListEvent.EntryClicked -> pushSideEffect(
            MovieListSideEffect.BrowseMovie(event.entry.id, event.entry.title)
        )
    }

    private fun initialize() {
        viewModelScope.launch {
            try {
                searchMoviesInteractor
                    .searchChanges()
                    .collect { results ->
                        pushState {
                            it.copy(
                                items = results,
                                contentState = if (results.isEmpty()) {
                                    ContentState.NoResults
                                } else {
                                    ContentState.Idle
                                }
                            )
                        }
                    }
            } catch (e: Exception) {
                val contentState = if (e is KnownIssue.ApiLimitException) {
                    ContentState.ApiLimit
                } else {
                    if (e !is KnownIssue) {
                        e.message?.let { Log.e("", it) }
                    }
                    ContentState.Error
                }
                pushState {
                    it.copy(
                        items = emptyList(),
                        contentState = contentState
                    )
                }
            }
        }
    }

    private fun onQueryChanged(newQuery: String) {
        pushState {
            it.copy(
                query = newQuery,
                contentState = if (newQuery.length > 2) {
                    ContentState.Loading
                } else {
                    ContentState.Idle
                }
            )
        }
        searchMoviesInteractor.setQuery(newQuery)
    }
}

sealed class MovieListEvent {
    data class QueryChange(val newQuery: String) : MovieListEvent()
    data class EntryClicked(val entry: SearchResult) : MovieListEvent()
    object Initialize : MovieListEvent()
}

sealed class MovieListSideEffect {
    data class BrowseMovie(val movieId: String, val movieName: String) : MovieListSideEffect()
}

data class MovieListViewState(
    val query: String = "",
    val items: List<SearchResult> = emptyList(),
    val contentState: ContentState = ContentState.Idle
)

enum class ContentState {
    Loading,
    Idle,
    NoResults,
    Error,
    ApiLimit,
}
