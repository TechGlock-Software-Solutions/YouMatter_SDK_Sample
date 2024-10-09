package com.techglock.health.app.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.RevUpdateParameterFragmentBinding
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.track_parameter.adapter.ProfileSelectionAdapter
import com.techglock.health.app.track_parameter.adapter.RevInputParamAdapter
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.techglock.health.app.track_parameter.viewmodel.UpdateParamViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class UpdateParameterFragment : BaseFragment(), ProfileSelectionAdapter.ProfileSelectionListener {

    private val viewModel: UpdateParamViewModel by lazy {
        ViewModelProvider(this)[UpdateParamViewModel::class.java]
    }
    private lateinit var binding: RevUpdateParameterFragmentBinding

    private var paramAdapter: RevInputParamAdapter? = null
    private var profileSelectionAdapter: ProfileSelectionAdapter? = null

    private val args: UpdateParameterFragmentArgs by navArgs()
    private var serverDate = ""
    private var profileCode = "BMI"
    private var isValidateSuccess = false
    private var validationMessage = ""
    private var isFirstTime = true
    //private val c = Calendar.getInstance()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RevUpdateParameterFragmentBinding.inflate(inflater, container, false)

        try {
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.TRACK_PARAMETER_UPDATE_SCREEN
        )
        profileCode = args.profileCode
        Utilities.printLog("ProfileCode---> $profileCode")

        profileSelectionAdapter = ProfileSelectionAdapter(requireContext(), this, false)
        binding.rvProfileSelection.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvProfileSelection.adapter = profileSelectionAdapter

        paramAdapter = RevInputParamAdapter(profileCode, requireContext(), viewModel)
        binding.rvInputParameters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvInputParameters.adapter = paramAdapter

        serverDate = DateHelper.currentDateAsStringddMMMyyyy
        binding.edtDate.setText(DateHelper.currentDateAsStringddMMMyyyyNew)

        viewModel.getParameterByProfileCodeAndDate(profileCode, serverDate)
        viewModel.refreshSelectedParamList(args.showAllProfile)

    }

    override fun onProfileSelection(position: Int, profile: ParameterListModel.SelectedParameter) {
        updateData(position, profile.profileCode)
    }

    private fun setClickable() {

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.edtDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveUpdateParameters.setOnClickListener {
            try {
                var IS_BMI = false
                var IS_WHR: Boolean = false
                var IS_BP: Boolean = false
                val updateDateTime: String = DateHelper.currentDateTime
                var recordDateSync: String? = ""

                isValidateSuccess = paramAdapter!!.validateFields(paramAdapter!!.dataList)
                validationMessage = paramAdapter!!.validationMassage
                if (isValidateSuccess) {
                    var parameters: ArrayList<ParameterListModel.InputParameterModel>? = null
                    parameters = paramAdapter!!.getUpdatedParamList()
                    val recordDate = serverDate
                    var strUnit: String? = null
                    if (!profileCode.isNullOrEmpty()
                        && profileCode.equals("LIPID", ignoreCase = true) && strUnit.isNullOrEmpty()
                    ) {
                        strUnit = "mg/dL"
                    }
                    if (profileCode.equals("WHR", ignoreCase = true)) {
                        IS_WHR = true
                        recordDateSync = recordDate
                    } else if (profileCode.equals("BMI", ignoreCase = true)) {
                        IS_BMI = true
                        recordDateSync = recordDate
                    } else if (profileCode.equals("BP", ignoreCase = true)
                        || profileCode.equals("BLOODPRESSURE", ignoreCase = true)
                    ) {
                        IS_BP = true
                        recordDateSync = recordDate
                    }
                    val isParameterEmpty: Boolean = checkParameterEmpty(parameters)
                    if (!isParameterEmpty) {
                        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.TRACK_PARAMETER_UPDATE)
                        viewModel.saveParameter(paramAdapter!!.dataList as ArrayList<ParameterListModel.InputParameterModel>, recordDate)
                    } else {
                        isValidateSuccess = false
                        validationMessage =
                            "${resources.getString(R.string.MSG_ENTER_AT_LEAST_ONE_VALUE_FOR)} $profileCode"
                    }
//                    checkIfDiastolicIsGreaterThanSystolic()
                    if (isValidateSuccess) {
                        if (IS_WHR || IS_BMI || IS_BP) {
                            /*val parameterToSync = ArrayMap<String, String>()
                            parameterToSync[GlobalConstants.RECORD_DATE] = recordDateSync
                            parameterToSync[GlobalConstants.PROFILE_CODE] = profileCode
                            parameterToSync[GlobalConstants.PERSON_ID] =
                                SessionManager.GetSessionDetails()
                                    .get(GlobalConstants.CURRENT_PERSON_ID)
                            HealthParametersDBHelper.markRecordToSync(parameterToSync)*/
                        }
                        //Synchronization calls
                        /*if (profileCode == "BMI") {
                            SyncManager.initiatePushDataSyncAsync("BMI")
                        } else if (profileCode == "WHR") {
                            SyncManager.initiatePushDataSyncAsync("WHR")
                        } else if (profileCode.equals(
                                "BP",
                                ignoreCase = true
                            ) || profileCode.equals("BLOODPRESSURE", ignoreCase = true)
                        ) {
                            SyncManager.initiatePushDataSyncAsync("BP")
                        } else {
                            SyncManager.initiatePushDataSyncAsync("LABPARAMETER")
                        }*/
                    }
                } else {
                    viewModel.showMessage(validationMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun registerObserver() {

        // Observer
        viewModel.saveParam.observe(viewLifecycleOwner) {
            Utilities.printLog("Inside SAVE API Call Response: $it")
        }

        viewModel.selectedParameter.observe(viewLifecycleOwner) {
            var counter = 0
            if (it.isNotEmpty()) {
                if (isFirstTime) {
                    for (item in it) {
                        if (item.profileCode.equals(profileCode, true)) {
                            break
                        } else {
                            counter++
                        }
                    }
                    profileSelectionAdapter!!.selectedPosition = counter
                    profileSelectionAdapter!!.updateData(it)

                    paramAdapter = RevInputParamAdapter(profileCode, requireContext(), viewModel)
                    binding.rvInputParameters.adapter = paramAdapter
                    viewModel.getParameterByProfileCodeAndDate(profileCode, serverDate)
                    paramAdapter!!.profileCode = profileCode
                    binding.rvProfileSelection.scrollToPosition(profileSelectionAdapter!!.selectedPosition)
                } else {
                    profileSelectionAdapter!!.updateData(it)
                }
            }
        }

        viewModel.inputParamList.observe(viewLifecycleOwner) {
            it?.let {
                paramAdapter!!.updateData(it)
            }
        }

    }

    private fun updateData(position: Int, pCode: String) {
        Utilities.printLog("Position: $position :: $profileCode")
        paramAdapter = RevInputParamAdapter(pCode, requireContext(), viewModel)
        binding.rvInputParameters.adapter = paramAdapter
        viewModel.getParameterByProfileCodeAndDate(pCode, serverDate)
        paramAdapter!!.profileCode = pCode
        viewModel.refreshSelectedParamList(args.showAllProfile)
        profileCode = pCode
    }

    private fun showDatePicker() {
        if (fragmentManager != null) {
            DialogHelper().showDatePickerDialog(resources.getString(R.string.RECORD_DATE),
                requireContext(),
                Calendar.getInstance(),
                null,
                Calendar.getInstance(),

                object : DialogHelper.DateListener {

                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {

                        val selectedDate = "$dayOfMonth/$month/$year"
                        if (!selectedDate.isNullOrEmpty()) {
                            serverDate = DateHelper.convertDateTimeValue(
                                selectedDate,
                                DateHelper.DISPLAY_DATE_DDMMYYYY,
                                DateHelper.DISPLAY_DATE_DDMMMYYYY
                            ).toString()
                            binding.edtDate.setText(
                                DateHelper.convertDateTimeValue(
                                    selectedDate,
                                    DateHelper.DISPLAY_DATE_DDMMYYYY,
                                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                                )
                            )
                            viewModel.getParameterByProfileCodeAndDate(profileCode, serverDate)
                        }
                    }
                })
        }
    }

    private fun checkParameterEmpty(parameters: ArrayList<ParameterListModel.InputParameterModel>?): Boolean {
        val isParameterEmpty: Boolean
        var counter = 0
        for (j in parameters!!.indices) {
            val parameter: ParameterListModel.InputParameterModel = parameters[j]
            val strValue: String? = parameter.parameterVal
            val strTextValue: String? = parameter.parameterTextVal
            if (TrackParameterHelper.isNullOrEmptyOrZero(strValue) && strTextValue.isNullOrEmpty()) {
                counter += 1
            }
        }
        isParameterEmpty = counter == parameters.size
        return isParameterEmpty
    }

    /*    private fun updateTabLayout(list: List<ParameterListModel.SelectedParameter>?) {
            if (!list.isNullOrEmpty()) {

                for (item in list) {
                    Utilities.printLog("TAG=> List :: " + item.profileName.replace("Profile",""))
                    var tab = binding.tabLayoutProfile.newTab().setText(item.profileName.replace("Profile",""))
                        .setTag(item.profileCode)
                    if(!item.profileCode.equals("URINE",true)) {
                        binding.tabLayoutProfile.addTab(tab)
                    }
                }

                for (i in 0..binding.tabLayoutProfile.tabCount) {
                    Utilities.printLog("TAG=> " + binding.tabLayoutProfile.getTabAt(i)?.text)
                    if (binding.tabLayoutProfile.getTabAt(i)?.tag?.equals(profileCode) == true) {
                        binding.tabLayoutProfile.scrollX = binding.tabLayoutProfile.width
                        binding.tabLayoutProfile.getTabAt(i)!!.select()
                    }
                }

            }
        }*/

}