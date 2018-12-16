package com.nibmz7gmail.sgprayertimemusollah.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.*

fun Context.isConnectedToInternet(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo

    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Context.showToast(message: String, length: Int) {
    Toast.makeText(this, message, length)
        .show()
}

fun <X, Y> LiveData<X>.switchMap(body: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, body)
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this, provider).get(VM::class.java)

/**
 * Like [Fragment.viewModelProvider] for Fragments that want a [ViewModel] scoped to the Activity.
 */
inline fun <reified VM : ViewModel> Fragment.activityViewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(requireActivity(), provider).get(VM::class.java)

inline fun <reified T> String.toListItems(): List<T>? {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val dataAdapter = moshi.adapter<List<T>>(type)

    return dataAdapter.fromJson(this)
}

inline fun <reified T> List<T>.toJsonString(): String {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val dataAdapter = moshi.adapter<List<T>>(type)

    return dataAdapter.toJson(this)
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction?) {
    beginTransaction().func()?.commit()
}
