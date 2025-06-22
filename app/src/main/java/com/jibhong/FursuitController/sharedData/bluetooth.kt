package com.jibhong.FursuitController

import android.bluetooth.BluetoothGatt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothViewModel : ViewModel() {
    private val _gatt = MutableLiveData<BluetoothGatt?>()
    val gatt: LiveData<BluetoothGatt?> = _gatt

    fun setGatt(gatt: BluetoothGatt?) {
        _gatt.value = gatt
    }

    fun clearGatt() {
        _gatt.value = null
    }
}
