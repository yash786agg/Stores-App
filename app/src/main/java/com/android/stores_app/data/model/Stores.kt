package com.android.stores_app.data.model

import android.os.Parcelable
import com.android.stores_app.common.util.Constants.ADDRESS_API_TAG
import com.android.stores_app.common.util.Constants.CITY_API_TAG
import com.android.stores_app.common.util.Constants.NAME_API_TAG
import com.android.stores_app.common.util.Constants.POSTAL_CODE_API_TAG
import com.android.stores_app.common.util.Constants.STORE_ID_API_TAG
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stores(
    @field:Json(name = STORE_ID_API_TAG) val storesID: Long,
    @field:Json(name = NAME_API_TAG) val storesName: String,
    @field:Json(name = ADDRESS_API_TAG) val storesAddress: String,
    @field:Json(name = POSTAL_CODE_API_TAG) val storesPostalCode: String,
    @field:Json(name = CITY_API_TAG) val storesCity: String
) : Parcelable