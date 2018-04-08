package com.kishan.employeetracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kishan.employeetracker.R
import kotlinx.android.synthetic.main.fragment_notice_period.*
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType

/**
 * Created by Kishan P Rao on 03/04/18.
 */
class NoticePeriodFragment : Fragment() {
//	TODO: Common fragment for date specific content.
	
	companion object {
		private val TAG = NoticePeriodFragment::class.java.simpleName
	}
	
	private val startDate = LocalDate(2018, 2, 21)
	private val noticePeriodDays = 90
	private var now = LocalDate.now()
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_notice_period, container, false)
	}
	
	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		val lastDate = startDate.plusDays(noticePeriodDays)
		val period = Period(now, lastDate, PeriodType.yearMonthDay())
		val years = period.years
		val months = period.months
//		val days = period.days      //Includes end date TODO: Right?
		val days = period.days - 1
		Log.d(TAG, "onViewCreated, diff = { $years : $months : $days }")
		first_text.text = years.toString()
		second_text.text = months.toString()
		third_text.text = days.toString()
	}
}