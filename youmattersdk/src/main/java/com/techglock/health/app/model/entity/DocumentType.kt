package com.techglock.health.app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DocumentTypeTable")
data class DocumentType(
    @PrimaryKey
    @SerializedName("Code")
    var code: String = "",
    @SerializedName("Description")
    var description: String = "",
    @SerializedName("ID")
    var id: Int = 0,
    @SerializedName("CreatedBy")
    var createdBy: String? = "",
    @SerializedName("CreatedDate")
    var createdDate: String? = "",
    @SerializedName("ModifiedBy")
    var modifiedBy: String? = "",
    @SerializedName("ModifiedDate")
    var modifiedDate: String? = ""
)