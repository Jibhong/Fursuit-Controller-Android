package com.jibhong.fursuitController.bluetooth

import android.bluetooth.BluetoothDevice

data class ScannedDevice(
    val device: BluetoothDevice
) {
    val name: String get() = device.name ?: "Unknown"
    val address: String get() = device.address
}
