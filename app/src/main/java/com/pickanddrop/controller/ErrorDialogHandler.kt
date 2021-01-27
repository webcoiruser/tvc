package com.pickanddrop.controller

import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import com.pickanddrop.R
import com.pickanddrop.dialog.ErrorDialogFragment


import java.lang.ref.WeakReference

/**
 * A convenience class to handle displaying error dialogs.
 */
class ErrorDialogHandler(activity: androidx.fragment.app.FragmentActivity) {

    private val activityRef: WeakReference<androidx.fragment.app.FragmentActivity> = WeakReference(activity)

    fun show(errorMessage: String) {
        val activity = activityRef.get() ?: return

        ErrorDialogFragment.newInstance(activity.getString(R.string.validationErrors), errorMessage)
            .show(activity.supportFragmentManager, "error")
    }
}
