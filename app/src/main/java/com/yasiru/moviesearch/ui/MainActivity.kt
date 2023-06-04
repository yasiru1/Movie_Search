package com.yasiru.moviesearch.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.yasiru.moviesearch.R
import com.yasiru.moviesearch.ui.list.MovieListFragment


class MainActivity : AppCompatActivity() {


    val navigationWrapper by lazy { NavigationWrapper(this, R.id.container) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigationWrapper.pushScreen(MovieListFragment.newInstance(), "movie-list")
        }
    }

}
