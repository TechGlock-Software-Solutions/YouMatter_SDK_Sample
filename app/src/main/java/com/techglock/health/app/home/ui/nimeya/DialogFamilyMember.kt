package com.techglock.health.app.home.ui.nimeya

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.view.SpinnerAdapter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.databinding.DialogFamilyMemberBinding
import com.techglock.health.app.model.nimeya.SaveProtectoMeterModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogFamilyMember(
    val mContext: Context,
    private val familyMember: SaveProtectoMeterModel.FamilyDetail,
    private val addEditListener: OnAddEditClickListener
) : Dialog(mContext) {

    private lateinit var binding: DialogFamilyMemberBinding
    private var spinnerAdapterRelation: SpinnerAdapter? = null
    private var spinnerAdapterIsDependent: SpinnerAdapter? = null
    private var relationList: ArrayList<SpinnerModel>? = null
    private var dependentList: ArrayList<SpinnerModel>? = null
    private var from = Constants.ADD
    private val cal = Calendar.getInstance()

    //private val calMin = Calendar.getInstance()
    private val df1 = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogFamilyMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        initialise()
        setClickable()
    }

    private fun initialise() {
        relationList = getRelationList()
        spinnerAdapterRelation = SpinnerAdapter(context, relationList!!)
        binding.spinnerRelation.adapter = spinnerAdapterRelation

        dependentList = getDependentList()
        spinnerAdapterIsDependent = SpinnerAdapter(context, dependentList!!)
        binding.spinnerIsDependent.adapter = spinnerAdapterIsDependent

        Utilities.printData("familyMember", familyMember, true)
        if (!Utilities.isNullOrEmpty(familyMember.name)) {
            binding.btnAddEdit.text = "Update"
            from = Constants.UPDATE
            binding.edtName.setText(familyMember.name)
            binding.spinnerRelation.setSelection(getRelationPosByCode(familyMember.relation!!))
            binding.txtModelSpinnerRelation.text = familyMember.relation
            selectedDate = DateHelper.convertDateSourceToDestination(
                familyMember.dob,
                DateHelper.DATEFORMAT_DDMMMMYYYY,
                DateHelper.SERVER_DATE_YYYYMMDD
            )
            binding.txtDate.text = DateHelper.convertDateSourceToDestination(
                familyMember.dob,
                DateHelper.DATEFORMAT_DDMMMMYYYY,
                DateHelper.DATEFORMAT_DDMMMYYYY_NEW
            )
            binding.spinnerIsDependent.setSelection(getDependentPosByCode(familyMember.isDependent!!))
            binding.txtModelSpinnerIsDependent.text = familyMember.isDependent
        } else {
            binding.btnAddEdit.text = "Add"
            from = Constants.ADD
            binding.edtName.setText("")
            binding.txtModelSpinnerRelation.text = ""
            binding.txtDate.text = ""
            binding.txtModelSpinnerIsDependent.text = ""
        }
    }

    private fun setClickable() {

        binding.layoutDate.setOnClickListener {
            showDatePicker()
        }

        binding.tabRelation.setOnClickListener {
            binding.spinnerRelation.performClick()
        }

        binding.txtModelSpinnerRelation.setOnClickListener {
            binding.spinnerRelation.performClick()
        }

        binding.imgDropDownRelation.setOnClickListener {
            binding.spinnerRelation.performClick()
        }

        binding.tabIsDependent.setOnClickListener {
            binding.spinnerIsDependent.performClick()
        }

        binding.txtModelSpinnerIsDependent.setOnClickListener {
            binding.spinnerIsDependent.performClick()
        }

        binding.imgDropDownIsDependent.setOnClickListener {
            binding.spinnerIsDependent.performClick()
        }

        binding.spinnerRelation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerAdapterRelation!!.selectedPos = position
                    val name: String = relationList!![position].name
                    binding.txtModelSpinnerRelation.text = name
                    val selectedTab = relationList!![position].position
                    Utilities.printLog("Selected Item=>$selectedTab,$name")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerIsDependent.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerAdapterIsDependent!!.selectedPos = position
                    val name: String = dependentList!![position].name
                    binding.txtModelSpinnerIsDependent.text = name
                    val selectedTab = dependentList!![position].position
                    Utilities.printLog("Selected Item=>$selectedTab,$name")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.btnAddEdit.setOnClickListener {
            validate()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    private fun validate() {
        val name = binding.edtName.text.toString()
        val relation = binding.txtModelSpinnerRelation.text.toString()
        //val dob = binding.txtDate.text.toString()
        //val dob = selectedDate
        val isDependent = binding.txtModelSpinnerIsDependent.text.toString()
        if (Utilities.isNullOrEmpty(name)) {
            Utilities.toastMessageShort(mContext, "Please Enter name")
        } else if (Utilities.isNullOrEmpty(relation)) {
            Utilities.toastMessageShort(mContext, "Please Select Relation")
        } else if (Utilities.isNullOrEmpty(selectedDate)) {
            Utilities.toastMessageShort(mContext, "Please Select Date of Birth")
        } else {
            familyMember.name = name
            familyMember.relation = relation
            familyMember.dob = DateHelper.convertDateSourceToDestination(
                selectedDate,
                DateHelper.SERVER_DATE_YYYYMMDD,
                DateHelper.DATEFORMAT_DDMMMMYYYY
            )
            familyMember.age = DateHelper.calculatePersonAge(
                DateHelper.formatDateValue(
                    DateHelper.DISPLAY_DATE_DDMMMYYYY,
                    selectedDate
                )!!, mContext
            ).split(" ").toTypedArray()[0]
            familyMember.isDependent = isDependent
            addEditListener.onAddEditClick(from, familyMember)
            dismiss()
        }
    }

    private fun showDatePicker() {
        try {
            if (!Utilities.isNullOrEmpty(selectedDate)) {
                cal.time = df1.parse(selectedDate)!!
            }
            DialogHelper().showDatePickerDialog(mContext.resources.getString(R.string.SELECT_DATE),
                mContext, cal, null, Calendar.getInstance(), object : DialogHelper.DateListener {
                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {
                        selectedDate = DateHelper.convertDateSourceToDestination(
                            date,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY,
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                        Utilities.printLogError("SelectedDate--->$selectedDate")
                        //val date = year + "-" + (month + 1) + "-" + dayOfMonth
                        binding.txtDate.text =
                            DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnAddEditClickListener {
        fun onAddEditClick(from: String, familyMember: SaveProtectoMeterModel.FamilyDetail)
    }

    private fun getRelationList(): ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel("Spouse", "", 0, false))
        list.add(SpinnerModel("Son", "", 1, false))
        list.add(SpinnerModel("Daughter", "", 2, false))
        list.add(SpinnerModel("Father", "", 3, false))
        list.add(SpinnerModel("Mother", "", 4, false))
        return list
    }

    fun getRelationPosByCode(code: String): Int {
        return when (code.uppercase()) {
            "SPOUSE" -> 0
            "SON" -> 1
            "DAUGHTER" -> 2
            "FATHER" -> 3
            "MOTHER" -> 4
            else -> 0
        }
    }

    fun getDependentPosByCode(code: String): Int {
        return when (code.uppercase()) {
            "NO" -> 1
            else -> 0
        }
    }

    private fun getDependentList(): ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel("Yes", "YES", 0, false))
        list.add(SpinnerModel("No", "NO", 1, false))
        return list
    }

}