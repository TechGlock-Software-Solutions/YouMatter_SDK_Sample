package com.techglock.health.app.fitness_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDailyDataBinding
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.model.fitness.YearlyMonthDataModel

class YearlyMonthDataAdapter(
    val context: Context,
    dataType: String,
    val fitnessHelper: FitnessHelper
) : RecyclerView.Adapter<YearlyMonthDataAdapter.YearlyMonthDataViewHolder>() {

    private var type = dataType
    private var yearlyList: MutableList<YearlyMonthDataModel> = mutableListOf()


    override fun getItemCount(): Int = yearlyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearlyMonthDataViewHolder =
        YearlyMonthDataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_daily_data, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: YearlyMonthDataViewHolder, position: Int) {
        val item = yearlyList[position]
        when (type) {
            Constants.STEPS_COUNT -> {
                //holder.txtData.text = item.stepsCount.toDouble().roundToInt().toString() + " " + context.resources.getString(R.string.STEPS)
                holder.txtData.text =
                    Utilities.formatNumberWithComma(item.totalSteps) + " " + context.resources.getString(
                        R.string.STEPS
                    )
                holder.imgData.setImageResource(R.drawable.img_step)
            }

            Constants.DISTANCE -> {
                //holder.txtData.text = item.distance.toString() + " " + context.resources.getString(R.string.KM)
                holder.txtData.text = Utilities.formatNumberWithComma(
                    fitnessHelper.convertMtrToKmsValueNew(item.totalDistance.toString()).toDouble()
                ) + " " + context.resources.getString(R.string.KM)
                holder.imgData.setImageResource(R.drawable.img_distance)
            }

            Constants.CALORIES -> {
                //holder.txtData.text = item.calories.toDouble().toString() + " " + context.resources.getString(R.string.CALORIES)
                holder.txtData.text =
                    Utilities.formatNumberWithComma(item.totalCalories) + " " + context.resources.getString(
                        R.string.KCAL
                    )
                holder.imgData.setImageResource(R.drawable.img_calories)
            }
        }
        //ImageViewCompat.setImageTintList(holder.imgData,ColorStateList.valueOf(ContextCompat.getColor(context,fitnessHelper.getColorBasedOnType(type))))
        holder.txtDate.text = "${item.monthYear} ${item.year}"
    }

    fun updateList(list: MutableList<YearlyMonthDataModel>) {
        this.yearlyList.clear()
        this.yearlyList.addAll(list)
        this.notifyDataSetChanged()
    }

    fun updateType(dataType: String) {
        this.type = dataType
        //Utilities.printLogError("dataType---> $dataType")
        this.notifyDataSetChanged()
    }

    class YearlyMonthDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDailyDataBinding.bind(view)
        val txtData = binding.txtData
        val txtDate = binding.txtDate
        val imgData = binding.imgData
    }

}