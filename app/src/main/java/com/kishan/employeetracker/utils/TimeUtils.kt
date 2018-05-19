package com.kishan.employeetracker.utils

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

/**
 * Created by Kishan P Rao on 19/05/18.
 */
class TimeUtils {
	
	companion object {
		private const val Mock = false
		private const val y = 2018
		private const val m = 5
		private const val d = 21
		private const val hour = 0
		private const val min = 20
		private const val second = 30
		
		fun getCurrentTime(): LocalDateTime {
			val dateTime = LocalDateTime.now()
			if (Mock)
				return LocalDateTime(y, m, d, hour, min, second)
			return dateTime
		}
		
		fun getCurrentDate(): LocalDate {
			val dateTime = LocalDate.now()
			if (Mock)
				return LocalDate(y, m, d)
			return dateTime
		}
		
		fun getLocalTime(y: Int, m: Int, d: Int): LocalDateTime {
			return LocalDateTime(y, m, d, 0, 0)
//			val dateTime = LocalDateTime.now()
//			return LocalDateTime(y, m, d, dateTime.hourOfDay, dateTime.minuteOfHour)
		}
	}
}