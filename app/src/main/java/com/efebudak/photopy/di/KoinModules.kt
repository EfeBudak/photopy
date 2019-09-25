package com.efebudak.photopy.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.efebudak.photopy.BuildConfig
import com.efebudak.photopy.PhotopyViewModelFactory
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.data.source.PhotosRepository
import com.efebudak.photopy.data.source.remote.PhotosRemoteDataSource
import com.efebudak.photopy.network.FlickrService
import com.efebudak.photopy.ui.search.SearchContract
import com.efebudak.photopy.ui.search.SearchStateHolder
import com.efebudak.photopy.ui.search.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {

    single<Retrofit>(createdAtStart = true) {
        val loggingIntercepter = HttpLoggingInterceptor()
        loggingIntercepter.level = HttpLoggingInterceptor.Level.BODY
        val okHttp = OkHttpClient.Builder().addInterceptor(loggingIntercepter).build()
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single<FlickrService> {
        (get() as Retrofit).create(FlickrService::class.java)
    }

    single<PhotosDataSource>(named(REMOTE_DATA_SOURCE)) { PhotosRemoteDataSource(get()) }
    single<PhotosDataSource>(named(REPOSITORY_DATA_SOURCE)) {
        PhotosRepository(get(named(REMOTE_DATA_SOURCE)))
    }
    factory<SearchContract.StateHolder> { SearchStateHolder() }

    factory { (fragment: Fragment) ->
        ViewModelProviders.of(
            fragment,
            PhotopyViewModelFactory(
                get(named(REPOSITORY_DATA_SOURCE)),
                get()
            )
        )
            .get(SearchViewModel::class.java)
    }

}

private const val REMOTE_DATA_SOURCE = "remoteDataSource"
private const val REPOSITORY_DATA_SOURCE = "repositoryDataSource"