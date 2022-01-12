package com.android.stores_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.stores_app.data.model.Stores
import com.android.stores_app.domain.StoresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StoresViewModel @Inject constructor(private val storesRepository: StoresRepository) :
    ViewModel() {

    private var currentLongitudeValue: Double? = null
    private var currentLatitudeValue: Double? = null
    private var storesFlow: Flow<PagingData<Stores>>? = null

    fun getStores(longitude: Double, latitude: Double): Flow<PagingData<Stores>> {
        val lastResult = storesFlow
        if (longitude == currentLongitudeValue && latitude == currentLatitudeValue && lastResult != null)
            return lastResult

        currentLongitudeValue = longitude
        currentLatitudeValue = latitude
        val newResult: Flow<PagingData<Stores>> =
            storesRepository.getStoresResultStream(longitude, latitude)
                .cachedIn(viewModelScope)

        storesFlow = newResult
        return newResult
    }
}