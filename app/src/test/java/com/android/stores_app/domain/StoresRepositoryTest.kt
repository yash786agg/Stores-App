package com.android.stores_app.domain

import com.android.stores_app.commons.util.ConstantTest.Companion.STORE_ADDRESS
import com.android.stores_app.commons.util.ConstantTest.Companion.STORE_CITY
import com.android.stores_app.commons.util.ConstantTest.Companion.STORE_ID
import com.android.stores_app.commons.util.ConstantTest.Companion.STORE_NAME
import com.android.stores_app.commons.util.ConstantTest.Companion.STORE_POSTAL_CODE
import com.android.stores_app.commons.util.SequenceList
import com.android.stores_app.data.StoreDataGenerator.getEmptyStoresData
import com.android.stores_app.data.StoreDataGenerator.getSuccessStoresData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import com.nhaarman.mockitokotlin2.mock
import org.mockito.Mockito
import org.junit.Assert.*
import retrofit2.Response

@RunWith(JUnit4::class)
class StoresRepositoryTest {

    private val storesRepository: StoresRepository = mock()

    @Test
    fun verifyApiModelToEntityModelMustReturnSameValues() {

        val response = Response.success(getSuccessStoresData())

        runBlocking {
            Mockito.`when`(storesRepository.getStoresResultStream(anyDouble(), anyDouble()))
                .thenAnswer(SequenceList(listOf(response)))
        }

        val expectedResult = Response.success(getSuccessStoresData())

        assertEquals(expectedResult.body(), response.body())

        assertEquals(1, response.body()?.stores?.size)
        assertEquals(STORE_ID, response.body()?.stores?.get(0)?.storesID)
        assertTrue(response.body()?.stores?.get(0)?.storesName == STORE_NAME)
        assertTrue(response.body()?.stores?.get(0)?.storesAddress == STORE_ADDRESS)
        assertFalse(response.body()?.stores?.get(0)?.storesCity == STORE_POSTAL_CODE)
        assertFalse(response.body()?.stores?.get(0)?.storesPostalCode == STORE_CITY)
    }

    @Test
    fun verifyResultWhenRepoMockReturnEmptyState() {

        val response = Response.success(getEmptyStoresData())

        runBlocking {
            `when`(storesRepository.getStoresResultStream(anyDouble(), anyDouble())).thenAnswer(SequenceList(listOf(response)))
        }

        val expectedResult = Response.success(getEmptyStoresData())

        assertEquals(expectedResult.body(), response.body())

        assertEquals(0, response.body()?.stores?.size)
        assertNotNull(null,response.body()?.stores)
        response.body()?.stores?.isNullOrEmpty()?.let { assertTrue(it) }
        response.body()?.stores?.isNotEmpty()?.let { assertFalse(it) }
    }
}