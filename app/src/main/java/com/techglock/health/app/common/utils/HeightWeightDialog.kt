package com.techglock.health.app.common.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.R
import com.techglock.health.app.databinding.DialogVitalParameterBinding
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject


class HeightWeightDialog @Inject constructor(
    context: Context, listener: OnDialogValueListener, dialogType: String,
    parameterDataModel: ParameterDataModel
) : Dialog(context) {

    private lateinit var binding: DialogVitalParameterBinding

    private var onDialogValueListener: OnDialogValueListener? = null
    private var dialogType = "Height"
    private var parameterDataModel: ParameterDataModel? = null
    private var paramFt: VitalParameter? = null
    private var paramCm: VitalParameter? = null
    private var paramLbs: VitalParameter? = null
    private var paramKg: VitalParameter? = null
    private var pickerSpeed = 8000
    private var height = 0
    private var heightInch = 0
    private var weight = 0.0

    init {
        this.onDialogValueListener = listener
        this.dialogType = dialogType
        this.parameterDataModel = parameterDataModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogVitalParameterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.dialog_vital_parameter)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        init()
    }

    fun init() {
        try {
            paramFt =
                Utilities.getVitalParameterData(context.resources.getString(R.string.FT), context)
            paramCm =
                Utilities.getVitalParameterData(context.resources.getString(R.string.CM), context)
            paramLbs =
                Utilities.getVitalParameterData(context.resources.getString(R.string.LBS), context)
            paramKg =
                Utilities.getVitalParameterData(context.resources.getString(R.string.KG), context)

            //val linearLayout = layout_tab.getChildAt(0) as LinearLayout
            //linearLayout.dividerDrawable = null
            //linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_NONE

            binding.txtMessage

            if (dialogType.equals("Height", ignoreCase = true)) {
                binding.lblTitle.text = context.resources.getString(R.string.PICK_YOUR_HEIGHT)
                //txt_message.text = context.resources.getString(R.string.ENTER_HEIGHT)

                binding.layoutTab.getTabAt(0)!!.text = context.resources.getString(R.string.FT_IN)
                binding.layoutTab.getTabAt(1)!!.text = context.resources.getString(R.string.CM)

                binding.txtUnit1.text = paramFt!!.unit
                binding.txtUnit2.text = context.resources.getString(R.string.INCH)
                binding.picker1.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                binding.picker1.wrapSelectorWheel = false
                binding.picker1.minValue = paramFt!!.minRange
                binding.picker1.maxValue = paramFt!!.maxRange
                binding.picker2.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                binding.picker2.wrapSelectorWheel = false
                binding.picker2.minValue = 0
                binding.picker2.maxValue = 11

                if (parameterDataModel!!.unit.equals("Feet/inch", ignoreCase = true)) {
                    binding.layoutPicker2.visibility = View.VISIBLE
                    binding.layoutTab.getTabAt(0)!!.select()
                    binding.picker1.value =
                        CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                    binding.picker2.value =
                        CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                    binding.picker1.value =
                        CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                    binding.picker2.value =
                        CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                } else if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.CM),
                        ignoreCase = true
                    )
                ) {
                    binding.layoutPicker2.visibility = View.GONE
                    binding.layoutTab.getTabAt(1)!!.select()
                    binding.txtUnit1.text = paramCm!!.unit
                    binding.picker1.minValue = paramCm!!.minRange
                    binding.picker1.maxValue = paramCm!!.maxRange
                    binding.picker1.value = parameterDataModel!!.finalValue.toDouble().toInt()
                }
            } else {
                binding.lblTitle.text = context.resources.getString(R.string.PICK_YOUR_WEIGHT)
                binding.txtMessage.text = context.resources.getString(R.string.ENTER_WEIGHT)

                binding.layoutTab.getTabAt(0)!!.text = context.resources.getString(R.string.LBS)
                binding.layoutTab.getTabAt(1)!!.text = context.resources.getString(R.string.KG)

                binding.picker1.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                binding.picker1.wrapSelectorWheel = false
                binding.picker2.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                binding.picker2.wrapSelectorWheel = false

                if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.LBS),
                        ignoreCase = true
                    )
                ) {
                    binding.layoutPicker2.visibility = View.GONE
                    binding.layoutTab.getTabAt(0)!!.select()
                    binding.txtUnit1.text = paramLbs!!.unit
                    binding.picker1.minValue = paramLbs!!.minRange
                    binding.picker1.maxValue = paramLbs!!.maxRange
                    val wt = CalculateParameters.convertKgToLbs(parameterDataModel!!.finalValue)
                        .toDouble()
                    Utilities.printLog("Converted_Wt=>$wt")
                    binding.picker1.value = wt.toInt()
                } else if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.KG),
                        ignoreCase = true
                    )
                ) {
                    binding.layoutPicker2.visibility = View.VISIBLE
                    binding.layoutTab.getTabAt(1)!!.select()
                    binding.txtUnit1.text = "."
                    binding.txtUnit2.text = paramKg!!.unit
                    binding.picker1.minValue = paramKg!!.minRange
                    binding.picker1.maxValue = paramKg!!.maxRange
                    binding.picker2.minValue = 0
                    binding.picker2.maxValue = 9
                    val `val`: Double = parameterDataModel!!.finalValue.toDouble()
                    val indexOfDecimal = `val`.toString().indexOf(".")
                    binding.picker1.value = `val`.toString().substring(0, indexOfDecimal).toInt()
                    binding.picker2.value = `val`.toString().substring(indexOfDecimal + 1).toInt()
                }
            }
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    if (dialogType.equals("Height", ignoreCase = true)) {
                        binding.picker1.displayedValues = null
                        binding.layoutPicker2.visibility = View.VISIBLE
                        binding.txtUnit1.text = paramFt!!.unit
                        binding.picker1.minValue = paramFt!!.minRange
                        binding.picker1.maxValue = paramFt!!.maxRange
                        binding.picker1.value =
                            CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                        binding.picker2.value =
                            CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                        height = binding.picker1.value
                    } else {
                        binding.layoutPicker2.visibility = View.GONE
                        binding.txtUnit1.text = paramLbs!!.unit
                        binding.picker1.minValue = paramLbs!!.minRange
                        binding.picker1.maxValue = paramLbs!!.maxRange
                        val wt = CalculateParameters.convertKgToLbs(parameterDataModel!!.finalValue)
                            .toDouble().toInt()
                        binding.picker1.value = wt
                        weight =
                            (binding.picker1.value.toString() + "." + binding.picker2.value).toDouble()
                    }
                } else if (tab.position == 1) {
                    if (dialogType.equals("Height", ignoreCase = true)) {
                        binding.layoutPicker2.visibility = View.GONE
                        binding.txtUnit1.text = paramCm!!.unit
                        binding.picker1.minValue = paramCm!!.minRange
                        binding.picker1.maxValue = paramCm!!.maxRange
                        binding.picker1.value = parameterDataModel!!.finalValue.toDouble().toInt()
                        height = binding.picker1.value
                        heightInch = binding.picker2.value
                    } else {
                        binding.picker1.displayedValues = null
                        binding.layoutPicker2.visibility = View.VISIBLE
                        binding.txtUnit1.text = "."
                        binding.txtUnit2.text = paramKg!!.unit
                        binding.picker1.minValue = paramKg!!.minRange
                        binding.picker1.maxValue = paramKg!!.maxRange
                        binding.picker2.minValue = 0
                        binding.picker2.maxValue = 9
                        val `val`: Double = parameterDataModel!!.finalValue.toDouble()
                        val indexOfDecimal = `val`.toString().indexOf(".")
                        binding.picker1.value =
                            `val`.toString().substring(0, indexOfDecimal).toInt()
                        binding.picker2.value =
                            `val`.toString().substring(indexOfDecimal + 1).toInt()
                        weight =
                            (binding.picker1.value.toString() + "." + binding.picker2.value).toDouble()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.picker1
        binding.btnSave.setOnClickListener {
            try {
                Utilities.hideKeyboard(binding.btnSave, context)
                if (dialogType.equals("Height", ignoreCase = true)) {
                    height = binding.picker1.value
                    heightInch = binding.picker2.value
                    if (height in 4..8) {
                        onDialogValueListener!!.onDialogValueListener(
                            "Height",
                            CalculateParameters.convertFeetInchToCm(
                                height.toString(),
                                heightInch.toString()
                            ).toString(), "0", "Feet/inch", "$height'$heightInch''"
                        )
                    } else {
                        onDialogValueListener!!.onDialogValueListener(
                            "Height",
                            height.toString(), "0", "Cm", height.toString()
                        )
                    }
                } else {
                    if (binding.txtUnit1.text.toString()
                            .contains(context.resources.getString(R.string.LBS))
                    ) {
                        weight = binding.picker1.value.toDouble()
                        val finalValue = CalculateParameters.convertLbsToKg(weight.toString())
                        onDialogValueListener!!.onDialogValueListener(
                            "Weight",
                            "0", finalValue, paramLbs!!.unit, weight.toString()
                        )
                    } else {
                        weight =
                            (binding.picker1.value.toString() + "." + binding.picker2.value).toDouble()
                        onDialogValueListener!!.onDialogValueListener(
                            "Weight",
                            "0", weight.toString(), paramKg!!.unit, weight.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismiss()
        }
        binding.picker1
        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    interface OnDialogValueListener {
        fun onDialogValueListener(
            dialogType: String,
            height: String,
            weight: String,
            unit: String,
            visibleValue: String
        )
    }

}