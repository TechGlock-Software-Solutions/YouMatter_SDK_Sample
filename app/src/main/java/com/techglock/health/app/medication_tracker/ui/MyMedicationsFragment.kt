package com.techglock.health.app.medication_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.common.view.SpinnerAdapter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.databinding.FragmentMyMedicationsBinding
import com.techglock.health.app.medication_tracker.adapter.MyMedicationsAdapter
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.entity.MedicationEntity.Medication
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMedicationsFragment(private val fragment: MedicineHomeFragment) : BaseFragment(),
    DefaultNotificationDialog.OnDialogValueListener, OptionsBottomSheet.OnOptionClickListener {

    private val viewModel: MedicineTrackerViewModel by lazy {
        ViewModelProvider(this)[MedicineTrackerViewModel::class.java]
    }
    lateinit var binding: FragmentMyMedicationsBinding


    var selectedTab = 0
    private var dialogClickType = "Delete"
    private var medicine = Medication()
    private var myMedicationsAdapter: MyMedicationsAdapter? = null
    private var spinnerAdapter: SpinnerAdapter? = null
    private var categoryList: ArrayList<SpinnerModel>? = null
    private var animation: LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyMedicationsBinding.inflate(inflater, container, false)
        /*        if (userVisibleHint) {
                    initialise()
                    registerObservers()
                }*/
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initialise()
        registerObservers()
        setClickable()
    }

    /*    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
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
        }*/

    private fun initialise() {
        selectedTab = fragment.tab
        Utilities.printLogError("selectedTab--->$selectedTab")

        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        categoryList = viewModel.medicationTrackerHelper.getCategoryList()
        spinnerAdapter = SpinnerAdapter(requireContext(), categoryList!!)
        binding.spinnerMedication.adapter = spinnerAdapter

        myMedicationsAdapter = MyMedicationsAdapter(viewModel, requireContext(), this)
        //binding.rvMedications.layoutAnimation = animation
        binding.rvMedications.adapter = myMedicationsAdapter

        binding.spinnerMedication.setSelection(selectedTab)
        //updateData(selectedTab)
    }

    private fun registerObservers() {

        viewModel.fetchMedicationList(this)
        viewModel.medicineList.observe(this.viewLifecycleOwner, Observer {
/*            if (it != null) {
                updateData(selectedTab)
            }*/
        })
        viewModel.medicinesList.observe(this.viewLifecycleOwner, Observer {
            if (it != null) {
                //Utilities.printData("DBList",it)
                updateMedicationList(it.toMutableList(), selectedTab)
            }
        })
        viewModel.deleteMedicine.observe(this.viewLifecycleOwner, Observer {

        })
    }

    private fun setClickable() {

        binding.spinnerMedication.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerAdapter!!.selectedPos = position
                val name: String = categoryList!![position].name
                binding.txtModelSpinner.text = name
                selectedTab = categoryList!![position].position
                fragment.tab = selectedTab
                Utilities.printLog("Selected Item=>$selectedTab,$name")
                updateData(selectedTab)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtModelSpinner.setOnClickListener {
            binding.spinnerMedication.performClick()
        }

        binding.imgDropDown.setOnClickListener {
            binding.spinnerMedication.performClick()
        }

        binding.layoutAddMedication.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, Constants.MEDICATION)
            bundle.putInt(Constants.TAB, selectedTab)
            fragment.navigateFromMyMedicationsToAddScreen(bundle)
        }

    }

    private fun updateMedicationList(
        medicineList: MutableList<MedicationEntity.Medication>,
        position: Int
    ) {
        if (medicineList.size > 0) {
            binding.rvMedications.visibility = View.VISIBLE
            binding.layoutNoMedicines.visibility = View.GONE
            binding.rvMedications.layoutAnimation = animation
            myMedicationsAdapter!!.updateMedicines(medicineList, position)
            binding.rvMedications.scheduleLayoutAnimation()
        } else {
            binding.rvMedications.visibility = View.GONE
            binding.layoutNoMedicines.visibility = View.VISIBLE
        }
    }

    fun updateData(position: Int) {
        selectedTab = position
        Utilities.printLogError("updateData:selectedTab--->$selectedTab")
        when (position) {
            0 -> viewModel.getOngoingMedicines(this)
            1 -> viewModel.getCompletedMedicines(this)
            2 -> viewModel.getAllMyMedicines(this)
        }
    }

    override fun onOptionClick(code: String, position: Int, medicine: MedicationEntity.Medication) {
        when (code) {
            Constants.EDIT -> {
                val medName = if (!Utilities.isNullOrEmpty(medicine.drug.strength)) {
                    medicine.drug.name + " - " + medicine.drug.strength
                } else {
                    medicine.drug.name
                }
                val bundle = Bundle()
                bundle.putString("medicine", Gson().toJson(medicine))
                bundle.putInt(Constants.TAB, position)
                bundle.putString(Constants.FROM, Constants.MEDICATION)
                bundle.putInt(Constants.MEDICATION_ID, medicine.medicationId)
                bundle.putInt(Constants.Drug_ID, medicine.drugID)
                bundle.putString(Constants.MEDICINE_NAME, medName)
                if (!Utilities.isNullOrEmpty(medicine.DrugTypeCode)) {
                    bundle.putString(Constants.DRUG_TYPE_CODE, medicine.DrugTypeCode)
                }
                fragment.navigateToScheduleScreen(bundle)
                //findNavController().navigate(R.id.action_myMedicationsFragment_to_scheduleMedicineFragment, bundle)
            }

            Constants.DELETE -> {
                showDeleteMedicineDialog(medicine)
            }
        }
    }

    fun showDeleteMedicineDialog(medicineDetails: MedicationEntity.Medication) {
        dialogClickType = "Delete"
        medicine = medicineDetails
        val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        showDialog(
            listener = this,
            title = this.resources.getString(R.string.DELETE_MEDICINE) + "?",
            message = this.resources.getString(R.string.MSG_CONFIRMATION_DELETE_MEDICINE),
            leftText = this.resources.getString(R.string.CANCEL),
            rightText = this.resources.getString(R.string.CONFIRM),
            showLeftBtn = true,
            hasErrorBtn = true
        )
    }

    fun showRescheduleDialog(medicineDetails: MedicationEntity.Medication) {
        dialogClickType = "Reschedule"
        medicine = medicineDetails
        val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        showDialog(
            listener = this,
            title = this.resources.getString(R.string.RESCHEDULE_MEDICINE),
            message = this.resources.getString(R.string.MSG_CONFIRMATION_RESCHEDULE_MEDICINE) + " " + medName + "?",
            leftText = this.resources.getString(R.string.CANCEL),
            rightText = this.resources.getString(R.string.CONFIRM),
            showLeftBtn = true
        )
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            try {
                if (dialogClickType.equals("Delete", ignoreCase = true)) {
                    viewModel.callDeleteMedicineApi(selectedTab, medicine, this)
                }
                if (dialogClickType.equals("Reschedule", ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putString(Constants.FROM, "Reschedule")
                    bundle.putInt(Constants.MEDICATION_ID, 0)
                    bundle.putString(
                        Constants.MEDICINE_NAME,
                        medicine.drug.name + " - " + medicine.drug.strength
                    )
                    bundle.putInt(Constants.Drug_ID, medicine.drugID)
                    bundle.putString(Constants.DRUG_TYPE_CODE, medicine.DrugTypeCode)
                    findNavController().navigate(
                        R.id.action_myMedicationsFragment_to_scheduleMedicineFragment,
                        bundle
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*    fun performBackClick() {
            if (tab.equals("Dashboard", ignoreCase = true)) {
                val bundle = Bundle()
                bundle.putString(Constants.DATE, selectedDate)
                findNavController().navigate(R.id.action_myMedicationsFragment_to_medicineDashboardFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_myMedicationsFragment_to_medicineHome)
            }
        }*/

}