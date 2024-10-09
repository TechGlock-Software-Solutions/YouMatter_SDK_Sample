package com.techglock.health.app.medication_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.hideKeyboard
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentAddMedicineBinding
import com.techglock.health.app.medication_tracker.adapter.DrugsListAdapter
import com.techglock.health.app.medication_tracker.adapter.MedicineTypeAdapter
import com.techglock.health.app.medication_tracker.model.MedTypeModel
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.medication.DrugsModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicineFragment : BaseFragment(), MedicineTypeAdapter.OnMedTypeListener {

    private var medTypeList: ArrayList<MedTypeModel> = ArrayList()
    private val viewModel: MedicineTrackerViewModel by lazy {
        ViewModelProvider(this)[MedicineTrackerViewModel::class.java]
    }

    private lateinit var binding: FragmentAddMedicineBinding


    private var medicineTypeAdapter: MedicineTypeAdapter? = null

    //    private val medTypeList =
    private var from = ""
    private var selectedDate = ""
    private var selectedTab = 0
    private var medicationId = 0
    private var drugId = 0
    private var drugTypeCode = ""
    private var medicineName = ""

    internal var isSelectedMedicine = false
    private lateinit var drugListAdapter: DrugsListAdapter

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onStop() {
        super.onStop()
        if (viewModel.medicineListByDay.hasObservers()) {
            viewModel.medicineListByDay.removeObservers(viewLifecycleOwner)
        }
        if (viewModel.setAlert.hasObservers()) {
            viewModel.setAlert.removeObservers(viewLifecycleOwner)
        }
        if (viewModel.listMedicationInTake.hasObservers()) {
            viewModel.listMedicationInTake.removeObservers(viewLifecycleOwner)
        }
        if (viewModel.addMedicineIntake.hasObservers()) {
            viewModel.addMedicineIntake.removeObservers(viewLifecycleOwner)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicationId = it.getInt(Constants.MEDICATION_ID, 0)
            drugId = it.getInt(Constants.Drug_ID, 0)
            medicineName = it.getString(Constants.MEDICINE_NAME, "")!!
            drugTypeCode = it.getString(Constants.DRUG_TYPE_CODE, "")!!
            from = it.getString(Constants.FROM, "")
            selectedDate = it.getString(Constants.DATE, "")!!
            selectedTab = it.getInt(Constants.TAB, 0)
            Utilities.printLogError("MedicationID,from,selectedDate,selectedTab----->$medicationId,$from,$selectedDate,$selectedTab")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMedicineBinding.inflate(inflater, container, false)

        try {
            initialise()
            registerObservers()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        medTypeList.clear()
        medTypeList.addAll(viewModel.medicationTrackerHelper.getMedTypeList())

        //viewModel.getPastMedicines(this)
        medicineTypeAdapter = MedicineTypeAdapter(requireContext(), medTypeList, this)
        binding.rvMedicineType.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMedicineType.adapter = medicineTypeAdapter

        drugListAdapter = DrugsListAdapter(requireContext(), ArrayList())
        binding.edtMedicineName.setAdapter(drugListAdapter)

        val onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            isSelectedMedicine = true
            val medicineModel = adapterView.getItemAtPosition(i) as DrugsModel.DrugsResponse.Drug
            if (!Utilities.isNullOrEmpty(medicineModel.strength)) {
                binding.edtMedicineName.setText(medicineModel.name + " - " + medicineModel.strength)
            } else {
                binding.edtMedicineName.setText(medicineModel.name)
            }
            drugId = medicineModel.iD.toInt()
            //Utilities.hideKeyboard(view, requireContext())
//            KeyboardUtils.hideSoftInput(context as Activity)
            binding.edtMedicineName.hideKeyboard(true)
            binding.edtMedicineName.setSelection(binding.edtMedicineName.text.length)
        }
        binding.edtMedicineName.onItemClickListener = onItemClickListener

        if (!Utilities.isNullOrEmpty(drugTypeCode)) {
            //binding.imgMedicine.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))
            binding.edtMedicineName.setText(medicineName)
            if (drugTypeCode.equals("MOUTHWASH", ignoreCase = true)) {
                //binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
            } else {
                //binding.txtMedicine.setText(medicationTrackerHelper.getMedTypeByCode(drugTypeCode))
            }

            for (i in medTypeList.indices) {
                if (medTypeList[i].medTypeCode.equals(drugTypeCode, ignoreCase = true)) {
                    medicineTypeAdapter!!.updateSelectedPos(i)
                    break
                }
            }
        }
    }

    private fun registerObservers() {

        /*        viewModel.medicinesList.observe(viewLifecycleOwner) {
                    if (it != null) {
                        //setPastMedicines(it)
                    }
                }*/

        viewModel.drugsList.observe(viewLifecycleOwner) {
            try {
                Utilities.printLog("BindingAdapter=> $it")
                if (it != null) {
                    it.drugs.let { drugListAdapter.updateData(it) }
                    binding.edtMedicineName.showDropDown()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun setClickable() {

        binding.edtMedicineName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (isSelectedMedicine) {
                        isSelectedMedicine = false
                    }

                    if (!binding.edtMedicineName.isPerformingCompletion) {
                        if (editable.toString().length >= 3) {
                            viewModel.fetchDrugsList(editable.toString(), this@AddMedicineFragment)
                        }
                    }

                    if (editable.toString().length <= 1) {
                        binding.edtMedicineName.dismissDropDown()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.btnAddSchedule.setOnClickListener {
            if (!Utilities.isNullOrEmpty(drugTypeCode)) {
                if (!Utilities.isNullOrEmpty(binding.edtMedicineName.text.toString())) {
                    val bundle = Bundle()
                    val med = MedicationEntity.Medication()
                    bundle.putString("medicine", Gson().toJson(med))
                    bundle.putString(Constants.FROM, Constants.ADD)
                    bundle.putInt(Constants.MEDICATION_ID, medicationId)
                    bundle.putInt(Constants.Drug_ID, drugId)
                    bundle.putString(
                        Constants.MEDICINE_NAME,
                        binding.edtMedicineName.text.toString()
                    )
                    bundle.putString(Constants.DRUG_TYPE_CODE, drugTypeCode)
                    //viewModel.routeToScheduleDetails(it, bundle)
                    findNavController().navigate(
                        R.id.action_addMedicineFragment_to_scheduleMedicineFragment,
                        bundle
                    )
                } else {
                    Utilities.toastMessageShort(
                        context,
                        resources.getString(R.string.ERROR_ENTER_MEDICINE_NAME)
                    )
                }
            } else {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.ERROR_ENTER_MEDICINE_TYPE)
                )
            }
        }

    }


    /*    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        private fun setPastMedicines(list: List<MedicationEntity.Medication>) {
            try {
                binding.flowLayout.removeAllViews()
                if (list.isNotEmpty()) {
                    binding.layoutRecentlyAddedMedicines.visibility = View.VISIBLE
                    val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    par.setMargins(10, 10, 10, 10)
                    for (i in list.indices) {
                        val textView = TextView(context)
                        textView.apply {
                            //text = list[i].drug.name + " - " +list[i].drug.strength
                            text = if (!Utilities.isNullOrEmpty(list[i].drug.strength)) {
                                list[i].drug.name + " - " + list[i].drug.strength
                            } else {
                                list[i].drug.name
                            }
                            tag = i
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.size_12sp.toFloat())
                            setPadding(dpToPx(18f).toInt(), dpToPx(10f).toInt(), dpToPx(18f).toInt(), dpToPx(10f).toInt())
                            setTextAppearance(context, R.style.textFlowLayout)
                            layoutParams = par
                            background = ViewUtils.getArcRbSelectorBgHra(false)
                            setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(false))
                            setOnClickListener { view: View ->
                                val pos = view.tag as Int
                                deselectAll(view.tag.toString())
                                setMedicineDetails(list[pos])
                            }
                        }
                        Utilities.printLog("Option--->${list[i].drug.name}")
                        binding.flowLayout.addView(textView)
                    }
                } else {
                    binding.layoutRecentlyAddedMedicines.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun deselectAll(except: String) {
            for (i in 0 until binding.flowLayout.childCount) {
                if (!binding.flowLayout.getChildAt(i).tag.toString().equals(except, ignoreCase = true)) {
                    val textView = binding.flowLayout.getChildAt(i) as TextView
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.vivant_greyish))
                    textView.setBackgroundResource(R.drawable.btn_oval_round)
                }
            }
        }*/

    /*    @SuppressLint("SetTextI18n")
        private fun setMedicineDetails(details: MedicationEntity.Medication) {

            try {
                Utilities.printData("details", details, true)
                //drugTypeCode = details.DrugTypeCode!!
                if (!Utilities.isNullOrEmpty(details.DrugTypeCode!!)) {
                    drugTypeCode = details.DrugTypeCode!!
                } else {
                    drugTypeCode = "TAB"
                }

                if (drugTypeCode.equals("MOUTHWASH", ignoreCase = true)) {
                    binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
                } else {
                    binding.txtMedicine.text = resources.getString(medicationTrackerHelper.getMedTypeByCode(drugTypeCode))
                }
                binding.imgMedicine.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))
                //binding.edtMedicineName.setText(details.drug.name + " - " +details.drug.strength)
                if (!Utilities.isNullOrEmpty(details.drug.strength)) {
                    binding.edtMedicineName.setText(details.drug.name + " - " + details.drug.strength)
                } else {
                    binding.edtMedicineName.setText(details.drug.name)
                }
                binding.edtMedicineName.setSelection(binding.edtMedicineName.text.length)
                drugId = details.drugID
                var medTypePos = 0
                for (i in medTypeList.indices) {
                    if (medTypeList[i].medTypeCode.equals(drugTypeCode, ignoreCase = true)) {
                        medicineTypeAdapter!!.updateSelectedPos(i)
                        medTypePos = i
                        break
                    }
                }
                Utilities.printLog("Position,DrugTypeCode,DrugID==>" + medTypePos + "," + drugTypeCode + "," + drugId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    override fun onMedTypeSelection(position: Int, medType: MedTypeModel) {
        if (medType.medTypeCode.equals("MOUTHWASH", ignoreCase = true)) {
            //binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
        } else {
            //binding.txtMedicine.text = medType.medTypeTitle
        }
        //binding.imgMedicine.setImageResource(medType.medTypeImageId)
        drugTypeCode = medType.medTypeCode
        Utilities.printLog("Position,DRUG_TYPE_CODE----->" + position + " , " + medType.medTypeCode)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    fun performBackClick() {
        Utilities.printLogError("MedicationID,from,selectedDate----->$medicationId,$from,$selectedDate")
        val bundle = Bundle()
        if (from.equals(Constants.DASHBOARD, ignoreCase = true)) {
            bundle.putString(Constants.FROM, Constants.DASHBOARD)
            bundle.putString(Constants.DATE, selectedDate)
            findNavController().navigate(R.id.action_addMedicineFragment_to_medicineHome, bundle)
        } else if (from.equals(Constants.MEDICATION, ignoreCase = true)) {
            bundle.putString(Constants.FROM, Constants.MEDICATION)
            bundle.putInt(Constants.TAB, selectedTab)
            findNavController().navigate(R.id.action_addMedicineFragment_to_medicineHome, bundle)
        } else if (from.equals(Constants.TRACK_PARAMETER, ignoreCase = true)) {
            requireActivity().finish()
        } else {
            findNavController().navigate(R.id.action_addMedicineFragment_to_medicineHome)
        }
    }

}