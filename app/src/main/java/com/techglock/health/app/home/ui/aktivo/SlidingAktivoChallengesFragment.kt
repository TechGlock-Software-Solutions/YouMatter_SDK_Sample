package com.techglock.health.app.home.ui.aktivo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.databinding.FragmentSlidingAktivoChallengesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlidingAktivoChallengesFragment(
    private val context: Context,
    /*private val challengesList: MutableList<Challenge>,*/
    val position: Int
) : Fragment(R.layout.fragment_sliding_aktivo_challenges) {

    private lateinit var binding: FragmentSlidingAktivoChallengesBinding

    //private val viewModel: DashboardViewModel by viewModel()
    private val dateHelper = DateHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlidingAktivoChallengesBinding.inflate(inflater, container, false)
        initialise()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun initialise() {
        /*try {
            val challenge = challengesList[position]
            if (!Utilities.isNullOrEmpty(challenge.imageUrl)) {
                //Glide.with(mContext).load(challenge.imageUrl).apply(RequestOptions.circleCropTransform()).into(holder.imgChallenge)
                Picasso.get()
                    .load(challenge.imageUrl)
                    .placeholder(R.drawable.bg_disabled)
                    //.resize(6000, 3000)
                    //.onlyScaleDown()
                    .error(R.drawable.bg_disabled)
                    .into(binding.imgChallenge)
            } else {
                binding.imgChallenge.setImageResource(R.drawable.img_placeholder)
            }

            //binding.btnChallengeType.text = challenge.challengeType
            binding.txtChallengeTitle.text = challenge.title
            binding.txtChallengeDuration.text = "${
                dateHelper.convertDateSourceToDestination(
                    challenge.startDate,
                    dateHelper.SERVER_DATE_YYYYMMDD,
                    dateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
            } - ${
                dateHelper.convertDateSourceToDestination(
                    challenge.endDate,
                    dateHelper.SERVER_DATE_YYYYMMDD,
                    dateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
            }"

            //binding.txtDaysLeft.text = "${Days.daysBetween(LocalDate.now(),LocalDate.parse(challenge.endDate)).days} ${context.resources.getString(R.string.DAYS_LEFT)}"
            binding.txtParticipantsCount.text = challenge.numberOfParticipants.toString()
            if (challenge.enrolled) {
                binding.txtStatus.text = context.resources.getString(R.string.ENROLLED)
                binding.txtStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.state_success
                    )
                )
            } else {
                binding.txtStatus.text = context.resources.getString(R.string.ENROLL_NOW)
                binding.txtStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.textViewColor
                    )
                )
            }

            binding.layoutAktivoChallenges.setOnClickListener {
                CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_CHALLENGES)
                openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                    putString(Constants.CODE, Constants.AKTIVO_CHALLENGES_CODE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

}