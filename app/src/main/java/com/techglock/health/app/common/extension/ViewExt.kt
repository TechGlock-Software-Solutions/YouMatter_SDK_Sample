package com.techglock.health.app.common.extension

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Event
import com.google.android.material.snackbar.Snackbar

private val appColorHelper = AppColorHelper.instance!!

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun Fragment.showSnackbar(snackbarText: String, timeLength: Int) {
    activity?.let {
        Snackbar.make(
            it.findViewById<View>(android.R.id.content),
            snackbarText,
            timeLength
        ).show()
    }
}

fun Fragment.showToast(toastText: String, timeLength: Int) {
    activity?.let {
        val toast = Toast.makeText(context, toastText, timeLength)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            val view = toast.view
            view?.background?.colorFilter =
                PorterDuffColorFilter(appColorHelper.primaryColor(), PorterDuff.Mode.SRC_IN)
            val text = view?.findViewById<TextView>(android.R.id.message)
            text?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        toast.show()
    }
}

fun Activity.openAnotherActivity(
    destination: String,
    clearTop: Boolean = false,
    animate: Boolean = true,
    extras: Bundle.() -> Unit = {}
) {
    val intent = Intent()
    intent.component = ComponentName(NavigationConstants.APPID, destination)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (clearTop) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
    if (animate) {
        if (clearTop) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}

fun Fragment.openAnotherActivity(
    destination: String,
    clearTop: Boolean = false,
    animate: Boolean = true,
    extras: Bundle.() -> Unit = {}
) {
    val intent = Intent()
    intent.component = ComponentName(NavigationConstants.APPID, destination)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (clearTop) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
    if (animate) {
        if (clearTop) {
            requireActivity().overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        } else {
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun Fragment.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner) { event ->
        event.getContentIfNotHandled()?.let { res ->
            context?.let { showSnackbar(it.getString(res), timeLength) }
        }
    }
}

fun Fragment.setupSnackbarMessenger(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<String>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner) { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showSnackbar(data, timeLength) }
        }
    }
}

fun Fragment.setupToast(
    lifecycleOwner: LifecycleOwner,
    toastEvent: LiveData<Event<String>>,
    timeLength: Int
) {
    toastEvent.observe(lifecycleOwner) { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showToast(data, timeLength) }
        }
    }
}

fun Fragment.setupToastError(
    lifecycleOwner: LifecycleOwner,
    toastEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    toastEvent.observe(lifecycleOwner) { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showToast(it.getString(data), timeLength) }
        }
    }
}

fun View.visibilityView(isShow: Boolean) {
    if (isShow) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.visibilityInvisibleView(isShow: Boolean) {
    if (isShow) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}