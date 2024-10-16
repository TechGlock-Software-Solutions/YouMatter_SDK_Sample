package com.techglock.health.app.track_parameter.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.DecimalValueFilter
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemInputParametersBinding
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.techglock.health.app.track_parameter.viewmodel.UpdateParamViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern

class RevInputParamAdapter(
    var profileCode: String,
    val context: Context,
    val viewModel: UpdateParamViewModel
) :
    RecyclerView.Adapter<RevInputParamAdapter.InputParameterViewHolder>() {

    val dataList: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
    private var edtBMI: EditText? = null
    private var edtWHR: EditText? = null
    var validationMassage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InputParameterViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_input_parameters, parent, false)
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: InputParameterViewHolder, position: Int) =
        holder.bindTo(dataList[position], position)

    fun updateData(items: List<ParameterListModel.InputParameterModel>) {
        dataList.clear()
        dataList.addAll(filterList(items))
        Utilities.printLog("Inside updateData $dataList")
        this.notifyDataSetChanged()
    }

    private fun filterList(items: List<ParameterListModel.InputParameterModel>): Collection<ParameterListModel.InputParameterModel> {
        var filterList: List<ParameterListModel.InputParameterModel> = mutableListOf()
        filterList = if (items[0].profileCode.equals("BMI")) {
            updateBMISequence(items)
        } else if (items[0].profileCode.equals("WHR")) {
            updateWHRSequence(items)
        } else if (items[0].profileCode.equals("BLOODPRESSURE")) {
            updateBPSequence(items)
        } else if (items[0].profileCode.equals("DIABETIC")) {
            items.sortedBy { it.description }
        } else {
            items.filter { item ->
                !item.parameterCode.equals("WBC", true)
                        && !item.parameterCode.equals("DLC", true)
            }
        }
        return filterList
    }

    private fun updateWHRSequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        val whrList: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var whr: ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var waist: ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var hip: ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        for (item in items) {
            if (item.parameterCode.equals("WHR")) {
                whr = item
            }
            if (item.parameterCode.equals("WAIST")) {
                waist = item
            }
            if (item.parameterCode.equals("HIP")) {
                hip = item
            }
        }
        whrList.add(hip)
        whrList.add(waist)
        whrList.add(whr)
        return whrList
    }

    private fun updateBPSequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        val bpList: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var systolic: ParameterListModel.InputParameterModel =
            ParameterListModel.InputParameterModel()
        var diastolic: ParameterListModel.InputParameterModel =
            ParameterListModel.InputParameterModel()
        var pulse: ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        for (item in items) {
            if (item.parameterCode.equals("BP_SYS")) {
                systolic = item
            }
            if (item.parameterCode.equals("BP_DIA")) {
                diastolic = item
            }
            if (item.parameterCode.equals("BP_PULSE")) {
                pulse = item
            }
        }
        bpList.add(systolic)
        bpList.add(diastolic)
        bpList.add(pulse)
        return bpList
    }

    private fun updateBMISequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        val bmiList: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var bmi: ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var height: ParameterListModel.InputParameterModel =
            ParameterListModel.InputParameterModel()
        var weight: ParameterListModel.InputParameterModel =
            ParameterListModel.InputParameterModel()
        for (item in items) {
            if (item.parameterCode.equals("BMI")) {
                bmi = item
            }
            if (item.parameterCode.equals("HEIGHT")) {
                height = item
            }
            if (item.parameterCode.equals("WEIGHT")) {
                weight = item
            }
        }
        bmiList.add(height)
        bmiList.add(weight)
        bmiList.add(bmi)
        return bmiList
    }

    inner class InputParameterViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemInputParametersBinding.bind(parent)

        fun bindTo(parameter: ParameterListModel.InputParameterModel, position: Int) {
            Utilities.printLog("DataAdapter=> " + parameter.minPermissibleValue + " : " + parameter.maxPermissibleValue + " : " + parameter.parameterUnit)
            binding.view.setBackgroundColor(binding.view.resources.getColor(getRandomColor(position)))
            binding.txtParamName.text = parameter.description
            binding.imgParam.setImageResource(
                TrackParameterHelper.getParameterImageByProfileCode(
                    profileCode
                )
            )

            binding.edtInputValue.hint = getHint(parameter)

            if (!parameter.parameterUnit.isNullOrEmpty()) {
                binding.txtParamUnit.text = parameter.parameterUnit
            } else {
                binding.txtParamUnit.text = ""
            }
            if (parameter.parameterType.equals("Value", true)) {
                binding.edtInputValue.setText(parameter.parameterVal)
                binding.edtInputValue.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                val decimalValueFilter = DecimalValueFilter(true)
                if (parameter.parameterCode.equals("BP_SYS")
                    || parameter.parameterCode.equals("BP_DIA")
                    || parameter.parameterCode.equals("BP_PULSE")
                ) {
                    decimalValueFilter.setDigits(0)
                    binding.edtInputValue.filters =
                        arrayOf(decimalValueFilter, InputFilter.LengthFilter(4))
                    binding.edtInputValue.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                } else {
//                    if(parameter.parameterCode.equals("SPECIFIC_GRAVITY")) {
//                        decimalValueFilter.setDigits(5)
//                    }else{
                    decimalValueFilter.setDigits(5)
//                    }
                    binding.edtInputValue.filters =
                        arrayOf(decimalValueFilter, InputFilter.LengthFilter(8))
                }
            } else {
                binding.edtInputValue.inputType = InputType.TYPE_CLASS_TEXT
                binding.edtInputValue.filters = arrayOf(filter, InputFilter.LengthFilter(60))
                binding.edtInputValue.setText(parameter.parameterTextVal)
            }
            if (dataList[position].parameterCode.equals("BMI", true)) {
                edtBMI = binding.edtInputValue
                //            binding.mainLayout.setAlpha(0.7f);
                binding.edtInputValue.inputType = 0
                binding.edtInputValue.isClickable = false
                binding.edtInputValue.isFocusable = false
                binding.edtInputValue.isFocusableInTouchMode = false
                if (!parameter.parameterVal.isNullOrEmpty()) {
                    val observation: String =
                        CalculateParameters.getBMIObservation(parameter.parameterVal!!, context)
//                    val observation: String = ""
                    if (!observation.isNullOrEmpty()) {
                        binding.edtInputValue.setTextColor(
                            binding.edtInputValue.resources.getColor(
                                TrackParameterHelper.getObservationColor(observation, "BMI")
                            )
                        )
                    }
                }
            } else
                if (dataList[position].parameterCode.equals("WHR", true)) {
                    edtWHR = binding.edtInputValue
                    //            binding.mainLayout.setAlpha(0.7f);
                    binding.edtInputValue.inputType = 0
                    binding.edtInputValue.isFocusable = false
                    binding.edtInputValue.isFocusableInTouchMode = false
                    if (!parameter.parameterVal.isNullOrEmpty()) {
                        val observation: String = CalculateParameters.getWHRObservation(
                            parameter.parameterVal!!,
                            1,
                            context
                        )
//                    val observation: String = ""
                        if (!observation.isNullOrEmpty()) {
                            binding.edtInputValue.setTextColor(
                                binding.edtInputValue.resources.getColor(
                                    TrackParameterHelper.getObservationColor(observation, "WHR")
                                )
                            )
                        }
                    }
                }/*else{
                binding.edtInputValue.isFocusable = true
                binding.edtInputValue.isFocusableInTouchMode = false
                if (parameter.parameterType.equals("Value",true)) {
                    binding.edtInputValue.setText(parameter.parameterVal)
                    binding.edtInputValue.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                    val decimalValueFilter = DecimalValueFilter(true)
                    if (parameter.parameterCode.equals("BP_SYS") || parameter.parameterCode
                            .equals("BP_DIA") || parameter.parameterCode.equals("BP_PULSE")
                    ) {
                        decimalValueFilter.setDigits(0)
                        binding.edtInputValue.filters =
                            arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(4))
                        binding.edtInputValue.inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                    } else {
                        decimalValueFilter.setDigits(2)
                        binding.edtInputValue.filters =
                            arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(6))
                    }
                } else {
                    binding.edtInputValue.inputType = InputType.TYPE_CLASS_TEXT
                    binding.edtInputValue.filters = arrayOf(filter, InputFilter.LengthFilter(60))
                    binding.edtInputValue.setText(parameter.parameterTextVal)
                }

            }*/

            binding.edtInputValue.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                override fun afterTextChanged(editable: Editable) {
                    if (!dataList[adapterPosition].parameterCode.equals("BMI", true) &&
                        !dataList[adapterPosition].parameterCode.equals("WHR", true)
                    ) {
                        if (dataList[adapterPosition].parameterType.equals("value", true)) {
                            dataList[adapterPosition].parameterVal = editable.toString()
                            if (profileCode.equals("BMI", ignoreCase = true)) {
                                updateBMIParameter()
                            } else if (profileCode.equals("WHR", ignoreCase = true)) {
                                updateWHRParameter()
                            }
                        } else {
                            dataList[adapterPosition].parameterTextVal = editable.toString().trim()
                        }
                    }
                }
            })
            if (parameter.parameterCode.equals("HEIGHT")) {
                binding.layoutInputNonBmi.visibility = View.GONE
                binding.layoutHeightWeight.visibility = View.VISIBLE
                binding.edtInputValueBmi.isFocusable = false
                binding.edtInputValueBmi.isCursorVisible = false
                binding.edtInputValueBmi.isFocusableInTouchMode = false
                binding.edtInputValueBmi.hint = binding.edtInputValue.hint

                if (viewModel.getPreference("HEIGHT") == "cm") {
                    var heightValue: String = ""
                    if (!binding.edtInputValue.text.toString()
                            .isNullOrEmpty() && binding.edtInputValue.text.toString()
                            .contains(".", true)
                    ) {
                        heightValue =
                            binding.edtInputValue.text.toString().toDouble().toInt().toString()
                        binding.edtInputValueBmi.setText(heightValue)
                    } else {
                        binding.edtInputValueBmi.text = binding.edtInputValue.text
                    }
                    binding.txtParamUnitBmi.text = binding.txtParamUnit.text
                } else {
                    var text: String = binding.edtInputValue.text.toString()
                    if (!text.isNullOrEmpty()) {
                        binding.edtInputValueBmi.setText(
                            CalculateParameters.convertCmToFeetInch(
                                text
                            )
                        )
                        binding.txtParamUnitBmi.text = ""
                    }
                }
                binding.edtInputValueBmi.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = context.resources.getString(R.string.HEIGHT)
                    data.value = " - - "
                    if (dataList[adapterPosition].parameterVal.isNullOrEmpty()) {
                        data.finalValue = "0"
                    } else {
                        data.finalValue = dataList.get(adapterPosition).parameterVal!!
                    }
                    if (viewModel.getPreference("HEIGHT") == "cm") {
                        data.unit = context.resources.getString(R.string.CM)
                    } else {
                        data.unit = context.resources.getString(R.string.FEET_INCH)
                    }
//                        data.unit = "Feet/inch"
//                        data.unit = "lbs"
//                        data.unit = "Kg"
                    data.code = "HEIGHT"
                    val heightWeightDialog = HeightWeightDialog(
                        binding.edtInputValue.context,

                        object : HeightWeightDialog.OnDialogValueListener {

                            override fun onDialogValueListener(
                                dialogType: String,
                                height: String,
                                weight: String,
                                unit: String,
                                visibleValue: String
                            ) {
                                viewModel.updateUserPreference(unit)
                                binding.edtInputValue.setText(height)
                                if (unit.equals("cm", true)) {
                                    binding.edtInputValueBmi.setText(height)
                                    binding.txtParamUnitBmi.text =
                                        context.resources.getString(R.string.CM)
                                } else {
                                    binding.edtInputValueBmi.setText(
                                        CalculateParameters.convertCmToFeetInch(
                                            height
                                        )
                                    )
                                    binding.txtParamUnitBmi.text = ""
                                }
                            }
                        }, "Height",
                        data
                    )
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }

            if (parameter.parameterCode.equals("WEIGHT")) {
                binding.layoutInputNonBmi.visibility = View.GONE
                binding.layoutHeightWeight.visibility = View.VISIBLE
                binding.edtInputValueBmi.isFocusable = false
                binding.edtInputValueBmi.isCursorVisible = false
                binding.edtInputValueBmi.isFocusableInTouchMode = false
                binding.edtInputValueBmi.hint = binding.edtInputValue.hint

                if (viewModel.getPreference("WEIGHT") == "kg") {
                    binding.edtInputValueBmi.text = binding.edtInputValue.text
                    binding.txtParamUnitBmi.text = binding.txtParamUnit.text
                } else {
                    val text: String = binding.edtInputValue.text.toString()
                    if (!text.isNullOrEmpty()) {
                        binding.edtInputValueBmi.setText(CalculateParameters.convertKgToLbs(text))
                        binding.txtParamUnitBmi.text = context.resources.getString(R.string.LBS)
                    }
                }
                binding.edtInputValueBmi.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = context.resources.getString(R.string.WEIGHT)
                    data.value = " - - "
                    if (dataList[adapterPosition].parameterVal.isNullOrEmpty()) {
                        data.finalValue = "50"
                    } else {
                        data.finalValue = dataList[adapterPosition].parameterVal!!

                    }
                    if (viewModel.getPreference("WEIGHT").equals("kg")) {
                        data.unit = context.resources.getString(R.string.KG)
                    } else {
                        data.unit = context.resources.getString(R.string.LBS)
                    }
//                        data.unit = "Feet/inch"
//                        data.unit = "lbs"
//                        data.unit = "Kg"
                    data.code = "WEIGHT"

                    val heightWeightDialog = HeightWeightDialog(
                        binding.edtInputValue.context,
                        object : HeightWeightDialog.OnDialogValueListener {

                            override fun onDialogValueListener(
                                dialogType: String,
                                height: String,
                                weight: String,
                                unit: String,
                                visibleValue: String
                            ) {
                                viewModel.updateUserPreference(unit)
                                binding.edtInputValue.setText(weight)
                                if (unit.equals("kg", true)) {
                                    binding.edtInputValueBmi.setText(weight)
                                    binding.txtParamUnitBmi.text =
                                        context.resources.getString(R.string.KG)
                                } else {
                                    var strWeight = ""
                                    if (!weight.isNullOrEmpty()) {
                                        strWeight = Utilities.roundOffPrecision(
                                            CalculateParameters.convertKgToLbs(weight).toDouble(), 0
                                        ).toInt().toString()
                                    }
                                    binding.edtInputValueBmi.setText(strWeight)
                                    binding.txtParamUnitBmi.text =
                                        context.resources.getString(R.string.LBS)
                                }
                            }
                        },
                        "Weight",
                        data
                    )
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }

            /*if(parameter.parameterCode.equals("WEIGHT")){
                binding.edtInputValue.isFocusable = false
                binding.edtInputValue.isCursorVisible = false
                binding.edtInputValue.isFocusableInTouchMode = false
                binding.edtInputValue.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = "Weight"
                    data.value = " - - "
                    if (dataList.get(adapterPosition).parameterVal.isNullOrEmpty()) {
                        data.finalValue = "0"
                    }else{
                        data.finalValue = dataList.get(adapterPosition).parameterVal!!
                    }
                    data.unit = "Kg"
                    data.code = "WEIGHT"
                    val heightWeightDialog = HeightWeightDialog(binding.edtInputValue.context,object:HeightWeightDialog.OnDialogValueListener {
                        override fun onDialogValueListener(
                            dialogType: String,
                            height: String,
                            weight: String,
                            unit: String,
                            visibleValue: String
                        ) {
                            viewModel.updateUserPreference(unit)
                            binding.edtInputValue.setText(weight)
                        }
                    },"Weight", data)
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }*/
        }

        private fun getHint(parameter: ParameterListModel.InputParameterModel): String {

            return if (parameter.maxPermissibleValue.isNullOrEmpty() || parameter.minPermissibleValue.isNullOrEmpty()) {
                ""
            } else {
                parameter.minPermissibleValue.toString() + " - " + parameter.maxPermissibleValue
            }
        }

    }

    private fun calculateBMI(heightVal: Double, weightVal: Double): String {
        var bmi = 0.0
        var bmiAsString = ""
        var height: String? = ""
        var weight: String? = ""
        try {
            try {
                height = heightVal.toString()
                weight = weightVal.toString()
                val weightValue = weight.toFloat()
                val heightValue = height.toFloat()
                if (weightValue >= Constants.WEIGHT_MIN_METRIC && weightValue <= Constants.WEIGHT_MAX_METRIC && heightValue >= Constants.HEIGHT_MIN && heightValue <= Constants.HEIGHT_MAX) {
                    bmi = (weightValue / (heightValue * heightValue) * 100 * 100).toDouble()
                    bmiAsString = DecimalFormat("##.##").format(bmi).toString()
                    if (bmiAsString.contains(",", true)) {
                        bmiAsString = bmiAsString.replace(",", ".")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            height = null
            weight = null
        }
        return bmiAsString
    }

    private fun calculateWHR(waistVal: Double, hipVal: Double): String {
        var waist: String? = ""
        var hip: String? = ""
        var whr = 0f
        var whrAsString = ""
        try {
            waist = waistVal.toString()
            hip = hipVal.toString()
            if (waist.isNullOrEmpty()) {
                return ""
            } else if (hip.isNullOrEmpty()) {
                return ""
            } else {
                try {
                    val waistValue = waist.toFloat()
                    val hipValue = hip.toFloat()
                    if (waistValue >= 63.5 && waistValue <= 165.2 && hipValue >= 63.5 && hipValue <= 165.2) {
                        whr = waistValue / hipValue
                        val nm = NumberFormat.getNumberInstance()
                        //whrAsString=String.valueOf(whr);
                        //whrAsString=(nm.format(whr));
                        whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            waist = null
            hip = null
        }
        return whrAsString
    }

    var filter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ]*")
                    .matcher(source[i].toString()).matches()
            ) {
                return@InputFilter ""
            }
        }
        null
    }

    private fun getRandomColor(position: Int): Int {
        return color[position % 7]
    }

    var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green,
        R.color.vivant_dusky_blue
    )

    private fun updateBMIParameter() {
        try {
            var height = 0.0
            var weight = 0.0
            var bmiPosition = -1
            for (i in dataList.indices) {
                if (dataList[i].parameterCode.equals("HEIGHT", true)) {
                    if (!dataList[i].parameterVal.isNullOrEmpty() && dataList.size > 0) {
                        height = dataList[i].parameterVal!!.toDouble()
                    }
                }
                if (dataList[i].parameterCode.equals("WEIGHT", true)) {
                    if (!dataList[i].parameterVal.isNullOrEmpty() && dataList.size > 0) {
                        weight = dataList[i].parameterVal!!.toDouble()
                    }
                }
                if (dataList[i].parameterCode.equals("BMI", true)) {
                    bmiPosition = i
                }
            }
            if (height != 0.0 && weight != 0.0 && bmiPosition != -1) {
                val bmi = calculateBMI(height, weight)
                dataList[bmiPosition].parameterVal = bmi
                if (edtBMI != null) {
                    edtBMI!!.setText(bmi)
                    val observation: String = CalculateParameters.getBMIObservation(bmi, context)
                    if (!observation.isNullOrEmpty()) {
                        edtBMI!!.setTextColor(
                            edtBMI!!.context.resources.getColor(
                                TrackParameterHelper.getObservationColor(observation, "BMI")
                            )
                        )
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateWHRParameter() = try {
        var waist = 0.0
        var hip = 0.0
        var whrPosition = -1
        for (i in dataList.indices) {
            if (dataList[i].parameterCode.equals("WAIST", true)) {
                if (!dataList[i].parameterVal.isNullOrEmpty()) {
                    waist = dataList[i].parameterVal!!.toDouble()
                }
            }
            if (dataList[i].parameterCode.equals("HIP", true)) {
                if (!dataList[i].parameterVal.isNullOrEmpty() && dataList[i].parameterVal!!.toDouble() != 0.0) {
                    hip = dataList[i].parameterVal!!.toDouble()
                }
            }
            if (dataList[i].parameterCode.equals("WHR")) {
                whrPosition = i
            }
        }
        if (waist != 0.0 && hip != 0.0 && whrPosition != -1) {
            val whr = calculateWHR(waist, hip)
            dataList[whrPosition].parameterVal = whr
            if (edtWHR != null) {
                edtWHR!!.setText(whr)
                val observation: String = CalculateParameters.getWHRObservation(whr, 1, context)
                if (!observation.isNullOrEmpty()) {
                    edtWHR!!.setTextColor(
                        edtWHR!!.context.resources.getColor(
                            TrackParameterHelper.getObservationColor(
                                observation,
                                "WHR"
                            )
                        )
                    )
                } else {
                }
            } else {
            }
        } else {
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**
     * Validates Fields
     *
     * @param list
     * @return
     */
    fun validateFields(list: List<ParameterListModel.InputParameterModel>): Boolean {
        var isValid = false
        var isBP = false
        var systolic = 0.0
        var diastolic = 0.0
        var counter = 0
        try {
            for (param in list) {
                if (!TrackParameterHelper.isNullOrEmptyOrZero(param.maxPermissibleValue) &&
                    !param.minPermissibleValue.isNullOrEmpty()
                ) {
                    if (!TrackParameterHelper.isNullOrEmptyOrZero(param.parameterVal)) {
                        try {
                            var minVal = 0.0
                            var maxVal = 0.0
                            var paramVal = 0.0
                            minVal = param.minPermissibleValue!!.toDouble()
                            maxVal = param.maxPermissibleValue!!.toDouble()
                            paramVal = param.parameterVal!!.toDouble()
                            if (param.parameterCode.equals("BP_SYS", true)) {
                                isBP = true
                                systolic = paramVal
                            } else if (param.parameterCode.equals("BP_DIA", true)) {
                                isBP = true
                                diastolic = paramVal
                            }
                            if (paramVal < minVal || paramVal > maxVal) {
                                isValid = false
                                validationMassage = param.description.toString() +
                                        " ${context.resources.getString(R.string.VALUE_SHOULD_BE_BETWEEN)} " + param.minPermissibleValue +
                                        " ${context.resources.getString(R.string.TO)} " + param.maxPermissibleValue
                                break
                            } else {
                                counter++
                                isValid = true
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if (param.parameterType.equals("text", true)) {
                    isValid = true
                    if (!param.parameterTextVal.isNullOrEmpty() && !param.parameterTextVal.toString()
                            .trim().equals("", true)
                    ) {
                        counter++
                    }
                }
            }
            if (isValid && isBP) {
                if (systolic < diastolic) {
                    isValid = false
                    validationMassage =
                        context.resources.getString(R.string.DIASTOLIC_VALUE_SHOULD_BE_LESS_THAN_SYSTOLIC_VALUE)
                } else if (systolic == 0.0) {
                    validationMassage =
                        context.resources.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE)
                    isValid = false
                } else if (diastolic == 0.0) {
                    validationMassage =
                        context.resources.getString(R.string.PLEASE_INSERT_DIASTOLIC_BP_VALUE)
                    isValid = false
                }
            } else if (profileCode.equals("BMI", ignoreCase = true) && counter != 2 && isValid) {
                validationMassage =
                    context.resources.getString(R.string.PLEASE_ENTER_ALL_BMI_PROFILE_FIELDS)
                isValid = false
            } else if (profileCode.equals("WHR", ignoreCase = true) && counter != 2 && isValid) {
                validationMassage =
                    context.resources.getString(R.string.PLEASE_ENTER_ALL_WHR_PROFILE_FIELDS)
                isValid = false
            } else if (counter == 0 && validationMassage.equals("", ignoreCase = true)) {
//                if (!profileCode.equalsIgnoreCase("BMI") && !profileCode.equalsIgnoreCase("WHR") && !profileCode.equalsIgnoreCase("BLOODPRESSURE")) {
                validationMassage = context.resources.getString(R.string.PLEASE_ENTER_VALUE)
                //                }else {
//                    validationMassage = "";
//                }
            } else if (counter == 0) {
                isValid = false
                validationMassage = context.resources.getString(R.string.PLEASE_ENTER_VALUE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isValid = false
        }
        return isValid
    }

    fun getUpdatedParamList(): ArrayList<ParameterListModel.InputParameterModel>? {
        return dataList as ArrayList<ParameterListModel.InputParameterModel>?
    }
}