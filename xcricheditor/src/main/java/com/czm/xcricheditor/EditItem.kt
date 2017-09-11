package com.czm.xcricheditor

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


data class EditItem(var type: Int, var content: String?, var uri: Uri?) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.type)
        dest.writeString(this.content)
        dest.writeParcelable(this.uri, flags)
    }

    constructor(source: Parcel) :
            this(source.readInt(), source.readString(), source.readParcelable(Uri::class.java!!.getClassLoader()))

    companion object {

        @JvmField final val CREATOR: Parcelable.Creator<EditItem> = object : Parcelable.Creator<EditItem> {
            override fun createFromParcel(source: Parcel): EditItem {
                return EditItem(source)
            }

            override fun newArray(size: Int): Array<EditItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}
