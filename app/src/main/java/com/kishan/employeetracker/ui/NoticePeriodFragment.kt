package com.kishan.employeetracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kishan.employeetracker.R
import com.kishan.employeetracker.data.DataStorage
import com.kishan.employeetracker.data.TimeMode
import com.kishan.employeetracker.utils.TimeUtils
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.fragment_notice_period.*
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.*
import java.util.*

/**
 * Created by Kishan P Rao on 03/04/18.
 */
class NoticePeriodFragment : Fragment() {
//	TODO: Common fragment for date specific content.
	
	companion object {
		private val TAG = NoticePeriodFragment::class.java.simpleName
		private const val MAX_TIME_MS = AnimationUtils.ANIM_TIME
		val Int.clampNegative: Int get() = if (this < 0) 0 else this
	}
	
	//	private val startDate = LocalDate(2018, 2, 21)
//	private lateinit var startDate: LocalDate
	private lateinit var startDate: LocalDateTime
	private var noticePeriodDays = 0
	private var daysCompleted = 0
	
	private val mInterpolator = AnimationUtils.INTERPOLATOR
	private var mStartTime = -1
	private var mIncrement = 1
	
	private var mode = TimeMode.YMD
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		JodaTimeAndroid.init(this.context)
		DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()))
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
	
	private fun resignDone() {
		val newContext = context
		newContext?.apply {
			progressArc.progressColor = ContextCompat.getColor(newContext, R.color.progressFull)
			notice_finished_text.visibility = View.VISIBLE
		}
	}
	
	private fun tryAnimateArc() {
		var progress = (daysCompleted * 100) / noticePeriodDays
		if (progress > 100) {
			progress = 100
		}
		val progressArc: SeekArc? = progressArc
		progressArc?.apply {
			//			updateIncrement()
			if (progressArc.progress < progress) {
				progressArc.progress++
//				progressArc.progress += mIncrement
				progressArc.postDelayed({
					tryAnimateArc()
				}, 16)
			} else if (progressArc.progress == 100) {
				resignDone()
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
	
	private fun toggleMode() {
		if (mode == TimeMode.YMD) {
			mode = TimeMode.HMS
		} else {
			mode = TimeMode.YMD
		}
		updateMode()
	}
	
	private fun changeText(textView: TextView, text: String, delay: Long) {
		val totalDuration = 500L
		val duration = totalDuration / 2
		textView.animate().alpha(0f).setDuration(duration).withEndAction({
			textView.text = text
//			textView.animate().setDuration(duration).setStartDelay(delay).alpha(1f).start()
			textView.animate().setDuration(duration).alpha(1f).start()
		}).start()
	}
	
	var track = false
	
	private fun trackIfNeeded() {
		if (track) {
			val delayInMs = 1000L
			val third_text: FlipCardView? = third_text
			third_text?.apply {
//				Assuming HMS mode
				val now = TimeUtils.getCurrentTime()
				val lastDate = startDate.plusDays(noticePeriodDays)
				val period = Period(now, lastDate, PeriodType.time())
				first_text.number = period.hours.clampNegative
				second_text.number = period.minutes.clampNegative
				third_text.number = period.seconds.clampNegative
				
				third_text.postDelayed({
					trackIfNeeded()
				}, delayInMs)
			}
		}
	}
	
	private fun stopTracking() {
		track = false
	}
	
	private fun updateMode() {
		stopTracking()
		val dateStorage = DataStorage(context!!)
		startDate = dateStorage.getResignDateTime()
		var firstData = 0
		var secondData = 0
		var thirdData = 0
//		mode = TimeMode.HMS
		var firstLabel = ""
		var secondLabel = ""
		var thirdLabel = ""
		when (mode) {
			TimeMode.YMD -> {
				val lastDate = dateStorage.getResignDate().plusDays(noticePeriodDays)
				val period = Period(TimeUtils.getCurrentDate(), lastDate, PeriodType.yearMonthDay())
				val years = period.years
				val months = period.months
				val days = period.days
				Log.d(TAG, "onViewCreated, diff = { $years : $months : $days }")
				firstData = years
				secondData = months
				thirdData = days
				firstLabel = getString(R.string.year)
				secondLabel = getString(R.string.month)
				thirdLabel = getString(R.string.day)
				Log.d(TAG, "updateMode, ymd: $firstData, $secondData, $thirdData")
			}
			TimeMode.HMS -> {
				val now = TimeUtils.getCurrentTime()
				val lastDate = startDate.plusDays(noticePeriodDays)
//				val period = Period(now, lastDate, PeriodType.time()).normalizedStandard()
//				val period = Period(now, lastDate, PeriodType.dayTime(), ISOChronology.getInstanceUTC())
				val period = Period(now, lastDate, PeriodType.time())
//				Log.d(TAG, "updateMode, ${now.toDate().time}, ${LocalTime.now(DateTimeZone.UTC)}")
//				Log.d(TAG, "updateMode, ${now.toDate().time}, ${LocalTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()))}")
//				Log.d(TAG, "updateMode, ${now.toDate().time}, ${LocalTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()))}")
				firstData = period.hours
				secondData = period.minutes
				thirdData = period.seconds
				firstLabel = getString(R.string.hour)
				secondLabel = getString(R.string.minute)
				thirdLabel = getString(R.string.second)
				Log.d(TAG, "updateMode, hms: $firstData, $secondData, $thirdData")
				track = true
			}
		}
		
		changeText(first_label, firstLabel, 0)
		changeText(second_label, secondLabel, 0)
		changeText(third_label, thirdLabel, 0)
		
		first_text.prepare()
		second_text.prepare()
		third_text.prepare()
		first_text.number = firstData.clampNegative
		second_text.number = secondData.clampNegative
		third_text.number = thirdData.clampNegative
//		mStartTime = -1
//		mIncrement = 1
		try {
			first_text.reset()
			second_text.reset()
			third_text.reset()
//			animateArc()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		trackIfNeeded()
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val dateStorage = DataStorage(context!!)
		startDate = dateStorage.getResignDateTime()
		noticePeriodDays = dateStorage.getNoticePeriod()
		Log.v(TAG, "onViewCreated, notice: $noticePeriodDays")
		Log.v(TAG, "onViewCreated, resign: $startDate")
//		daysCompleted = Days.daysBetween(LocalDate.now(), startDate)
//		daysCompleted = Days.daysBetween(startDate, LocalDate.now()).days
		daysCompleted = Days.daysBetween(startDate, TimeUtils.getCurrentTime()).days
		
		/*val lastDate = startDate.plusDays(noticePeriodDays)
		val period = Period(now, lastDate, PeriodType.yearMonthDay())
		val years = period.years
		val months = period.months
		val days = period.days
		Log.d(TAG, "onViewCreated, diff = { $years : $months : $days }")
		first_text.number = years
		second_text.number = months
		third_text.number = days*/
		updateMode()
		try {
			animateArc()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		toggleMode.setOnClickListener {
			toggleMode()
		}
	}
}