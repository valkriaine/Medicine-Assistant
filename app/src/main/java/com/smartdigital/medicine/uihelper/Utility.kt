package com.smartdigital.medicine.uihelper

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.smartdigital.medicine.MainActivity.currentMed
import com.smartdigital.medicine.R
import com.smartdigital.medicine.util.CustomSuggestionsAdapter
import java.lang.Exception

//attach this on the HomePager to animate search bar
class OnPageChangeListener(private var objectToAnimate: View,
                           private var suggestionList: CustomSuggestionsAdapter,
                           private var title: TextView,
                           private var homePager: com.valkriaine.factor.HomePager,
                           private var context: Context) : ViewPager.OnPageChangeListener
{
    //do nothing here
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int){}
    override fun onPageScrollStateChanged(state: Int) {}

    //animate search bar
    override fun onPageSelected(position: Int)
    {
        if (position == 0)
        {
            if (currentMed == null)
                homePager.setSwipeAllowed(false)
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
                title.text = context.getString(R.string.drug_text_placeholder)
            }
            homePager.setSwipeAllowed(true)
            objectToAnimate.translationY = 0f
            ObjectAnimator.ofFloat(objectToAnimate, "translationY", -400f).apply{
                duration = 200
                start()
            }
        }
    }
}