package com.yasiru.moviesearch.list

import app.cash.turbine.test
import com.yasiru.moviesearch.network.SearchResult
import com.yasiru.moviesearch.ui.list.ContentState
import com.yasiru.moviesearch.ui.list.MovieListEvent
import com.yasiru.moviesearch.ui.list.MovieListViewModel
import com.yasiru.moviesearch.ui.list.MovieListViewState
import com.yasiru.moviesearch.ui.list.interactor.SearchMoviesInteractor
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class MovieListViewModelTest {


    lateinit var viewModel: MovieListViewModel

    private val interactor = SearchMoviesInteractorMock()

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MovieListViewModel(interactor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user enters at least 2 letters, search is triggered`() = runBlocking {
        val matrixResults = listOf(searchResultOf("1"), searchResultOf("2"), searchResultOf("3"))
        val matrixReloadResults = listOf(searchResultOf("1"), searchResultOf("2"))

        interactor.putStubs(
            "the matrix" to FakeResult.Success(matrixResults),
            "the matrix reloaded" to FakeResult.Success(matrixReloadResults),
        )

        viewModel.viewState.test {
            assertEquals(
                MovieListViewState(
                    "",
                    emptyList(),
                    contentState = ContentState.Idle
                ), awaitItem()
            )
            viewModel.onEvent(MovieListEvent.Initialize)
            viewModel.onEvent(MovieListEvent.QueryChange("the matrix"))
            assertEquals(
                MovieListViewState(
                    "the matrix",
                    emptyList(),
                    contentState = ContentState.Loading
                ),
                awaitItem()
            )
            assertEquals(
                MovieListViewState(
                    "the matrix",
                    matrixResults,
                    contentState = ContentState.Idle
                ), awaitItem()
            )

            viewModel.onEvent(MovieListEvent.QueryChange("the matrix reloaded"))

            assertEquals(
                MovieListViewState(
                    "the matrix reloaded",
                    matrixReloadResults,
                    contentState = ContentState.Loading
                ),
                awaitItem()
            )
            assertEquals(
                MovieListViewState(
                    "the matrix reloaded",
                    matrixReloadResults,
                    contentState = ContentState.Idle
                ), awaitItem()
            )
        }
    }

    private fun searchResultOf(id: String): SearchResult {
        return SearchResult(
            id = id,
            imageUrl = "path/to/image",
            title = "Movie title",
            description = ""
        )
    }
}

private class SearchMoviesInteractorMock : SearchMoviesInteractor {
    private val fakes = hashMapOf<String, FakeResult>()
    private val queryFlow = MutableStateFlow("")

    override fun setQuery(newQuery: String) {
        queryFlow.value = newQuery
    }

    override fun searchChanges(): Flow<List<SearchResult>> {
        return queryFlow
            .filter { it.length > 3 }
            .map { query ->
                when (val item = requireNotNull(fakes[query]) {
                    "no fakes provided for query \'$query\'"
                }) {
                    is FakeResult.Failure -> throw item.issue
                    is FakeResult.Success -> item.items
                }
            }
    }

    fun putStubs(vararg args: Pair<String, FakeResult>) {
        args.forEach { (newQuery, newResult) ->
            fakes[newQuery] = newResult
        }
    }
}

private sealed class FakeResult {
    data class Success(val items: List<SearchResult>) : FakeResult()
    data class Failure(val issue: Throwable) : FakeResult()
}
