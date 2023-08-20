package vac.test.aidlservice

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class TestData(var code: String, var name: String, var price: Float, var qty: Int) : Parcelable
