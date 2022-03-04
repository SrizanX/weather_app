package com.srizan.weatherapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.srizan.weatherapp.R
import com.srizan.weatherapp.databinding.CityListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CityListFragment : Fragment() {
    private lateinit var viewModel: CityListViewModel
    private lateinit var binding: CityListFragmentBinding

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // When network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            //Request data again if there was any error before
            if (viewModel.errorHappened.value == true) {
                viewModel.getCityList()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestFineLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showRationale(
                    getString(R.string.rationale_background_location),
                    "Ok"
                ) {
                    requestBackgroundLocation()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Background notification will not work without location permission!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestBackgroundPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CityListViewModel::class.java]
        binding = CityListFragmentBinding.inflate(inflater, container, false)

        val cityListAdapter = CityListAdapter()
        viewModel.cityList.observe(viewLifecycleOwner) { cityList ->
            cityListAdapter.submitList(cityList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.imageViewFailure.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.errorHappened.observe(viewLifecycleOwner) { error ->
            if (error) {
                binding.imageViewFailure.visibility = View.VISIBLE
            } else {
                binding.imageViewFailure.visibility = View.GONE
            }
        }

        binding.rcvCityList.adapter = cityListAdapter

        //List Separator of the recyclerview
        binding.rcvCityList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, networkCallback)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestLocationPermission()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showRationale(
                        getString(R.string.rationale_background_location),
                        "Ok"
                    ) {
                        requestBackgroundLocation()
                    }
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                //Show a snackbar with information why permission is needed
                showRationale(getString(R.string.rationale), "Allow") {
                    requestFineLocation()
                }
            }
            else -> {
                requestFineLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestFineLocation() {
        requestFineLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocation() {
        requestBackgroundPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showRationale(message: String, action: String, block: () -> Unit) {
        Snackbar.make(
            requireActivity().findViewById(R.id.nav_host_fragment),
            message,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(action) {
            block.invoke()
        }.show()
    }
}