package com.bairwa.trackyourrun.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.other.CustomMarkerView
import com.bairwa.trackyourrun.other.TrackingUtiltiy
import com.bairwa.trackyourrun.ui.viemodels.MainViewModel
import com.bairwa.trackyourrun.ui.viemodels.StatisticViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticFragment:Fragment(R.layout.fragment_statistics) {
    private val viewmodel: StatisticViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObserver()
        setUpBarChart()
    }


    private fun setUpBarChart(){
        barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor=Color.WHITE
textColor=Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor=Color.WHITE
textColor= Color.WHITE
            setDrawLabels(false)
        }
        barChart.axisRight.apply {
            axisLineColor=Color.WHITE
            textColor= Color.WHITE
            setDrawLabels(false)
        }

        barChart.apply {
           description.text="Average Speed Over Time"
            legend.isEnabled=false
        }
    }





    private fun subscribeToObserver(){
        viewmodel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtiltiy.getFormattedStopWatchTIme(it)
                tvTotalTime.text = totalTimeRun
            }

        })
viewmodel.totalDistance.observe(viewLifecycleOwner, Observer {
    it?.let {
        val km=it / 1000f
        val totalDistance= round(km*10f)/10f

        val totalDistanceString="${totalDistance}km"
        tvTotalDistance.text=totalDistanceString
    }



})
        viewmodel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed= round(it*10f)/10f
                val avgSpeedString="${avgSpeed}km/h"
                tvAverageSpeed.text=avgSpeedString

            }


        })

        viewmodel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val caloriesBurned="${it}kcal"
                tvTotalCalories.text=caloriesBurned
            }


        })
        viewmodel.runSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeed=it.indices.map { i-> BarEntry(i.toFloat(),it[i].avgSpeedInKmh) }
                val barDataSet=BarDataSet(allAvgSpeed,"AVerage Speed Over Time").apply {
                    valueTextColor=Color.WHITE
                    color=ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }

                barChart.data= BarData(barDataSet)
                barChart.marker=CustomMarkerView(it,requireContext(),R.layout.marker_view)
                barChart.invalidate()


            }



        })







    }
}