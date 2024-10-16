package com.techglock.health.app.common.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.navigation.NavigationCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(mApplication: Application) :
    AndroidViewModel(mApplication) {

    // FOR ERROR HANDLER
    protected val _snackbarError = MutableLiveData<Event<Int>>()
    val snackBarError: LiveData<Event<Int>> get() = _snackbarError

    // FOR SESSION EXPIRED
    protected val _sessionError = MutableLiveData<Event<Boolean>>()
    val sessionError: LiveData<Event<Boolean>> get() = _sessionError

    // FOR Progress Dialog
    protected val _progressBar = MutableLiveData<Event<String>>()
    val progressBar: LiveData<Event<String>> get() = _progressBar

    // FOR ERROR HANDLER
    protected val _snackbarMessage = MutableLiveData<Event<String>>()
    val snackMessenger: LiveData<Event<String>> get() = _snackbarMessage

    // FOR ERROR HANDLER
    protected val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    // FOR ERROR HANDLER
    protected val _toastError = MutableLiveData<Event<Int>>()
    val toastError: LiveData<Event<Int>> get() = _toastError

    // FOR NAVIGATION
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation

    /**
     * Convenient method to handle navigation from a [ViewModel]
     */
    fun navigate(directions: NavDirections) {
        _navigation.value = Event(NavigationCommand.To(directions))
    }

    fun toastMessage(message: String = "") {
        _toastMessage.value = Event(message)
    }

    fun toastMessage(message: Int) {
        _toastError.value = Event(message)
    }

    fun snackMessage(message: String = "") {
        _snackbarMessage.value = Event(message)
    }

    fun snackMessage(message: Int) {
        _snackbarError.value = Event(message)
    }

    fun showProgressBar(message: String = "") {
        _progressBar.value = Event(message)
    }

    fun hideProgressBar() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

}