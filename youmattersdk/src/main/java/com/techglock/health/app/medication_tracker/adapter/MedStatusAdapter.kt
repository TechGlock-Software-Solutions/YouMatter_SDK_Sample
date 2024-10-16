package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.databinding.ItemStatusBinding
import com.techglock.health.app.medication_tracker.model.MedStatusModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedStatusAdapter(
    val context: Context, private var pos: Int, private var statusList: List<MedStatusModel>,
    private var selectedPos: Int, var time: String, private var selectedDate: String,
    private var listener: OnStatusChangeListener, private var futureListener: OnFutureListener
) :
    RecyclerView.Adapter<MedStatusAdapter.MedTimeStatusViewHolder>() {

    override fun getItemCount(): Int = statusList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeStatusViewHolder =
        MedTimeStatusViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false)
        )

    override fun onBindViewHolder(holder: MedTimeStatusViewHolder, position: Int) {
        try {
            val statusDetails = statusList[position]
            holder.txtStatus.text = statusDetails.statusTitle
            holder.imgStatus.setImageResource(statusDetails.imageId)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val strDate = sdf.parse(selectedDate)
            val currentDate = sdf.parse(DateHelper.currentDateAsStringyyyyMMdd)

            //val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val currentTime = dateFormat.parse(dateFormat.format(Date()))
            val medTime = dateFormat.parse(time)
            val differenceInMinutes = (currentTime!!.time - medTime!!.time) / (60 * 1000)

            if (strDate == currentDate) {
                // To check whether given time is earlier than current time
                //Also it will be Enabled to Update 60 Minitues before Current Time
                //if( medTime.before(currentTime) ) {
                if (medTime.before(currentTime) || differenceInMinutes >= -60) {
                    if (selectedPos == position) {
                        ImageViewCompat.setImageTintList(
                            holder.imgStatus,
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                        )
                        holder.layoutStatus.background =
                            ResourcesCompat.getDrawable(context.resources, statusDetails.bgId, null)
                        holder.txtStatus.setTextColor(context.resources.getColor(R.color.white))
                    } else {
                        ImageViewCompat.setImageTintList(
                            holder.imgStatus,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context,
                                    R.color.dark_gray
                                )
                            )
                        )
                        holder.layoutStatus.background = ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.btn_oval_disabled,
                            null
                        )
                        holder.txtStatus.setTextColor(context.resources.getColor(R.color.dark_gray))
                    }
                } else {
                    // Disable Medicine times from Current time
                    futureListener.onFutureChange(pos, true)
                    holder.layoutStatus.isEnabled = false
                    holder.layoutStatus.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.border_oval_disabled,
                        null
                    )
                    ImageViewCompat.setImageTintList(
                        holder.imgStatus,
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
                    )
                    holder.txtStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.light_gray
                        )
                    )
                }
            } else if (strDate!!.before(currentDate)) {
                if (selectedPos == position) {
                    ImageViewCompat.setImageTintList(
                        holder.imgStatus,
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                    )
                    holder.layoutStatus.background =
                        ResourcesCompat.getDrawable(context.resources, statusDetails.bgId, null)
                    holder.txtStatus.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    ImageViewCompat.setImageTintList(
                        holder.imgStatus,
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray))
                    )
                    holder.layoutStatus.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.btn_oval_disabled,
                        null
                    )
                    holder.txtStatus.setTextColor(context.resources.getColor(R.color.dark_gray))
                }
            } else {
                // Disable Medicine times for future Dates
                futureListener.onFutureChange(pos, true)
                holder.layoutStatus.isEnabled = false
                holder.layoutStatus.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.border_oval_disabled,
                    null
                )
                ImageViewCompat.setImageTintList(
                    holder.imgStatus,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
                )
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
            }

            holder.itemView.setOnClickListener {
                listener.onStatusChange(pos, statusDetails.statusCode)
                notifyItemChanged(selectedPos)
                selectedPos = position
                notifyItemChanged(selectedPos)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedPosStatus(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnStatusChangeListener {
        fun onStatusChange(position: Int, status: String)
    }

    interface OnFutureListener {
        fun onFutureChange(position: Int, isFuture: Boolean)
    }

    inner class MedTimeStatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemStatusBinding.bind(view)
        val txtStatus = binding.txtStatus
        val imgStatus = binding.imgStatus
        val layoutStatus = binding.layoutStatus
    }

}