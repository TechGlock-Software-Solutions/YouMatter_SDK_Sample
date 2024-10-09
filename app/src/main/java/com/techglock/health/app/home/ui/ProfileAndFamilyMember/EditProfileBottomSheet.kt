package com.techglock.health.app.home.ui.ProfileAndFamilyMember

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techglock.health.app.R
import com.techglock.health.app.databinding.BottomSheetEditProfileBinding
import com.techglock.health.app.home.common.DataHandler.ProfileImgOption
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileBottomSheet(var listener: OnOptionClickListener, var hasProfileImage: Boolean) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditProfileBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        if (hasProfileImage) {
            binding.layoutViewPic.visibility = View.VISIBLE
            binding.layoutRemovePhoto.visibility = View.VISIBLE
        } else {
            binding.layoutViewPic.visibility = View.GONE
            binding.layoutRemovePhoto.visibility = View.GONE
        }

    }

    private fun setClickable() {

        binding.layoutViewPic.setOnClickListener {
            dismiss()
            listener.onOptionClick(0, ProfileImgOption.View)
        }

        binding.layoutOpenGallery.setOnClickListener {
            dismiss()
            listener.onOptionClick(1, ProfileImgOption.Gallery)
        }

        binding.layoutTakePhoto.setOnClickListener {
            dismiss()
            listener.onOptionClick(2, ProfileImgOption.Photo)
        }

        binding.layoutRemovePhoto.setOnClickListener {
            dismiss()
            listener.onOptionClick(3, ProfileImgOption.Remove)
        }

    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState);
        return BottomSheetDialog(requireContext(), theme)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }


    interface OnOptionClickListener {
        fun onOptionClick(position: Int, code: String)
    }

}