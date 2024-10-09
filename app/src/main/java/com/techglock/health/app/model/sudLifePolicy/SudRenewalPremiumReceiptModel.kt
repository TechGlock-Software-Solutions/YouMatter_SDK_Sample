package com.techglock.health.app.model.sudLifePolicy

import com.techglock.health.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudRenewalPremiumReceiptModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class SudRenewalPremiumReceiptResponse(
        @SerializedName("Result")
        @Expose
        val records: List<RenewalPremiumReceipt> = listOf(),
    )

    data class RenewalPremiumReceipt(
        @SerializedName("Customer_Full_Name")
        val customerFullName: String? = "",
        @SerializedName("Address")
        val address: String? = "",
        @SerializedName("Pincode")
        val pincode: String? = "",
        @SerializedName("Contact_Number")
        val contactNumber: String? = "",
        @SerializedName("Receipt_Number")
        val receiptNumber: String? = "",
        @SerializedName("Date_of_Receipt")
        val dateOfReceipt: String? = "",
        @SerializedName("Premium_Adjustment_Date")
        val premium_Adjustment_Date: String? = "",
        @SerializedName("Premium_Due_Date")
        val premium_Due_Date: String? = "",
        @SerializedName("Base_Premium")
        val basePremium: Double? = 0.0,
        @SerializedName("Base_CGST")
        val baseCGST: Double? = 0.0,
        @SerializedName("Base_SGST")
        val baseSGST: Double? = 0.0,
        @SerializedName("Base_IGST")
        val baseIGST: Double? = 0.0,
        @SerializedName("Rider_Premium")
        val riderPremium: Double? = 0.0,
        @SerializedName("Rider_CGST")
        val riderCGST: Double? = 0.0,
        @SerializedName("Rider_SGST")
        val riderSGST: Double? = 0.0,
        @SerializedName("Rider_IGST")
        val RiderIGST: Double? = 0.0,
        @SerializedName("Policy_Status")
        val policyStatus: String? = "",
        @SerializedName("Payment_Frequency")
        val paymentFrequency: String? = "",
        @SerializedName("Policy_Number")
        val policyNumber: String? = "",
        @SerializedName("Plan_Name")
        val planName: String? = "",
        @SerializedName("Next_Premium_due_date")
        val nextPremiumDueDate: String? = "",
        @SerializedName("Balance_in_deposit")
        val balanceInDeposit: Double? = 0.0,
        @SerializedName("Product_and_Rider_Name")
        val productAndRiderName: String? = "",
        @SerializedName("Premium amount")
        val premiumAmount: Double? = 0.0
    )

}