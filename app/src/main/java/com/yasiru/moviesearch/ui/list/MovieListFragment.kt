package com.yasiru.moviesearch.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.yasiru.moviesearch.R
import com.yasiru.moviesearch.common.autoCleared
import com.yasiru.moviesearch.databinding.FragmentSearchBinding
import com.yasiru.moviesearch.databinding.ItemToolbarBinding
import com.yasiru.moviesearch.ui.MainActivity
import com.yasiru.moviesearch.ui.detail.MovieDetailFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MovieListFragment : Fragment() {

    private var binding by autoCleared<FragmentSearchBinding>()
    private val adapter = SearchAdapter()
    private val navigation by lazy { (requireActivity() as MainActivity).navigationWrapper }
    private val viewModel: MovieListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar(binding.toolbar)
        configureRecyclerView(binding.recyclerView)

//            call when search text submit
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
//            call when search text change
            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.onEvent(MovieListEvent.QueryChange(query.orEmpty()))
                return true
            }
        })
        viewModel.viewState.asLiveData().observe(viewLifecycleOwner, ::renderViewState)
        viewModel.sideEffects.asLiveData().observe(viewLifecycleOwner, ::handleSideEffect)

        viewModel.onEvent(MovieListEvent.Initialize)
    }

    private fun configureRecyclerView(recyclerView: RecyclerView) {
        adapter.listener = {
            viewModel.onEvent(MovieListEvent.EntryClicked(it))
        }
        recyclerView.adapter = adapter
    }
//  set app name in tool bar
    private fun configureToolbar(toolbar: ItemToolbarBinding) {
        toolbar.title.text = getString(R.string.app_name)
    }

    private fun handleSideEffect(effect: MovieListSideEffect?) = when (effect) {
        is MovieListSideEffect.BrowseMovie -> {
            navigation.pushScreen(
                MovieDetailFragment.newInstance(
                    effect.movieId,
                    effect.movieName
                ),
                "movie-detail"
            )
        }
        null -> Unit
    }

    private fun renderViewState(state: MovieListViewState) {
        adapter.submitList(state.items)

        binding.noResults.isVisible = state.contentState in listOf(
            ContentState.Error,
            ContentState.NoResults,
            ContentState.ApiLimit,
        )
        binding.progressbar.isVisible = state.contentState == ContentState.Loading
//      When result is no found or when error happen
        return when (state.contentState) {
            ContentState.Error -> {
                binding.noResultsText.setText(R.string.something_went_wrong)
            }
            ContentState.Idle -> Unit
            ContentState.Loading -> Unit
            ContentState.NoResults -> {
                binding.noResultsText.setText(R.string.search_no_results)
            }
            ContentState.ApiLimit -> {
                binding.noResultsText.setText(R.string.api_limit_exceed)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): MovieListFragment {
            return MovieListFragment()
        }
    }
}
