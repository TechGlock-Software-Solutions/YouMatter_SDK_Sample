package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemLiveSessionBinding
import com.techglock.health.app.model.home.LiveSessionModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveSessionAdapter(
    private val mContext: Context,
    private val listener: OnLiveSessionListener
) : RecyclerView.Adapter<LiveSessionAdapter.LiveSessionViewHolder>() {

    private var sessionsList: MutableList<LiveSessionModel> = mutableListOf()
    private val dateHelper = DateHelper
    private val currentDate = dateHelper.currentDateAsStringyyyyMMdd

    //private val currentDate = "2023-07-08"
    private var differenceInMinutes = 0
    private lateinit var slotTime: Date
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    private var currentTime = dateFormat.parse(dateFormat.format(Date()))

    override fun getItemCount(): Int = sessionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSessionViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_live_session, parent, false)
        return LiveSessionViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LiveSessionViewHolder, position: Int) {
        val session = sessionsList[position]
        try {
            holder.txtLiveSessionDate.text = dateHelper.convertDateSourceToDestination(
                session.date,
                dateHelper.DATE_FORMAT_UTC,
                dateHelper.SHORT_DATE_FORMAT
            ) + ", " + dateHelper.convertDateSourceToDestination(
                session.time,
                dateHelper.TIME_24_FORMAT,
                dateHelper.TIME_12_FORMAT
            )
            holder.txtLiveSessionName.text = session.name
            //holder.imgLiveSession.setImgUrl(session.bannerImage)
            when (session.name.uppercase()) {
                "LIVE YOGA SESSION" -> holder.imgLiveSession.setImageResource(R.drawable.img_yoga_session)
                "LIVE WORKOUT SESSION" -> holder.imgLiveSession.setImageResource(R.drawable.img_exercise_session)
                "LIVE MEDITATION SESSION" -> holder.imgLiveSession.setImageResource(R.drawable.img_meditation_session)
            }
            //currentTime = dateFormat.parse("07:05:00")!!
            slotTime = dateFormat.parse(session.time)!!
            differenceInMinutes = ((currentTime!!.time - slotTime.time) / (60 * 1000)).toInt()
            if (currentDate == session.date.split("T")[0] && differenceInMinutes >= -5) {
                holder.btnJoin.visibility = View.VISIBLE
            } else {
                holder.btnJoin.visibility = View.GONE
                holder.layoutLiveSession.setOnClickListener {
                    Utilities.toastMessageShort(
                        mContext,
                        "${mContext.resources.getString(R.string.PLEASE_JOIN_THE_SESSION_ON)} ${
                            dateHelper.convertDateSourceToDestination(
                                session.date,
                                dateHelper.DATE_FORMAT_UTC,
                                dateHelper.SHORT_DATE_FORMAT
                            ) + ", " + dateHelper.convertDateSourceToDestination(
                                session.time,
                                dateHelper.TIME_24_FORMAT,
                                dateHelper.TIME_12_FORMAT
                            )
                        }"
                    )
                }
            }

            if (currentDate == session.date.split("T")[0] && currentTime!!.time >= slotTime.time) {
                holder.imgDate.setImageResource(R.drawable.img_live_streaming)
                holder.txtLiveSessionDate.text = mContext.resources.getString(R.string.ON_GOING)
                holder.txtLiveSessionDate.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.color_meditation
                    )
                )
            } else {
                holder.imgDate.setImageResource(R.drawable.img_cal_date)
                holder.txtLiveSessionDate.text = dateHelper.convertDateSourceToDestination(
                    session.date,
                    dateHelper.DATE_FORMAT_UTC,
                    dateHelper.SHORT_DATE_FORMAT
                ) + ", " + dateHelper.convertDateSourceToDestination(
                    session.time,
                    dateHelper.TIME_24_FORMAT,
                    dateHelper.TIME_12_FORMAT
                )
                holder.txtLiveSessionDate.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.textViewColor
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.btnJoin.setOnClickListener {
            listener.onLiveSessionClick(session)
        }
    }

    fun updateList(list: List<LiveSessionModel>) {
        this.sessionsList.clear()
        this.sessionsList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnLiveSessionListener {
        fun onLiveSessionClick(liveSessionModel: LiveSessionModel)
    }

    inner class LiveSessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLiveSessionBinding.bind(view)
        var layoutLiveSession = binding.layoutLiveSession
        var imgDate = binding.imgDate
        var txtLiveSessionDate = binding.txtLiveSessionDate
        var txtLiveSessionName = binding.txtLiveSessionName
        var imgLiveSession = binding.imgLiveSession
        var btnJoin = binding.btnJoin
    }

}