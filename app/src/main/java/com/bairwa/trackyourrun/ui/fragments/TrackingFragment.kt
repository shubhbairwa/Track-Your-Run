package com.bairwa.trackyourrun.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.db.Run
import com.bairwa.trackyourrun.other.Constant.ACTION_PAUSE_SERVICE
import com.bairwa.trackyourrun.other.Constant.ACTION_START_OR_RESUME_SERVICE
import com.bairwa.trackyourrun.other.Constant.ACTION_STOP_SERVICE
import com.bairwa.trackyourrun.other.Constant.MAP_ZOOM
import com.bairwa.trackyourrun.other.Constant.POLYLINE_COLOR
import com.bairwa.trackyourrun.other.Constant.POLYLINE_WIDTH
import com.bairwa.trackyourrun.other.TrackingUtiltiy
import com.bairwa.trackyourrun.services.TrackingService
import com.bairwa.trackyourrun.services.polyline
import com.bairwa.trackyourrun.ui.viemodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round
const val CANCEL_TRACKING_DIALOG="CancelDialog"
@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private var isTracking = false
    private var pathPoint = mutableListOf<polyline>()
    private val viewmodel: MainViewModel by viewModels()
    private var currentTimeInMiliis = 0L
    private var map: GoogleMap? = null

    @set:Inject
     var weight = 80f

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }


        mapView.getMapAsync {
            map = it
            addAllPolyLines()
        }
        subscribeToObservers()


        if (savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(CANCEL_TRACKING_DIALOG) as CancelTrackingDialog?
cancelTrackingDialog?.setYesListener {
    stopRun()
}

        }


    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            upDateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoint = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMiliis = it
            val formattedTime = TrackingUtiltiy.getFormattedStopWatchTIme(currentTimeInMiliis, true)
            tvTimer.text = formattedTime
        })


    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMiliis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun showCancelTrackingDialog() {

        CancelTrackingDialog().apply {
    setYesListener {

        stopRun()
    }
}.show(parentFragmentManager,CANCEL_TRACKING_DIALOG)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_run ->
                showCancelTrackingDialog()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun stopRun() {
tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)

        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)

    }

    private fun upDateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && currentTimeInMiliis>0L) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking){
            btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE
        }

    }

    private fun moveCameraToUser() {
        if (pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last().last(), MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoint) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )


    }

    private fun endRunAndSaveToDb() {
        //for screenshot of map view
        map?.snapshot {
            var distanceInMeters = 0
            for (polyline in pathPoint) {
                distanceInMeters = TrackingUtiltiy.calculatePolyLineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (currentTimeInMiliis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(
                it,
                dateTimeStamp,
                avgSpeed,
                distanceInMeters,
                currentTimeInMiliis,
                caloriesBurned
            )
            viewmodel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
stopRun()
        }
    }


    private fun addAllPolyLines() {
        for (polyline in pathPoint) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 2) {
            val preLastLatLng = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatLng = pathPoint.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()

        mapView?.onStop()

    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


}