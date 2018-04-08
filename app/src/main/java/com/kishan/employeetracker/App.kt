package com.kishan.employeetracker

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

/**
 * Created by Kishan P Rao on 05/04/18.
 */
class App: Application() {
	
	override fun onCreate() {
		super.onCreate()
		JodaTimeAndroid.init(this);
	}
}