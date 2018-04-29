package com.kishan.employeetracker

import android.annotation.SuppressLint
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import com.kishan.employeetracker.ui.LaunchActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test


/**
 * Created by Kishan P Rao on 28/04/18.
 */
class BasicTest : ActivityTestRule<LaunchActivity>(LaunchActivity::class.java) {
	companion object {
		private val TAG = BasicTest::class.java.simpleName
	}
	
	private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
		return object : TypeSafeMatcher<View>() {
			internal var currentIndex: Int = 0
			internal var viewObjHash: Int = 0
			
			@SuppressLint("DefaultLocale")
			override fun describeTo(description: Description) {
				description.appendText(String.format("with index: %d ", index))
				matcher.describeTo(description)
			}
			
			override fun matchesSafely(view: View): Boolean {
				if (matcher.matches(view) && currentIndex++ == index) {
					viewObjHash = view.hashCode()
				}
				return view.hashCode() === viewObjHash
			}
		}
	}
	
	
	@Before
	internal fun setUp() {
		Log.d(TAG, "setUp, ")
		launchActivity(null)
	}
	
	private fun setNumber(num: Int): ViewAction {
		return object : ViewAction {
			override fun perform(uiController: UiController, view: View) {
				val np = view as NumberPicker
				np.value = num
				
			}
			
			override fun getDescription(): String {
				return "Set the passed number into the NumberPicker"
			}
			
			override fun getConstraints(): Matcher<View> {
				return ViewMatchers.isAssignableFrom(NumberPicker::class.java)
			}
		}
	}
	
//	private fun launchActivity() {
//	}
	
	private val index = 0
	
	private val samplesStart = arrayOf(
			LocalDate(2016, 5, 16),
			LocalDate(2018, 4, 24)
	)
	private val samplesResign = arrayOf(
			LocalDate(2018, 2, 20),
			LocalDate(2018, 4, 26)
	)
	
	private val samplesNotice = arrayOf(
			90,
			2
	)
	
	private fun testWait() {
		Thread.sleep(2 * 1000)
	}
	
	private fun completeWithoutNext() {
		var year = samplesStart[index].year
		var month = samplesStart[index].monthOfYear
		var day = samplesStart[index].dayOfMonth
		
		var datePicker = withIndex(withId(R.id.datePicker), 0)
		onView(datePicker).perform(PickerActions.setDate(year, month, day))
		onView(withIndex(withId(R.id.launch_next), 0)).perform(click())
//		testWait()
		
		year = samplesResign[index].year
		month = samplesResign[index].monthOfYear
		day = samplesResign[index].dayOfMonth
		datePicker = withIndex(withId(R.id.datePicker), 1)
		onView(datePicker).perform(PickerActions.setDate(year, month, day))
		onView(withIndex(withId(R.id.launch_next), 1)).perform(click())
//		testWait()
		
		val notice = samplesNotice[index]
		onView(withId(R.id.numberPicker)).perform(setNumber(notice))
		testWait()
	}
	
//	@Test
	internal fun testWithoutResign() {
		var year = samplesStart[index].year
		var month = samplesStart[index].monthOfYear
		var day = samplesStart[index].dayOfMonth
		
		var datePicker = withIndex(withId(R.id.datePicker), 0)
		onView(datePicker).perform(PickerActions.setDate(year, month, day))
		onView(withIndex(withId(R.id.launch_next), 0)).perform(click())
		testWait()
		
		onView(withIndex(withId(R.id.launch_skip), 1)).perform(click())
	}
	
	@Test
	internal fun testComplete() {
		Log.d(TAG, "complete, ")
		completeWithoutNext()
		onView(withId(R.id.launch_notice_next)).perform(click())
	}
	
	@After
	fun tearDown() {
		Log.d(TAG, "tearDown, ")
	}
}