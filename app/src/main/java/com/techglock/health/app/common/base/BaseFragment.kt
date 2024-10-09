package com.techglock.health.app.common.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.extension.setupSnackbar
import com.techglock.health.app.common.extension.setupSnackbarMessenger
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.navigation.NavigationCommand
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    private val progressDialog by lazy {
//        ProgressDialog(context)
        CustomProgressBar(requireContext())
    }

    private val sessionExpiryDialog by lazy {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.SESSION)
        dialogData.message = resources.getString(R.string.MSG_SESSION_EXPIRED)
        dialogData.showLeftButton = false
        dialogData.showDismiss = false
        DefaultNotificationDialog(
            requireContext(),
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        if (Utilities.logout(requireContext(), requireActivity())) {
                            openAnotherActivity(
                                destination = NavigationConstants.LOGIN,
                                clearTop = true
                            )
                        }
                    }
                }
            }, dialogData
        )
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeNavigation(getViewModel())
        observeSession(getViewModel())
        setupSnackbar(this, getViewModel().snackBarError, Snackbar.LENGTH_LONG)
        setupSnackbarMessenger(this, getViewModel().snackMessenger, Snackbar.LENGTH_LONG)
        setUpToast(this, getViewModel().toastMessage)
        setupToastMessageError(this, getViewModel().toastError, Toast.LENGTH_LONG)
        setUpProgressBar(this, getViewModel().progressBar)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    private fun setUpToast(lifecycleOwner: LifecycleOwner, toastEvent: LiveData<Event<String>>) {
        toastEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                showToast(data)
            }
        }
    }

    private fun setupToastMessageError(
        lifecycleOwner: LifecycleOwner,
        toastEvent: LiveData<Event<Int>>,
        timeLength: Int
    ) {
        toastEvent.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { res ->
                showToast(this.getString(res))
            }
        }
    }

    private fun showToast(data: String) {
        Utilities.toastMessageShort(requireContext(), data)
    }

    private fun observeSession(viewModel: BaseViewModel) {
        viewModel.sessionError.observe(viewLifecycleOwner) {
            sessionExpiryDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            sessionExpiryDialog.show()
        }
    }

    private fun setUpProgressBar(
        lifecycleOwner: LifecycleOwner,
        progressBar: LiveData<Event<String>>
    ) {
        progressBar.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.equals(Event.HIDE_PROGRESS, true))
                    showProgress(false, it)
                else
                    showProgress(true, it)
            }
        }
    }

    abstract fun getViewModel(): BaseViewModel

    // UTILS METHODS ---

    /**
     * Observe a [NavigationCommand] [Event] [LiveData].
     * When this [LiveData] is updated, [Fragment] will navigate to its destination
     */
    private fun observeNavigation(viewModel: BaseViewModel) {
        viewModel.navigation.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is NavigationCommand.To -> findNavController().navigate(
                        command.directions,
                        getExtras()
                    )

                    is NavigationCommand.Back -> findNavController().navigateUp()
                }
            }
        }
    }

    private fun showProgress(showProgress: Boolean, message: String) {
        try {
            if (showProgress) {
//                progressDialog.setMessage(message)
//                progressDialog.isIndeterminate = false
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.setLoaderType(message)
                progressDialog.show()
            } else {
                if (progressDialog.isShowing) {
                    //progressDialog.cancel()
                    //progressDialog.dismiss()
                    if (message.equals(Constants.LOADER_DELETE, ignoreCase = true)) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.cancel()
                            progressDialog.dismiss()
                        }, (Constants.DELETE_LOADER_ANIM_DELAY_IN_MS).toLong())
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            progressDialog.cancel()
                            progressDialog.dismiss()
                        }, (Constants.LOADER_ANIM_DELAY_IN_MS).toLong())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * [FragmentNavigatorExtras] mainly used to enable Shared Element transition
     */
    open fun getExtras(): FragmentNavigator.Extras = FragmentNavigatorExtras()

}