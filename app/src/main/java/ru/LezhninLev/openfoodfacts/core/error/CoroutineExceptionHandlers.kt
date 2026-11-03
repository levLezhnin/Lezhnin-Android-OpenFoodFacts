package ru.LezhninLev.openfoodfacts.core.error

import kotlinx.coroutines.CoroutineExceptionHandler

fun createCoroutineExceptionHandler(
    onException: (Throwable) -> Unit
): CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
    onException(exception)
}
