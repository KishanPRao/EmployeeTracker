package com.kishan.employeetracker.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kishan.employeetracker.R
import com.kishan.employeetracker.data.DataStorage
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.fragment_base_launch.*
import kotlinx.android.synthetic.main.fragment_notice_launch.*
import org.joda.time.LocalDate

/**
 * Created by Kishan P Rao on 05/04/18.
 */
class LaunchActivity : AppCompatActivity() {
	companion object {
		private val TAG = LaunchActivity::class.java.simpleName
		const val SLIDE_COUNT = 3
		
		const val START_SLIDE = 0
		const val RESIGN_SLIDE = 1
		const val NOTICE_SLIDE = 2
	}
	
	private lateinit var dataStorage: DataStorage
	
	private fun stop() {
		finish()
		val intent = Intent(applicationContext, MainActivity::class.java)
		startActivity(intent)
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_launch)
		
		dataStorage = DataStorage(applicationContext)
		
		if (dataStorage.isInitialized()) {
			stop()
			return
		}
		
//		TODO: Until Skip, or last pager has been reached, not init!!
		
		val viewPager = viewPager
		val adapter = LaunchAdapter(supportFragmentManager, SLIDE_COUNT, {
			Log.d(TAG, "onCreate, date => $it")
//			if (it == null || viewPager.currentItem == (SLIDE_COUNT - 1)) {
			if (it == null) {
				Log.d(TAG, "onCreate, done")
				stop()
			} else {
				if (viewPager.currentItem == START_SLIDE) {
					dataStorage.putStartDate(it)
				} else {
					dataStorage.putResignDate(it)
				}
				viewPager.currentItem++
			}
		}, {
			if (viewPager.currentItem == (SLIDE_COUNT - 1)) {
				dataStorage.putNoticePeriod(it)
				Log.d(TAG, "onCreate, done")
				stop()
			}
		})
		viewPager.adapter = adapter
	}
	
	class LaunchAdapter(
			fm: FragmentManager,
			private var numOfItems: Int,
			private var dateCallback: (LocalDate?) -> Unit,
			private var noticePeriodCallback: (Int) -> Unit
	) : FragmentStatePagerAdapter(fm) {
		
		override fun getCount() = numOfItems
		
		private var resignFragment = ResignationLaunchFragment()
		private var startFragment = StartDateLaunchFragment()
		private var noticePeriodFragment = NoticePeriodLaunchFragment()
		
		override fun getItem(position: Int): Fragment? {
			return when (position) {
				START_SLIDE -> {
					startFragment.callback = dateCallback
					startFragment
				}
				RESIGN_SLIDE -> {
					resignFragment.callback = dateCallback
					resignFragment
				}
				NOTICE_SLIDE -> {
					noticePeriodFragment.callback = noticePeriodCallback
					noticePeriodFragment
				}
				else -> {
					Log.w(TAG, "Bad fragment")
					null
				}
			}
		}
	}
	
	class NoticePeriodLaunchFragment : Fragment() {
		lateinit var callback: (Int) -> Unit
		
		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
			return inflater.inflate(R.layout.fragment_notice_launch, null, false)
		}
		
		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//			FIXME: Enter through keyboard, press back button!
			numberPicker.minValue = 0
			numberPicker.maxValue = 180
			launch_notice_next.setOnClickListener {
				callback.invoke(numberPicker.value)
			}
		}
	}
	
	class StartDateLaunchFragment : BaseLaunchFragment() {
		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
//			datePicker.maxDate = System.currentTimeMillis()
			launch_desc.text = resources.getString(R.string.launch_worked_desc)
			launch_skip.visibility = View.GONE
		}
	}
	
	class ResignationLaunchFragment : BaseLaunchFragment() {
		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			super.onViewCreated(view, savedInstanceState)
//			datePicker.minDate = System.currentTimeMillis()
			launch_desc.text = resources.getString(R.string.launch_resign_desc)
		}
	}
	
	open class BaseLaunchFragment : Fragment() {
		lateinit var callback: (LocalDate?) -> Unit
		var date: LocalDate? = null
		
		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
			return inflater.inflate(R.layout.fragment_base_launch, null, false)
		}
		
		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
			launch_next.setOnClickListener {
				val day = datePicker.dayOfMonth
				val month = datePicker.month + 1
				val year = datePicker.year
				date = LocalDate(year, month, day)
				callback.invoke(date)
			}
			launch_skip.setOnClickListener {
				callback.invoke(null)
			}
		}
	}
}