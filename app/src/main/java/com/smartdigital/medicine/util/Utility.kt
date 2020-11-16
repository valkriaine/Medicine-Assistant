package com.smartdigital.medicine.util

import android.animation.ObjectAnimator
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.smartdigital.medicine.MainActivity.currentMed
import java.lang.Exception

//attach this on the HomePager to animate search bar
class OnPageChangeListener(private var objectToAnimate: View,
                           private var suggestionList: CustomSuggestionsAdapter,
                           private var title: TextView) : ViewPager.OnPageChangeListener
{
    //do nothing here
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int){}
    override fun onPageScrollStateChanged(state: Int) {}

    //animate search bar
    override fun onPageSelected(position: Int)
    {
        if (position == 0)
        {
            objectToAnimate.translationY = -400f
            ObjectAnimator.ofFloat(objectToAnimate, "translationY", 0f).apply {
                duration = 200
                start()
            }
        }
        else
        {
            suggestionList.clear()
            try {
                title.text = currentMed.info
            }
            catch (e: Exception)
            {
                title.text = "No drug info"
            }

            objectToAnimate.translationY = 0f
            ObjectAnimator.ofFloat(objectToAnimate, "translationY", -400f).apply{
                duration = 200
                start()
            }
        }
    }
}