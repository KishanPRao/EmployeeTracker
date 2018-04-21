package com.kishan.employeetracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kishan.employeetracker.R
import com.kishan.employeetracker.data.DateStorage
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.fragment_notice_period.*
import org.joda.time.Days
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
		private const val MAX_TIME_MS = AnimationUtils.ANIM_TIME
	}
	
	//	private val startDate = LocalDate(2018, 2, 21)
	private lateinit var startDate: LocalDate
	private var noticePeriodDays = 0
	private var daysCompleted = 0
	private var now = LocalDate.now()
	
	private val mInterpolator = AnimationUtils.INTERPOLATOR
	private var mStartTime = -1
	private var mIncrement = 1
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_notice_period, container, false)
	}
	
	private fun updateIncrement() {
		if (mStartTime == -1) {
			mStartTime = System.currentTimeMillis().toInt()
		}
		val currentTime = System.currentTimeMillis().toInt()
		val elapsed = (currentTime - mStartTime)
		var amount = elapsed / MAX_TIME_MS.toFloat()
		if (amount > 1.0f) {
			amount = 1.0f
		}
		val amountModified = mInterpolator.getInterpolation(amount)
//		Log.d(TAG, "updateIncrement, $amount -> $amountModified")
		amount = amountModified
		val progress = (daysCompleted * 100) / noticePeriodDays
		val totalRotation = progress
		val rotationForCurrentTime = amount * totalRotation
		val diff = rotationForCurrentTime - progressArc.progress
		mIncrement = diff.toInt()
//		Log.d(TAG, "updateIncrement, $elapsed, $amount, $totalRotation")
//		Log.d(TAG, "updateIncrement, Rotation: $rotationForCurrentTime, $mNumRotationFinished, $diff")
	}
	
	private fun tryAnimateArc() {
		val progress = (daysCompleted * 100) / noticePeriodDays
		val progressArc: SeekArc? = progressArc
		progressArc?.let {
//			updateIncrement()
			if (progressArc.progress < progress) {
				progressArc.progress++
//				progressArc.progress += mIncrement
				progressArc.postDelayed({
					tryAnimateArc()
				}, 16)
			}
		}
	}
	
	private fun animateArc() {
//		TODO: Use Timing Curve, within 2 seconds. Build proper class for this.
//		val progress = (daysCompleted * 100) / noticePeriodDays
//		Log.d(TAG, "animateArc, $daysCompleted, $progress")
		progressArc.progress = 0
		tryAnimateArc()
	}
	
	fun reset() {
		mStartTime = -1
		mIncrement = 1
		try {
			first_text.reset()
			second_text.reset()
			third_text.reset()
			animateArc()
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
		startDate = dateStorage.getResignDate()
		noticePeriodDays = dateStorage.getNoticePeriod()
//		daysCompleted = Days.daysBetween(LocalDate.now(), startDate)
		daysCompleted = Days.daysBetween(startDate, LocalDate.now()).days
		
		val lastDate = startDate.plusDays(noticePeriodDays)
		val period = Period(now, lastDate, PeriodType.yearMonthDay())
		val years = period.years
		val months = period.months
//		val days = period.days      //Includes end date TODO: Right?
		val days = period.days - 1
		Log.d(TAG, "onViewCreated, diff = { $years : $months : $days }")
		first_text.number = years
		second_text.number = months
		third_text.number = days
		try {
			animateArc()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}