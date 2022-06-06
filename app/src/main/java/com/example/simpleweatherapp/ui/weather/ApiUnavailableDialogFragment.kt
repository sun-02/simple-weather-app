package com.example.simpleweatherapp.ui.weather

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.simpleweatherapp.R
import timber.log.Timber

class ApiUnavailableDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ApiUnavailableDialog"
    }

    private var _listener: DialogInterface.OnClickListener? = null
    private val listener get() = _listener!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            _listener = targetFragment as DialogInterface.OnClickListener
        } catch (e: Exception) {
            Timber.e("Can't get TargetFragment: $e")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.weather_unavailable))
            .setPositiveButton(R.string.retry, listener)
            .setNegativeButton(R.string.exit, listener)
            .create()

}