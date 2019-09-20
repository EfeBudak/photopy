package com.efebudak.photopy.di

import com.efebudak.photopy.BuildConfig
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {

    single<Retrofit> { Retrofit.Builder().baseUrl(BuildConfig.APPLICATION_ID).build() }
}