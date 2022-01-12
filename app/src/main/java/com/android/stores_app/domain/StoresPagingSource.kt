package com.android.stores_app.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.stores_app.common.util.Constants.RADIUS_VALUE
import com.android.stores_app.common.util.Constants.STARTING_PAGE_INDEX
import com.android.stores_app.common.util.Constants.TEST_APPLICATION_VALUE
import com.android.stores_app.data.api.StoresService
import com.android.stores_app.data.model.Stores
import retrofit2.HttpException
import java.io.IOException

class StoresPagingSource(
    private val storesService: StoresService,
    private val longitude: Double,
    private val latitude: Double
) : PagingSource<Int, Stores>() {
    override fun getRefreshKey(state: PagingState<Int, Stores>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stores> {
        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = storesService.getNearestPlacesAsync(
                longitude = longitude, latitude = latitude,
                radius = RADIUS_VALUE, page = position, itemsPerPage = params.loadSize,
                clientApplicationKey = TEST_APPLICATION_VALUE
            )

            val repos = response.stores
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                position + 1
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}