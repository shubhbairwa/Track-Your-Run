package com.bairwa.trackyourrun.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.other.Constant.KEY_FIRST_TIME_TOGGLE
import com.bairwa.trackyourrun.other.Constant.KEY_NAME
import com.bairwa.trackyourrun.other.Constant.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isAppFirstOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAppFirstOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setUpFragment, true).build()
            findNavController().navigate(
                R.id.action_setUpFragment_to_runFragment, savedInstanceState
                , navOptions

            )

            val saveName=   sharedPref.getString(KEY_NAME,"shu")

            val toolbarText = "let's go $saveName !"
            requireActivity().tvToolbarTitle.text = toolbarText



        }



        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setUpFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Require all field to fill", Snackbar.LENGTH_SHORT)
                    .show()
            }

        }


    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()


     val saveName=   sharedPref.getString(KEY_NAME,"shu")

        val toolbarText = "let's go $saveName !"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true

    }


}