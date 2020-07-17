package com.bairwa.trackyourrun.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.adapters.RunAdapter
import com.bairwa.trackyourrun.other.Constant.REQUEST_CODE_LOCATION
import com.bairwa.trackyourrun.other.TrackingUtiltiy
import com.bairwa.trackyourrun.ui.viemodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks{

   lateinit var runAdapter:RunAdapter
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermission()
        }
    }

    private fun setUpRecyclerView()=rvRuns.apply {
        runAdapter=RunAdapter()
      adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    private val viewmodel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
setUpRecyclerView()
        requestPermission()
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        viewmodel.runSortedByDate.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)

        })


    }

    private fun requestPermission() {
        if (TrackingUtiltiy.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this, "You need to accept location Permission for this app."
                , REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this, "You need to accept location Permission for this app."
                , REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION

                )

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)


    }
}