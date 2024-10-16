package com.techglock.health.app.water_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemCalenderTrackBinding
import com.techglock.health.app.water_tracker.model.CalenderTrackModel
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel


class CalenderTrackIntakeAdapter(
    val viewModel: WaterTrackerViewModel, val context: Context,
    val listener: OnTrackDateListener
) : RecyclerView.Adapter<CalenderTrackIntakeAdapter.MedicineTypeViewHolder>() {
    private var selectedPos = -1
    private var actualFirstPos = 0
    private var achievedPercent = 0.0
    private var isCurrentMonth = false

    private var trackDaysList: MutableList<CalenderTrackModel> = mutableListOf()
    private val weekDayList = mutableListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val extremeLeftPos = mutableListOf(0, 7, 14, 21, 28, 35)
    private val extremeRightPos = mutableListOf(6, 13, 20, 27, 34)

    private val currentDateStr = DateHelper.currentDateAsStringyyyyMMdd
    private val currentDate = DateHelper.getDate(currentDateStr, DateHelper.SERVER_DATE_YYYYMMDD)

    override fun getItemCount(): Int = trackDaysList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_calender_track, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val trackDate = trackDaysList[position]
        try {
            //achievedPercent = ( (trackDate.trackAchieved * 100) / trackDate.trackGoal )
            achievedPercent = trackDate.trackGoalPercentage
            if (!Utilities.isNullOrEmpty(trackDate.trackDate)) {

                holder.layoutContainer.visibility = View.VISIBLE
                holder.txtDate.text = trackDate.trackDate
                holder.waterWaveView.setImageResource(
                    Utilities.getWaterDropImageByValue(
                        achievedPercent
                    )
                )

                /*                if( achievedPercent == 100 ) {
                                    holder.waterWaveView.setColorFilter(ContextCompat.getColor(context,R.color.vivantYellow),android.graphics.PorterDuff.Mode.SRC_ATOP)
                                } else {
                                    holder.waterWaveView.clearColorFilter()
                                }*/

                if (DateHelper.getDate(trackDate.trackServerDate, DateHelper.SERVER_DATE_YYYYMMDD)
                        .after(currentDate)
                ) {
                    holder.waterWaveView.visibility = View.INVISIBLE
                    holder.viewLeft.visibility = View.INVISIBLE
                    holder.viewRight.visibility = View.INVISIBLE
                } else {
                    holder.waterWaveView.visibility = View.VISIBLE
                    holder.itemView.setOnClickListener {
                        notifyItemChanged(selectedPos)
                        selectedPos = position
                        //notifyItemChanged(selectedPos)
                        notifyDataSetChanged()
                    }
                }

                if (selectedPos == -1) {
                    //holder.layoutContainer.background = ContextCompat.getDrawable(context,R.drawable.bg_transparant)
                } else {
                    if (selectedPos == position) {
                        listener.onTrackDateSelection(position, trackDate)
                        holder.imgSelect.visibility = View.VISIBLE
                    } else {
                        holder.imgSelect.visibility = View.INVISIBLE
                    }
                }

                if (trackDate.isGoalAchieved && (DateHelper.getDate(
                        trackDate.trackServerDate,
                        DateHelper.SERVER_DATE_YYYYMMDD
                    ).before(currentDate) || DateHelper.getDate(
                        trackDate.trackServerDate,
                        DateHelper.SERVER_DATE_YYYYMMDD
                    ) == currentDate) &&
                    position !in extremeLeftPos && trackDaysList[position - 1].isGoalAchieved
                ) {
                    holder.viewLeft.visibility = View.VISIBLE
                } else {
                    holder.viewLeft.visibility = View.INVISIBLE
                }

                if (position == actualFirstPos) {
                    holder.viewLeft.visibility = View.INVISIBLE
                }

                if (trackDate.isGoalAchieved && DateHelper.getDate(
                        trackDate.trackServerDate,
                        DateHelper.SERVER_DATE_YYYYMMDD
                    ).before(currentDate) &&
                    position !in extremeRightPos && (position + 1) < trackDaysList.size && trackDaysList[position + 1].isGoalAchieved
                ) {
                    holder.viewRight.visibility = View.VISIBLE
                } else {
                    holder.viewRight.visibility = View.INVISIBLE
                }

                if (position == trackDaysList.lastIndex) {
                    holder.viewRight.visibility = View.INVISIBLE
                }

            } else {
                holder.layoutContainer.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateList(list: MutableList<CalenderTrackModel>, isCurrMonth: Boolean) {
        this.trackDaysList.clear()
        for (i in 0 until weekDayList.size) {
            if (weekDayList[i].equals(list[0].trackWeekDay, ignoreCase = true)) {
                actualFirstPos = i
                break
            } else {
                trackDaysList.add(CalenderTrackModel())
            }
        }
        this.trackDaysList.addAll(list)
        isCurrentMonth = isCurrMonth

        if (isCurrentMonth) {
            for (j in 0 until trackDaysList.size) {
                if (trackDaysList[j].trackServerDate == currentDateStr) {
                    this.selectedPos = j
                    break
                }
            }
        } else {
            this.selectedPos = trackDaysList.size - 1
        }
        this.notifyDataSetChanged()
        viewModel.hideProgressBar()
    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnTrackDateListener {
        fun onTrackDateSelection(position: Int, trackDate: CalenderTrackModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemCalenderTrackBinding.bind(view)
        val waterWaveView = binding.waterWaveView
        val imgSelect = binding.imgSelect
        val txtDate = binding.txtDate
        val layoutContainer = binding.layoutContainer
        val viewLeft = binding.viewLeft
        val viewRight = binding.viewRight
    }

}