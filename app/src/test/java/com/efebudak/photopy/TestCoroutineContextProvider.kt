package com.efebudak.photopy

import com.efebudak.photopy.utils.BaseCoroutineContextProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestCoroutineContextProvider:BaseCoroutineContextProvider {
    override val main: CoroutineContext
        get() = Dispatchers.Unconfined
    override val io: CoroutineContext
        get() = Dispatchers.Unconfined
}