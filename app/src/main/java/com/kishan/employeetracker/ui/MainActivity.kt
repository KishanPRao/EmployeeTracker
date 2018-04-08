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
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Kishan P Rao on 03/04/18.
 */
class MainActivity : AppCompatActivity() {
	
	companion object {
		private val TAG = MainActivity::class.java.simpleName
	}
	
	var openingSettings = false
	
	private fun initView() {
		val tabLayout = tabLayout
		for (tabName in resources.getStringArray(R.array.tabs_array)) {
			val tab = tabLayout.newTab().setText(tabName)
			tabLayout.addTab(tab)
		}
		val viewPager = viewPager
		val adapter = EmployeeTrackerAdapter(supportFragmentManager, 2)
		viewPager.adapter = adapter
		viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
		tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab?) {
				tab?.let {
					viewPager.currentItem = tab.position
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
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initView()
		btnSettings.setOnClickListener {
			openSettings()
		}
	}
	
	class EmployeeTrackerAdapter(
			fm: FragmentManager,
			private var numOfItems: Int
	) : FragmentStatePagerAdapter(fm) {
		
		override fun getCount() = numOfItems
		
		override fun getItem(position: Int): Fragment? {
			return when (position) {
				0 -> NoticePeriodFragment()
				1 -> WorkedForFragment()
				else -> {
					Log.w(TAG, "Bad fragment")
					null
				}
			}
		}
	}
}
