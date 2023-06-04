package com.yasiru.moviesearch.detail

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.yasiru.moviesearch.ui.detail.MovieDetailEvent
import com.yasiru.moviesearch.ui.detail.MovieDetailViewModel
import com.yasiru.moviesearch.ui.detail.MovieResponseState
import com.yasiru.moviesearch.ui.detail.usecase.GetMovieDetailsUseCase
import com.yasiru.moviesearch.ui.detail.usecase.MovieDetails
import com.yasiru.moviesearch.ui.detail.usecase.MovieDetailsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MovieDetailViewModelTest {

    private val movieId = "100"

    lateinit var viewModel: MovieDetailViewModel

    val useCase: GetMovieDetailsUseCase = mock()

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MovieDetailViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when screen initialized, it loads and displays movie details`() = runBlocking {
        val details = MovieDetails(
            "boring movie",
            emptyList(),
            emptyList()
        )
        whenever(useCase.execute(movieId)).thenReturn(MovieDetailsResult.Success(details))

        viewModel.viewState.test {
            assertEquals(MovieResponseState.Loading, awaitItem())

            viewModel.onEvent(MovieDetailEvent.Initialize(movieId))
            assertEquals(MovieResponseState.Ready(details), awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `when screen initialized, it loads and displays error`() = runBlocking {
        whenever(useCase.execute(movieId)).thenReturn(MovieDetailsResult.Error.NetworkIssue)

        viewModel.viewState.test {
            assertEquals(MovieResponseState.Loading, awaitItem())

            viewModel.onEvent(MovieDetailEvent.Initialize(movieId))
            assertEquals(MovieResponseState.FailedToFetch, awaitItem())

            expectNoEvents()
        }
    }
}