package com.android.stores_app.data.api

import com.android.stores_app.BuildConfig
import com.android.stores_app.common.util.Constants.CLIENT_APPLICATION_KEY_QUERY_TAG
import com.android.stores_app.common.util.Constants.CONTENT_TYPE_TAG
import com.android.stores_app.common.util.Constants.LATITUDE_QUERY_TAG
import com.android.stores_app.common.util.Constants.LONGITUDE_QUERY_TAG
import com.android.stores_app.common.util.Constants.PAGE_QUERY_TAG
import com.android.stores_app.common.util.Constants.PAGE_SIZE_QUERY_TAG
import com.android.stores_app.common.util.Constants.RADIUS_QUERY_TAG
import com.android.stores_app.common.util.Constants.STORES_URL
import com.android.stores_app.data.model.StoresResponseData
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface StoresService {

    @Headers(CONTENT_TYPE_TAG)
    @GET(BuildConfig.BASE_URL + STORES_URL)
    suspend fun getNearestPlacesAsync(
        @Query(LONGITUDE_QUERY_TAG) longitude: Double,
        @Query(LATITUDE_QUERY_TAG) latitude: Double,
        @Query(RADIUS_QUERY_TAG) radius: Long,
        @Query(PAGE_QUERY_TAG) page: Int,
        @Query(PAGE_SIZE_QUERY_TAG) itemsPerPage: Int,
        @Query(CLIENT_APPLICATION_KEY_QUERY_TAG) clientApplicationKey: String
    ): StoresResponseData
}