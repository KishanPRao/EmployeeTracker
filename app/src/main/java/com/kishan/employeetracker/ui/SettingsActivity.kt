package com.kishan.employeetracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.DatePicker
import com.kishan.employeetracker.R
import kotlinx.android.synthetic.main.activity_settings.*
import org.joda.time.LocalDate

/**
 * Created by Kishan P Rao on 05/04/18.
 */
class SettingsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
	companion object {
		private val TAG = SettingsActivity::class.java.simpleName
	}
	
	override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
		Log.d(TAG, "onDateSet, $year, $month, $dayOfMonth")
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)
		val date = LocalDate.now()
		val startDatePicker = DatePickerDialog(this, this, date.year, date.monthOfYear - 1, date.dayOfMonth)
		val resignDatePicker = DatePickerDialog(this, this, date.year, date.monthOfYear - 1, date.dayOfMonth)
		startDateSetting.setOnClickListener {
			startDatePicker.show()
		}
		resignDateSetting.setOnClickListener {
			resignDatePicker.show()
		}
	}
}