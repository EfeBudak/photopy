package com.efebudak.photopy.utils

import kotlin.coroutines.CoroutineContext

interface BaseCoroutineContextProvider {
    val main: CoroutineContext
    val io: CoroutineContext
}