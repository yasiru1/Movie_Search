package com.yasiru.moviesearch.ui.list.interactor

import com.yasiru.moviesearch.network.ApiInterface
import com.yasiru.moviesearch.network.SearchResult
import com.yasiru.moviesearch.ui.list.interactor.SearchMoviesInteractor.Companion.DEBOUNCE_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

interface SearchMoviesInteractor {

    companion object {
        const val DEBOUNCE_MILLIS = 300L
    }

    fun setQuery(newQuery: String)

    fun searchChanges(): Flow<List<SearchResult>>
}

class SearchMoviesInteractorImpl(
    private val client: ApiInterface
) : SearchMoviesInteractor {

    private val query = MutableStateFlow("")

    override fun setQuery(query: String) {
        this.query.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchChanges(): Flow<List<SearchResult>> {
        return query
            .filter { it.isNotBlank() }
            .debounce(DEBOUNCE_MILLIS)
            .mapLatest { query ->
                client.search( query).results.orEmpty()
            }
    }
}
