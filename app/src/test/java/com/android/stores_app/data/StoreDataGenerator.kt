package com.android.stores_app.data

import com.android.stores_app.data.model.Stores
import com.android.stores_app.data.model.StoresResponseData

object StoreDataGenerator {

    fun getSuccessStoresData(): StoresResponseData {
        return StoresResponseData(
            listOf(
                Stores(
                    storesID = 7026440,
                    storesName = "Tango Almere",
                    storesAddress = "Omroepweg",
                    storesPostalCode = "1324 KV",
                    storesCity = "Almere"
                )
            )
        )
    }

    fun getEmptyStoresData(): StoresResponseData {
        return StoresResponseData(listOf())
    }
}