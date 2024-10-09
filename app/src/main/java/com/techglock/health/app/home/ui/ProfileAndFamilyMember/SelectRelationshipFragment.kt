package com.techglock.health.app.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSelectRelationshipBinding
import com.techglock.health.app.home.adapter.FamilyRelationshipAdapter
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRelationshipFragment : BaseFragment() {

    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private lateinit var binding: FragmentSelectRelationshipBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var screen = ""
    private var from = ""
    private var relationShipCode = ""
    private var relation = ""
    private var gender = ""
    private var familyRelationshipAdapter: FamilyRelationshipAdapter? = null
    var isExist = false

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM, "")!!
            Utilities.printLogError("from----->$from")
            screen = it.getString(Constants.SCREEN, "")!!
            Utilities.printLogError("screen--->$screen")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    from.equals(Constants.HRA, ignoreCase = true) -> {
                        requireActivity().finish()
                    }

                    screen.equals("FAMILY_MEMBER_ADD", ignoreCase = true) -> {
                        (activity as FamilyProfileActivity).finish()
                    }

                    else -> {
                        requireActivity().finish()
                    }
                }/*                if(from.equals(Constants.HRA, ignoreCase = true)){
                                    requireActivity().finish()
                                } else {
                                    requireActivity().finish()
                                }*/
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectRelationshipBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        // binding.btnNext.setEnabled(false)
        viewModel.getFamilyRelationshipList()
        binding.rvFamilyRelation.layoutManager = GridLayoutManager(context, 3)
        familyRelationshipAdapter = FamilyRelationshipAdapter(this, viewModel, requireContext())
        binding.rvFamilyRelation.adapter = familyRelationshipAdapter

        viewModel.alreadyExistRelatives.observe(viewLifecycleOwner) {
            if (it != null) {
                isExist = it.isNotEmpty()
            }
        }

        viewModel.familyRelationList.observe(viewLifecycleOwner) {
            it?.let {
                if (familyRelationshipAdapter != null) {
                    familyRelationshipAdapter!!.updateRelationList(it)
                }

            }
        }
    }

    private fun setClickable() {

        binding.btnAddRelation.setOnClickListener {
            if (!Utilities.isNullOrEmpty(relationShipCode) && !Utilities.isNullOrEmpty(relation) && !Utilities.isNullOrEmpty(
                    gender
                )
            ) {
                if ((relationShipCode.equals(
                        Constants.FATHER_RELATIONSHIP_CODE, ignoreCase = true
                    ) && isExist) || (relationShipCode.equals(
                        Constants.MOTHER_RELATIONSHIP_CODE, ignoreCase = true
                    ) && isExist) || (relationShipCode.equals(
                        Constants.WIFE_RELATIONSHIP_CODE, ignoreCase = true
                    ) && isExist) || (relationShipCode.equals(
                        Constants.HUSBAND_RELATIONSHIP_CODE, ignoreCase = true
                    ) && isExist)
                ) {
                    val msg = resources.getString(R.string.MSG_ALREADY_ADDED) + " " + relation
                    Utilities.toastMessageShort(context, msg)
                } else {
                    navigateToAddRelativeWithBundle(it, from)
                }
            } else {
                Utilities.toastMessageShort(
                    context, resources.getString(R.string.ERROR_SELECT_RELATION_FIRST)
                )
            }
        }

        /*        binding.btnBackRelation.setOnClickListener {
                    it.findNavController().navigate(R.id.action_selectRelationshipFragment2_to_familyMembersListFragment2)
                }*/

    }

//    private fun isMemberAlreadyExist(): Boolean {
//        var isExist = false
//        viewModel.alreadyExistRelatives.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                isExist = it.isNotEmpty()
//            }
//        })
//        return isExist
//    }

    private fun navigateToAddRelativeWithBundle(view: View, from: String) {
        val bundle = Bundle()
        bundle.putString(Constants.RELATION_CODE, relationShipCode)
        bundle.putString(Constants.RELATION, relation)
        bundle.putString(Constants.GENDER, gender)
        bundle.putString(Constants.FROM, from)
        relationShipCode = ""
        relation = ""
        gender = ""
        view.findNavController()
            .navigate(R.id.action_selectRelationshipFragment2_to_addFamilyMemberFragment2, bundle)
    }

    fun setRelationShipCode(relationShipCode: String) {
        this.relationShipCode = relationShipCode
        viewModel.getUserRelativeSpecific(relationShipCode)
    }

    fun setRelation(relation: String) {
        this.relation = relation
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

}
