package com.smartdigital.medicine.uihelper

import androidx.recyclerview.widget.RecyclerView
import com.smartdigital.medicine.R
import com.smartdigital.medicine.UserDataManager
import nz.co.trademe.covert.Covert

class CovertManager (recyclerView: RecyclerView, userDataManager: UserDataManager)
{
    private val covertConfig : Covert.Config = Covert.Config(
            iconRes = R.drawable.common_google_signin_btn_icon_dark, // The icon to show
            iconDefaultColorRes = R.color.white,            // The color of the icon
            actionColorRes = R.color.colorAccent          // The color of the background
    )

    val covert = Covert.with(covertConfig)
            .setIsActiveCallback {
                // This is a callback to check if the item is active, i.e checked
                userDataManager.contains(it.adapterPosition)
            }
            .doOnSwipe { viewHolder, _ ->
                // This callback is fired when a ViewHolder is swiped
                userDataManager.remove(viewHolder.adapterPosition)
            }
            .attachTo(recyclerView)
}