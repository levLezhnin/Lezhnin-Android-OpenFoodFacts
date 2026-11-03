package ru.LezhninLev.openfoodfacts.core.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.LezhninLev.openfoodfacts.core.domain.usecase.ObserveFiltersUseCase

/**
 * Общий кэш для экрана списка и экрана настроек.
 * Показывать бейдж = есть хотя бы один активный фильтр.
 * Сам факт «показывали/не показывали» не персистится — пересчитывается из DataStore.
 */
class FilterBadgeCache(
    observeFiltersUseCase: ObserveFiltersUseCase,
    scope: CoroutineScope
) {
    private val _show = MutableStateFlow(false)
    val show: StateFlow<Boolean> = _show.asStateFlow()

    init {
        scope.launch {
            observeFiltersUseCase().collect { filters ->
                _show.value = filters.hasActive()
            }
        }
    }
}
