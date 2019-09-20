package com.efebudak.photopy

import android.app.Application
import com.efebudak.photopy.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PhotopyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PhotopyApplication)
            modules(appModule)
        }

    }
}