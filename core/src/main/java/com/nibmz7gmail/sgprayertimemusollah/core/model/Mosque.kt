package com.nibmz7gmail.sgprayertimemusollah.core.model

import android.os.Parcel
import android.os.Parcelable

data class Mosque(
    val id: Int,
    val name: String,
    val address: String,
    val fbPage: String,
    val wcFriendly: Int,
    val latitude: Double,
    val longitude: Double,
    val tel: String,
    @Transient var distance: Float = 0f
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(fbPage)
        parcel.writeInt(wcFriendly)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(tel)
        parcel.writeFloat(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mosque> {
        override fun createFromParcel(parcel: Parcel): Mosque {
            return Mosque(parcel)
        }

        override fun newArray(size: Int): Array<Mosque?> {
            return arrayOfNulls(size)
        }
    }

}
