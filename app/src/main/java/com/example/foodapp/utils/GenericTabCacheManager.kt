package com.example.foodapp.utils

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TabData<T : Any>(
    val flow: MutableStateFlow<PagingData<T>> = MutableStateFlow(PagingData.empty()),
    var loadJob: Job? = null
)

class TabCacheManager<T : Any>(
    private val scope: CoroutineScope,
    private val getFilter: (Int) -> Any?,
    private val loadData: suspend (Any?) -> Flow<PagingData<T>>
) {
    private val _tabDataMap = MutableStateFlow<Map<Int, TabData<T>>>(emptyMap())
    val tabDataMap: StateFlow<Map<Int, TabData<T>>> = _tabDataMap.asStateFlow()

    fun getFlowForTab(tabIndex: Int = 0) {
        val currentMap = _tabDataMap.value
        val existing = currentMap[tabIndex]
        if (existing != null &&
            existing.loadJob?.isActive == true
        ) {
            return
        }

        val newData = existing ?: TabData()
        newData.loadJob?.cancel()

        val filter = getFilter(tabIndex)
        newData.loadJob = scope.launch {
            loadData(filter)
                .cachedIn(scope)
                .catch { newData.flow.value = PagingData.empty() }
                .collectLatest { pagingData ->
                    newData.flow.value = pagingData
                }
        }

        _tabDataMap.value = currentMap.toMutableMap().apply {
            put(tabIndex, newData)
        }

    }

    fun refreshAllTabs() {
        val map = _tabDataMap.value.toMutableMap()
        map.forEach { (_, data) ->
            data.loadJob?.cancel()
            data.flow.value = PagingData.empty()
        }
    }
}

