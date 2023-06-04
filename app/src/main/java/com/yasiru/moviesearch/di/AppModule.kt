package com.yasiru.moviesearch.di


import com.yasiru.moviesearch.BuildConfig
import com.yasiru.moviesearch.network.ApiConfig
import com.yasiru.moviesearch.network.ApiInterface
import com.yasiru.moviesearch.network.ApiInterfaceImpl
import com.yasiru.moviesearch.ui.detail.MovieDetailViewModel
import com.yasiru.moviesearch.ui.list.MovieListViewModel
import com.yasiru.moviesearch.ui.list.interactor.SearchMoviesInteractor
import com.yasiru.moviesearch.ui.list.interactor.SearchMoviesInteractorImpl
import com.yasiru.moviesearch.ui.detail.usecase.GetMovieDetailsUseCase
import com.yasiru.moviesearch.ui.detail.usecase.GetMovieDetailsUseCaseImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

//  config api key
    single<ApiConfig> { ApiConfig.Factory().create(BuildConfig.IMDB_API_KEY) }
    single<ApiInterface> { ApiInterfaceImpl(get()) }

    single<SearchMoviesInteractor> { SearchMoviesInteractorImpl(get()) }

    factory<GetMovieDetailsUseCase> { GetMovieDetailsUseCaseImpl(get()) }

    viewModel { MovieListViewModel(get()) }

    viewModel { MovieDetailViewModel(get()) }
}
