package com.efebudak.photopy

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)