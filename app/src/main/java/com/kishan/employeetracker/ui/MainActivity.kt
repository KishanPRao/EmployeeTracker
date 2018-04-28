package com.kishan.employeetracker.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kishan.employeetracker.R
import com.kishan.employeetracker.data.DataStorage
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Kishan P Rao on 03/04/18.
 */
class MainActivity : AppCompatActivity() {
	
	companion object {
		private val TAG = MainActivity::class.java.simpleName
		
		private const val WORKING_COUNT = 1
		private const val RESIGN_COUNT = 2
	}
	
	private var openingSettings = false
	
	private fun initView() {
		val resigned = DataStorage(applicationContext).hasResigned()
		val tabLayout = tabLayout
		val arrayId = if (resigned) R.array.tabs_resign_array else R.array.tabs_array
		for (tabName in resources.getStringArray(arrayId)) {
			val tab = tabLayout.newTab().setText(tabName)
			tabLayout.addTab(tab)
		}
		val viewPager = viewPager
		val count = if (resigned)
			RESIGN_COUNT
		else
			WORKING_COUNT
		Log.d(TAG, "initView, $count")
		val adapter = EmployeeTrackerAdapter(supportFragmentManager, count)
		viewPager.adapter = adapter
		viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
		tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab?) {
				tab?.let {
					viewPager.currentItem = tab.position
					adapter.resetItem(tab.position)
				}
			}
			
			override fun onTabReselected(tab: TabLayout.Tab?) {}
			
			override fun onTabUnselected(tab: TabLayout.Tab?) {}
		})
	}
	
	private fun openSettings() {
		if (!openingSettings) {
			openingSettings = true
			val intent = Intent(applicationContext, SettingsActivity::class.java)
			startActivity(intent)
		}
	}
	
	override fun onResume() {
		super.onResume()
		openingSettings = false
	}
	
	override fun onPause() {
		super.onPause()
	}
	
	private fun init() {
		val dataStorage = DataStorage(applicationContext)
		if (!AppLauncherManager.DISABLE && !dataStorage.isIconInitialized()) {
			val resigned = dataStorage.hasResigned()
			if (resigned) {
				AppLauncherManager.disable(this, ".MainActivityJoin")
				AppLauncherManager.enable(this, ".MainActivityResign")
			} else {
				AppLauncherManager.disable(this, ".MainActivityResign")
				AppLauncherManager.enable(this, ".MainActivityJoin")
			}
			AppLauncherManager.disable(this, ".ui.LaunchActivity")
			dataStorage.updateIconInitialization()
		}
		initView()
		btnSettings.setOnClickListener {
			openSettings()
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		val isInitialized = DataStorage(applicationContext).isInitialized()
		Log.d(TAG, "onCreate, $isInitialized")
		super.onCreate(savedInstanceState)
		if (isInitialized) {
			setContentView(R.layout.activity_main)
			init()
		} else {
			AppLauncherManager.disable(this, ".MainActivityResign")
			AppLauncherManager.disable(this, ".MainActivityJoin")
			AppLauncherManager.enable(this, ".ui.LaunchActivity")
			startActivity(Intent(this, LaunchActivity::class.java))
			finish()
		}
	}
	
	class EmployeeTrackerAdapter(
			fm: FragmentManager,
			private var numOfItems: Int
	) : FragmentStatePagerAdapter(fm) {
		
		override fun getCount() = numOfItems
		
		private var workedForFragment = WorkedForFragment()
		private val noticePeriodFragment: NoticePeriodFragment by lazy {
			NoticePeriodFragment()
		}
		
		fun resetItem(position: Int) {
//			TODO: Is Active Logic.
			when (position) {
				0 -> {
					if (numOfItems == RESIGN_COUNT) {
						noticePeriodFragment.reset()
					} else {
						workedForFragment.reset()
					}
				}
				1 -> {
					workedForFragment.reset()
				}
			}
		}
		
		override fun getItem(position: Int): Fragment? {
			return when (position) {
				0 -> {
					if (numOfItems == RESIGN_COUNT) {
						noticePeriodFragment
					} else {
						workedForFragment
					}
				}
				1 -> {
					workedForFragment
				}
				else -> {
					Log.w(TAG, "Bad fragment")
					null
				}
			}
		}
	}
}
