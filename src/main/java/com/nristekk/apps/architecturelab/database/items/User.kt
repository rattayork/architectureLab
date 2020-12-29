package com.nristekk.apps.architecturelab.database.items

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "user_table", primaryKeys = arrayOf("firstName","lastName"))
data class User(

    @ColumnInfo(name = "firstName") val firstName: String,
    @ColumnInfo(name = "lastName") val lastName: String,
    @ColumnInfo(name = "age") var age: Int,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "tel") @Nullable var tel:String? = null

)
