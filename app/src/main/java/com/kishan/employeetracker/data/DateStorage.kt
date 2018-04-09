package com.kishan.employeetracker.data

import android.content.Context
import android.content.SharedPreferences
import org.joda.time.LocalDate

/**
 * Created by Kishan P Rao on 09/04/18.
 */
class DateStorage(context: Context) {
	companion object {
		const val SHARED_PREFS_NAME = "EmployeeInfo"
		const val NOTICE = "NOTICE"
		const val START_Y = "START_y"
		const val START_M = "START_m"
		const val START_D = "START_d"
		
		const val RESIGN_Y = "RESIGN_y"
		const val RESIGN_M = "RESIGN_m"
		const val RESIGN_D = "RESIGN_d"
		
		const val HAS_RESIGNED = "HAS_RESIGNED"
		const val INITIALIZED = "INITIALIZED"
	}
	
	private val sharedPreferences: SharedPreferences
	
	init {
		sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
	}
	
	fun hasResigned() = sharedPreferences.getBoolean(HAS_RESIGNED, false)
	
	fun isInitialized() = sharedPreferences.getBoolean(INITIALIZED, false)
	
	fun putNoticePeriod(days: Int) {
		sharedPreferences.edit().putInt(NOTICE, days).apply()
	}
	
	fun putStartDate(date: LocalDate) {
		sharedPreferences.edit().putBoolean(INITIALIZED, true)
				.putInt(START_Y, date.year)
				.putInt(START_M, date.monthOfYear)
				.putInt(START_D, date.dayOfMonth).apply()
	}
	
	fun putResignDate(date: LocalDate) {
		sharedPreferences.edit().putBoolean(HAS_RESIGNED, true)
				.putInt(RESIGN_Y, date.year)
				.putInt(RESIGN_M, date.monthOfYear)
				.putInt(RESIGN_D, date.dayOfMonth).apply()
	}
	
	fun getNoticePeriod() = sharedPreferences.getInt(NOTICE, 0)
	
	fun getStartDate(): LocalDate {
		val y = sharedPreferences.getInt(START_Y, 0)
		val m = sharedPreferences.getInt(START_M, 0)
		val d = sharedPreferences.getInt(START_D, 0)
		return LocalDate(y, m, d)
	}
	
	fun getResignDate(): LocalDate {
		val y = sharedPreferences.getInt(RESIGN_Y, 0)
		val m = sharedPreferences.getInt(RESIGN_M, 0)
		val d = sharedPreferences.getInt(RESIGN_D, 0)
		return LocalDate(y, m, d)
	}
}