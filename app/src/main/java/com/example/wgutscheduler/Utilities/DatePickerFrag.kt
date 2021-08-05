package com.example.wgutscheduler.Utilities

import android.os.Bundle
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFrag : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(requireActivity(), activity as OnDateSetListener?, year, month, day)
    }
}