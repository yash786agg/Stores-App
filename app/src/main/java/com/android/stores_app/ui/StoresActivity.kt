package com.android.stores_app.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.stores_app.R
import com.android.stores_app.common.helper.PermissionHelper
import com.android.stores_app.common.helper.PermissionHelper.Companion.PERMISSIONS_REQUEST_LOCATION
import com.android.stores_app.common.location.GpsSetting
import com.android.stores_app.common.location.LocationViewModel
import com.android.stores_app.common.helper.UiHelper
import com.android.stores_app.common.location.GpsEnableListener
import com.android.stores_app.common.location.nonNull
import com.android.stores_app.databinding.ActivityStoresBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class StoresActivity : AppCompatActivity(), PermissionHelper.OnPermissionRequested {
    // View Binding:- https://developer.android.com/topic/libraries/view-binding#activities
    private lateinit var activityStoresBinding: ActivityStoresBinding
    private val storesViewModel: StoresViewModel by viewModels()
    private val locationVM: LocationViewModel by viewModels()

    @Inject
    lateinit var uiHelper: UiHelper
    private var gpsSetting: GpsSetting? = null
    private var permissionHelper: PermissionHelper? = null
    private val storesAdapter = StoresAdapter()
    private var isPermissionPermanentlyDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityStoresBinding = ActivityStoresBinding.inflate(layoutInflater)
        setContentView(activityStoresBinding.root) // activity_main.xml
        initRecyclerView()

        gpsSetting = GpsSetting(this, uiHelper)

        permissionHelper = PermissionHelper(this, uiHelper)

        checkNetwork()
    }

    private fun checkNetwork() {
        if (uiHelper.isNetworkAvailable()) {
            if (!permissionHelper?.isPermissionGranted(ACCESS_FINE_LOCATION)!!)
                permissionHelper?.requestPermission(
                    arrayOf(ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_LOCATION,
                    this
                )
            else enableGps()
        } else uiHelper.showSnackBar(
            activityStoresBinding.storeActivityRv,
            resources.getString(R.string.error_network_connection)
        )
    }

    override fun onResume() {
        super.onResume()

        if (isPermissionPermanentlyDenied) checkPermissionGranted()
    }

    /*
     * Checking whether Location Permission is granted or not.
     * */

    private fun checkPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            permissionHelper?.openSettingsDialog()
        else enableGps()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * This function is to get the result form [PermissionHelper] class
     *
     * @param isPermissionGranted the [Boolean]
     */

    override fun onPermissionResponse(isPermissionGranted: Boolean) {

        if (!isPermissionGranted) isPermissionPermanentlyDenied = true
        else enableGps()
    }

    private fun enableGps() {
        isPermissionPermanentlyDenied = false

        if (!uiHelper.isLocationProviderEnabled()) subscribeLocationObserver()
        else gpsSetting?.openGpsSettingDialog(resolutionForResult)
    }

    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> subscribeLocationObserver()

                RESULT_CANCELED -> {
                    uiHelper.showPositiveDialogWithListener(
                        this,
                        resources.getString(R.string.need_location),
                        resources.getString(R.string.location_content),
                        object : GpsEnableListener {
                            override fun onPositive() {
                                enableGps()
                            }
                        }, resources.getString(R.string.turn_on), false
                    )
                }
            }
        }

    // Start Observing the User Current Location and set the marker to it.
    private fun subscribeLocationObserver() {
        showProgressBar(true)

        // OBSERVABLES ---
        locationVM.currentLocation.nonNull().observe(this, {
            showProgressBar(false)
            initObservable(longitude = it.longitude, latitude = it.latitude)
            locationVM.stopLocationUpdates()
        })

        locationVM.requestLocationUpdates()
    }

    private fun initObservable(longitude: Double, latitude: Double) {
        lifecycleScope.launchWhenStarted {
            storesViewModel.getStores(longitude = longitude, latitude = latitude).collectLatest {
                storesAdapter.submitData(it)
            }
        }
    }

    // Setup the adapter class for the RecyclerView
    private fun initRecyclerView() {
        activityStoresBinding.rcvStores.layoutManager = LinearLayoutManager(this)
        activityStoresBinding.rcvStores.adapter = storesAdapter

        storesAdapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && storesAdapter.itemCount == 0
            showEmptyList(isEmpty = isListEmpty)

            if (loadState.source.refresh is LoadState.Loading)
                showProgressBar(true)
            else if (loadState.source.refresh is LoadState.NotLoading)
                showProgressBar(false)

            // getting the error
            val errorState = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {
                uiHelper.showSnackBar(
                    activityStoresBinding.storeActivityRv,
                    it.error.message.toString()
                )
            }
        }
    }

    private fun showEmptyList(isEmpty: Boolean) {
        if (isEmpty)
            activityStoresBinding.noDataTxt.visibility = View.VISIBLE
        else
            activityStoresBinding.noDataTxt.visibility = View.GONE
    }

    // UPDATE UI ----
    private fun showProgressBar(display: Boolean) {
        if (!display) activityStoresBinding.progressBar.visibility = View.GONE
        else activityStoresBinding.progressBar.visibility = View.VISIBLE
    }
}