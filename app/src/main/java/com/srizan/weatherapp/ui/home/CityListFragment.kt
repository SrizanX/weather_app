package com.srizan.weatherapp.ui.home

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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted->
            if (!isGranted){
                Toast.makeText(requireContext(),"You will not receive any location update...",Toast.LENGTH_SHORT).show()
            }
        }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CityListViewModel::class.java)
        binding = CityListFragmentBinding.inflate(inflater, container, false)

        val cityListAdapter = CityListAdapter()
        viewModel.cityList.observe(viewLifecycleOwner) { cityList ->
            cityListAdapter.submitList(cityList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading->
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
        requestPermission()
        return binding.root
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                //Do nothing
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                //Show a snackbar with information why permission is needed
                showRationale()
            }
            else -> {
                launchRequestDialog()
            }
        }
    }

    private fun launchRequestDialog() {
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showRationale() {
        Snackbar.make(
            requireActivity().findViewById(R.id.nav_host_fragment),
            "Location permission is needed to provide you current temperature of your last location!",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("Allow") {
                launchRequestDialog()
            }
            .show()

    }
}