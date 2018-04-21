package com.kishan.employeetracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kishan.employeetracker.R
import com.kishan.employeetracker.data.DateStorage
import kotlinx.android.synthetic.main.fragment_worked_for.*
import org.joda.time.*


/**
 * Created by Kishan P Rao on 03/04/18.
 */
class WorkedForFragment : Fragment() {
	companion object {
		private val TAG = WorkedForFragment::class.java.simpleName
	}
	
	//	private val startDate = LocalDate(2016, 5, 16)
	private lateinit var startDate: LocalDate
	private var now = LocalDate.now()
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		
		/*val monthsBetween = Months.monthsBetween(startDate, now).months
		val yearsBetween = Years.yearsBetween(startDate, now).years
		val daysBetween = Days.daysBetween(startDate, now).days
		Log.d(TAG, "onCreateView, $yearsBetween, $monthsBetween, $daysBetween")*/
		return inflater.inflate(R.layout.fragment_worked_for, container, false)
	}
	
	fun reset() {
		try {
			first_text.reset()
			second_text.reset()
			third_text.reset()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	override fun onResume() {
		super.onResume()
//		reset()
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val dateStorage = DateStorage(context!!)
		startDate = dateStorage.getStartDate()
		Log.d(TAG, "onCreateView, $startDate, $now")
		val period = Period(startDate, now, PeriodType.yearMonthDay())
		val years = period.years
		val months = period.months
		val days = period.days
		Log.d(TAG, "onViewCreated, diff = { $years : $months : $days }")
		first_text.number = years
		second_text.number = months
		third_text.number = days
	}
}