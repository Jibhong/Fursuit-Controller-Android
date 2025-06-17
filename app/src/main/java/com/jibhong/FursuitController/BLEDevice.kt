package com.jibhong.FursuitController

import android.bluetooth.BluetoothDevice

data class ScannedDevice(
    val device: BluetoothDevice
) {
    val name: String get() = device.name ?: "Unknown"
    val address: String get() = device.address
}
