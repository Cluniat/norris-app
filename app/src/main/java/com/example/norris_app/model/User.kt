package com.example.norris_app.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private var _id: Long = 0L,

    @ColumnInfo(name = "lastname")
    private var _lastname: String? = "",

    @ColumnInfo(name = "firstname")
    private var _firstname: String? = "",

    @ColumnInfo(name = "birthday_date")
    private var _birthdayDate: String? = "",

    @ColumnInfo(name = "gender")
    private var _gender: Int = 0,

    @ColumnInfo(name = "email")
    private var _email: String? = "",

    @ColumnInfo(name = "password")
    private var _password: String? = "",

    @ColumnInfo(name = "city")
    private var _city: String? = "",

    @ColumnInfo(name = "address")
    private var _address: String? = "",

    @ColumnInfo(name = "country")
    private var _country: String? = "",

    @ColumnInfo(name = "favourites")
    private var _favourites: String? = ""
)

    : Parcelable, BaseObservable() {

    var id: Long
        @Bindable get() = _id
        set(value) {
            _id = value
            notifyPropertyChanged(BR.id)
        }
    var lastname: String?
        @Bindable get() = _lastname
        set(value) {
            _lastname = value
            notifyPropertyChanged(BR.lastname)
        }
    var firstname: String?
        @Bindable get() = _firstname
        set(value) {
            _firstname = value
            notifyPropertyChanged(BR.firstname)
        }
    var birthdayDate: String?
        @Bindable get() = _birthdayDate
        set(value) {
            _birthdayDate = value
            notifyPropertyChanged(BR.birthdayDate)
        }
    var gender: Int
        @Bindable get() = _gender
        set(value) {
            _gender = value
            notifyPropertyChanged(BR.gender)
        }
    var email: String?
        @Bindable get() = _email
        set(value) {
            _email = value
            notifyPropertyChanged(BR.email)
        }
    var password: String?
        @Bindable get() = _password
        set(value) {
            _password = value
            notifyPropertyChanged(BR.password)
        }
    var city: String?
        @Bindable get() = _city
        set(value) {
            _city = value
            notifyPropertyChanged(BR.city)
        }
    var address: String?
        @Bindable get() = _address
        set(value) {
            _address = value
            notifyPropertyChanged(BR.address)
        }
    var country: String?
        @Bindable get() = _country
        set(value) {
            _country = value
            notifyPropertyChanged(BR.country)
        }
    var favourites: String?
        @Bindable get() = _favourites
        set(value) {
            _favourites = value
            notifyPropertyChanged(BR.favourites)
        }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeLong(id)
        parcel?.writeString(lastname)
        parcel?.writeString(firstname)
        parcel?.writeString(birthdayDate)
        parcel?.writeInt(gender)
        parcel?.writeString(email)
        parcel?.writeString(password)
        parcel?.writeString(city)
        parcel?.writeString(address)
        parcel?.writeString(country)
        parcel?.writeString(favourites)
    }

    override fun describeContents(): Int {
        return 0
    }
}

