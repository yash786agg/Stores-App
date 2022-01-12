package com.android.stores_app.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.stores_app.common.util.Constants.NETWORK_PAGE_SIZE
import com.android.stores_app.data.api.StoresService
import com.android.stores_app.data.model.Stores
import kotlinx.coroutines.flow.Flow

class StoresRepository(private val storesService: StoresService) {

    fun getStoresResultStream(longitude: Double, latitude: Double): Flow<PagingData<Stores>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { StoresPagingSource(storesService, longitude, latitude) }
        ).flow
    }
}