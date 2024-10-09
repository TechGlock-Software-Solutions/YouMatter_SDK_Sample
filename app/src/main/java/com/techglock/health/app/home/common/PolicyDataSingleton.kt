package com.techglock.health.app.home.common

import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import com.techglock.health.app.model.sudLifePolicy.SudFundDetailsModel.FundDetail
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel.KYP
import com.techglock.health.app.model.sudLifePolicy.SudPolicyDetailsByPolicyNumberModel.PolicyDetails


class PolicyDataSingleton private constructor() {

    var productsList: MutableList<PolicyProductsModel.PolicyProducts> = mutableListOf()
    var policyList: MutableList<KYP> = mutableListOf()
    var kypDetails = KYP()
    var policyDetails = PolicyDetails()
    var fundDetails: MutableList<FundDetail> = mutableListOf()

    fun clearData() {
        instance = null
        productsList.clear()
        policyList.clear()
        kypDetails = KYP()
        policyDetails = PolicyDetails()
        fundDetails.clear()
        Utilities.printLogError("Cleared Policy Data")
    }

    fun clearPolicyDetails() {
        policyDetails = PolicyDetails()
        Utilities.printLogError("Cleared Policy Details")
    }

    fun clearFundDetails() {
        fundDetails.clear()
        Utilities.printLogError("Cleared Fund Details")
    }

    companion object {
        private var instance: PolicyDataSingleton? = null
        fun getInstance(): PolicyDataSingleton? {
            if (instance == null) {
                instance = PolicyDataSingleton()
            }
            return instance
        }
    }

}