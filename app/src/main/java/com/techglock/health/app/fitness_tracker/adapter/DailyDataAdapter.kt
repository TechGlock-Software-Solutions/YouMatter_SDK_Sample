package com.techglock.health.app.fitness_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDailyDataBinding
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.model.entity.FitnessEntity

class DailyDataAdapter(val context: Context, dataType: String, val fitnessHelper: FitnessHelper) :
    RecyclerView.Adapter<DailyDataAdapter.DailyDataViewHolder>() {

    private var type = dataType
    private var monthlyList: MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()


    override fun getItemCount(): Int = monthlyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyDataViewHolder =
        DailyDataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_daily_data, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyDataViewHolder, position: Int) {
        val item = monthlyList[position]
        when (type) {
            Constants.STEPS_COUNT -> {
                //holder.txtData.text = item.stepsCount.toDouble().roundToInt().toString() + " " + context.resources.getString(R.string.STEPS)
                holder.txtData.text =
                    Utilities.formatNumberWithComma(item.stepsCount) + " " + context.resources.getString(
                        R.string.STEPS
                    )
                holder.imgData.setImageResource(R.drawable.img_step)
            }

            Constants.DISTANCE -> {
                //holder.txtData.text = item.distance.toString() + " " + context.resources.getString(R.string.KM)
                holder.txtData.text = Utilities.formatNumberWithComma(
                    fitnessHelper.convertMtrToKmsValueNew(item.distance.toString()).toDouble()
                ) + " " + context.resources.getString(R.string.KM)
                holder.imgData.setImageResource(R.drawable.img_distance)
            }

            Constants.CALORIES -> {
                //holder.txtData.text = item.calories.toDouble().toString() + " " + context.resources.getString(R.string.CALORIES)
                holder.txtData.text =
                    Utilities.formatNumberWithComma(item.calories) + " " + context.resources.getString(
                        R.string.KCAL
                    )
                holder.imgData.setImageResource(R.drawable.img_calories)
            }
        }
        //ImageViewCompat.setImageTintList(holder.imgData,ColorStateList.valueOf(ContextCompat.getColor(context,fitnessHelper.getColorBasedOnType(type))))
        holder.txtDate.text =
            DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, item.recordDate)
    }

    fun updateList(list: MutableList<FitnessEntity.StepGoalHistory>) {
        this.monthlyList.clear()
        this.monthlyList.addAll(list)
        this.notifyDataSetChanged()
    }

    fun updateType(dataType: String) {
        this.type = dataType
        //Utilities.printLogError("dataType---> $dataType")
        this.notifyDataSetChanged()
    }

    class DailyDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDailyDataBinding.bind(view)
        val txtData = binding.txtData
        val txtDate = binding.txtDate
        val imgData = binding.imgData
    }

}