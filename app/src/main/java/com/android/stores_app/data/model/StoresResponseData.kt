package com.android.stores_app.data.model

import android.os.Parcelable
import com.android.stores_app.common.util.Constants.STORES_API_TAG
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoresResponseData(@field:Json(name = STORES_API_TAG) val stores: List<Stores>) :
    Parcelable
