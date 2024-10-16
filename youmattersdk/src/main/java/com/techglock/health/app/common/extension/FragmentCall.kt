package com.techglock.health.app.common.extension

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.techglock.health.app.R


fun AppCompatActivity.replaceFragment(
    fragment: Fragment, args: Bundle? = null, frameId: Int = R.id.main_container
) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
        replace(frameId, fragment).addToBackStack(fragment.javaClass.name)
    }
}

fun AppCompatActivity.replaceFragmentWithoutStack(
    fragment: Fragment, args: Bundle? = null, frameId: Int = R.id.main_container
) {

    fragment.arguments = args
    supportFragmentManager.inTransaction {
//        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragmentWithoutStackWithAnim(
    fragment: Fragment, args: Bundle? = null, frameId: Int = R.id.main_container
) {

    fragment.arguments = args
    supportFragmentManager.inTransaction {
//        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragWithArgs(
    fragment: Fragment, frameId: Int = R.id.main_container, args: Bundle
) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
//        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        replace(frameId, fragment).addToBackStack(fragment.javaClass.name)
    }
}

fun AppCompatActivity.replaceFragWithArgsWithoutStack(
    fragment: Fragment, frameId: Int = R.id.main_container, args: Bundle
) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
//        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        replace(frameId, fragment)
    }
}

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(
    fragment: Fragment, frameId: Int = R.id.main_container, backStackTag: String? = null
) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        backStackTag?.let {
            addToBackStack(fragment.javaClass.name)
        }!!
    }
}