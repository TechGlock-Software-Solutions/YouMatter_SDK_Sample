package com.techglock.health.app.fitness_tracker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogEdittextGoalBinding
import com.techglock.health.app.databinding.FragmentTodayBinding
import com.techglock.health.app.fitness_tracker.common.StepsDataSingleton
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.fitness_tracker.viewmodel.ActivityTrackerViewModel
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.fitness.SetGoalModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TodayFragment() : BaseFragment(),
    FitnessDataActivity.OnLatestGoalListener, FitnessDataActivity.OnGoogleAccountNameListener {

    private val viewModel: ActivityTrackerViewModel by lazy {
        ViewModelProvider(this)[ActivityTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentTodayBinding

    @Inject
    lateinit var fitnessHelper: FitnessHelper
    private val stepsDataSingleton = StepsDataSingleton.instance!!
    private var fitnessDataActivity: FitnessDataActivity? = null

    private var todayStepCount = 0
    private var todayStepGoal = Constants.DEFAULT_STEP_GOAL

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as FitnessDataActivity).setLatestGoalListener(this)
        (activity as FitnessDataActivity).setGoogleAccountNameListener(this)
    }

    override fun onGoogleAccountNameReceived(name: String) {
        binding.txtSyncAccount.text = name
        binding.layoutSyncAccount.visibility = View.VISIBLE
    }

    override fun onLatestGoalReceived(latestGoal: GetStepsGoalModel.LatestGoal) {
        Utilities.printData("LatestGoalData", latestGoal, true)
        latestGoalResp(latestGoal)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        if (userVisibleHint) {
            try {
                initialise()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            try {
                initialise()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        fitnessDataActivity = (activity as FitnessDataActivity)
        if (!Utilities.isNullOrEmpty(fitnessDataActivity!!.syncFrom)) {
            binding.txtSyncAccount.text = fitnessDataActivity!!.syncFrom
            binding.layoutSyncAccount.visibility = View.VISIBLE
        } else {
            binding.layoutSyncAccount.visibility = View.INVISIBLE
        }
        startAnimation()
        binding.progressBar.isClickable = false
        binding.progressBar.setOnTouchListener { _, _ -> true }
        //binding.txtTodaysDate.text = DateHelper.getDateTimeAs_EEE_MMM_yyyy_new(DateHelper.currentDateAsStringddMMMyyyy).toUpperCase(Locale.ROOT)
        updateTodayData()
    }

    /*    private fun startAnimation() {
            try {
                binding.imgDottedCircle.startAnimation(fitnessDataActivity!!.animRotate)
                binding.imgStep.startAnimation(fitnessDataActivity!!.animBounce)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    private fun startAnimation() {
        try {
            binding.imgDottedCircle.startAnimation(
                AnimationUtils.loadAnimation(
                    fitnessDataActivity,
                    R.anim.rotate_forward_finite
                )
            )
            binding.imgStep.startAnimation(
                AnimationUtils.loadAnimation(
                    fitnessDataActivity,
                    R.anim.anim_bounce
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAnimation() {
        try {
            fitnessDataActivity!!.showAnim = true
            Handler(Looper.getMainLooper()).postDelayed({
                binding.imgDottedCircle.clearAnimation()
                binding.imgStep.clearAnimation()
            }, 1500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerObservers() {
        viewModel.saveStepsGoal.observe(viewLifecycleOwner, { })
    }

    private fun setClickable() {

        binding.txtGoal.setOnClickListener {
            showGoalDialog()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateTodayData() {
        try {
            //val item = fitnessActivity.todayFitnessData
            val item = fitnessDataActivity!!.todayFitnessData
            Utilities.printData("TodayFitnessData", item, true)
            todayStepCount = item.stepsCount
            todayStepGoal = if (item.totalGoal > 0) item.totalGoal else Constants.DEFAULT_STEP_GOAL

            binding.txtStepCount.text = Utilities.formatNumberWithComma(todayStepCount)
            binding.txtGoal.text = Utilities.formatNumberWithComma(todayStepGoal)

            val progress = (todayStepCount * 100) / todayStepGoal
            //binding.progressBar.setValueAnimated(progress.toFloat(),1000)
            binding.progressBar.setProgressWithAnimation(progress.toDouble(), 800)

            if (fitnessDataActivity!!.showAnim) {
                stopAnimation()
            }

            val todayDistance = fitnessHelper.convertMtrToKmsValueNew(item.distance.toString())
            val todayCalories =
                fitnessHelper.getCaloriesWithUnit(item.calories.toString()).split(" ")
            val todayActiveTime = DateHelper.getHourMinFromStrMinutes(item.activeTime)
            //val todayActiveTime = DateHelper.getHourMinFromStrMinutes(item.activeTime).split(" ")

            //Utilities.printLogError("todayDistance----->${todayDistance}")
            //Utilities.printLogError("todayCalories----->${todayCalories[0]}")
            //Utilities.printLogError("todayActiveTime--->${todayActiveTime[0]}")

            binding.txtTodayDistance.text =
                Utilities.formatNumberWithComma(todayDistance.toDouble()) + " " + resources.getString(
                    R.string.KM
                )
            binding.txtTodayCalories.text =
                Utilities.formatNumberWithComma(todayCalories[0].toDouble()) + " " + todayCalories[1]
            binding.txtTodayActiveTime.text = todayActiveTime
            //binding.txtTodayActiveTime.text = Utilities.formatNumberWithComma(todayActiveTime[0].toDouble()) + " " + todayActiveTime[1]
            //binding.txtGoalReached.text = "$progress %"
            //val stepsToGo = if (todayStepGoal - todayStepCount < 0) 0 else todayStepGoal - todayStepCount
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun latestGoalResp(latestGoal: GetStepsGoalModel.LatestGoal) {
        try {
            startAnimation()
            if (!Utilities.isNullOrEmptyOrZero(latestGoal.goal.toString())) {
                /*            if (currentDate.equals(latestGoal.date,ignoreCase = true)) {
                                todayStepGoal = latestGoal.goal
                                fitnessActivity.todayFitnessData.totalGoal = latestGoal.goal
                            }*/
                todayStepGoal = latestGoal.goal
                //fitnessActivity.todayFitnessData.totalGoal = latestGoal.goal
                fitnessDataActivity!!.todayFitnessData.totalGoal = latestGoal.goal
            }
            updateTodayData()
            stopAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    fun saveGoalResp(stepsGoals: SetGoalModel.StepsGoals) {
        startAnimation()
        //Utilities.printLogError("Save Goal--->${stepsGoals}")
        todayStepGoal = stepsGoals.goal
        val saveGoal = GetStepsGoalModel.LatestGoal(
            personID = stepsGoals.personID,
            date = stepsGoals.date.split("T").toTypedArray()[0],
            type = stepsGoals.type,
            goal = stepsGoals.goal,
            iD = stepsGoals.iD
        )
        stepsDataSingleton.latestGoal = saveGoal
        //updateStepsData
        val progress = (todayStepCount * 100) / todayStepGoal
        //binding.progressBar.setValueAnimated(progress.toFloat())
        binding.progressBar.setProgressWithAnimation(progress.toDouble(), 1000)
        stopAnimation()

        binding.txtGoal.text = Utilities.formatNumberWithComma(todayStepGoal)
        binding.txtGoalReached.text = "$progress %"
        stopAnimation()
        //val stepsToGo = if (todayStepGoal - todayStepCount < 0) 0 else todayStepGoal - todayStepCount
        //binding.txtStepsToGo.text = stepsToGo.toString()
    }

    private fun showGoalDialog() {
        try {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogEdittextGoalBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            //dialog.setContentView(R.layout.dialog_edittext_goal)
            //dialogBinding.txtDialogTitle.text = binding.txtGoal.text.toString().replace(",", "")
            dialogBinding.dialogEt.setText(binding.txtGoal.text.toString().replace(",", ""))
            dialog.show()

            dialogBinding.imgClose.setOnClickListener { dialog.dismiss() }

            dialogBinding.dialogBtncancel.setOnClickListener { dialog.dismiss() }

            dialogBinding.dialogBtnok.setOnClickListener {
                val strValue = dialogBinding.dialogEt.text.toString()
                if (!Utilities.isNullOrEmptyOrZero(strValue)) {
                    val value = Integer.parseInt(strValue)
                    if (value in 30..10000) {
                        viewModel.saveStepsGoal(this, value)
                        dialog.dismiss()
                    } else {
                        Utilities.toastMessageLong(
                            requireContext(),
                            resources.getString(R.string.ERROR_ENTER_VALUE_IN_30_10000)
                        )
                    }
                } else {
                    Utilities.toastMessageLong(
                        requireContext(),
                        resources.getString(R.string.ERROR_ENTER_STEPS)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    private fun proceedWithFitnessData() {
            try {
                viewModel.showProgressBar()
                val today = Calendar.getInstance().time
                fitnessDataManager!!.readHistoryData(today, today).addOnCompleteListener {
                    if ( fitnessDataManager!!.fitnessDataArray.length() > 0 ) {
                        val todayData = fitnessDataManager!!.fitnessDataArray.getJSONObject(0)
                        Utilities.printData("TodayFitnessData",todayData)
                        todayFitnessData.recordDate = todayData.getString(Constants.RECORD_DATE)
                        todayFitnessData.stepsCount = todayData.getString(Constants.STEPS_COUNT).toInt()
                        todayFitnessData.calories = todayData.getString(Constants.CALORIES).toInt()
                        todayFitnessData.distance = todayData.get(Constants.DISTANCE).toString().toDouble()
                        todayFitnessData.activeTime = todayData.getString(Constants.ACTIVE_TIME)
                        updateTodayData()
                        viewModel.hideProgressBar()
                    } else {
                        //Utilities.printLogError("Fitness Data not Available")
                        viewModel.hideProgressBar()
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }*/

}