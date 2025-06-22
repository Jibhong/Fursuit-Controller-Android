package com.jibhong.FursuitController.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.slider.Slider
import com.jibhong.FursuitController.BluetoothViewModel
import com.jibhong.FursuitController.R
import java.util.UUID
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class QuickPage : Fragment(R.layout.quick_page) {

    private val bluetoothViewModel: BluetoothViewModel by activityViewModels()

    private val LED_SERVICE_UUID = UUID.fromString("56956ce8-6d0d-4919-8016-8ba7ea56b350")

    private val LED_CHARACTERISTIC_UUID = listOf(
        UUID.fromString("5952eae5-f5da-4be1-adad-795a663c3aec"),
        UUID.fromString("f0e2c120-01f6-4fc6-982d-de2c45b1623d")
    )

    private lateinit var brightnessSlider: Slider
    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        brightnessSlider = view.findViewById(R.id.brightnessSlider)

        brightnessSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Optional: do something when user starts sliding
            }

            override fun onStopTrackingTouch(slider: Slider) {
                val value = slider.value
                lifecycleScope.launch {
                    BLESendBrightness(LED_SERVICE_UUID, LED_CHARACTERISTIC_UUID[0], value)
                    BLESendBrightness(LED_SERVICE_UUID, LED_CHARACTERISTIC_UUID[1], value)
                }
            }
        })

    }

    private suspend fun BLESendBrightness(serviceUUID: UUID, characteristicUUID: UUID, value: Float,tryCount: Int = 1) {
        val gatt = bluetoothViewModel.gatt.value ?: return

        val service = gatt.getService(serviceUUID)
        if (service == null) {
            Log.e("BLE", "Service $serviceUUID not found")
            return
        }

        val characteristic = service.getCharacteristic(characteristicUUID)
        if (characteristic == null) {
            Log.e("BLE", "Characteristic $characteristicUUID not found")
            return
        }

        // Format value as JSON string and convert to UTF-8 bytes
        val formattedValue = String.format("%.2f", value)
        val jsonString = "{\"brightness\":$formattedValue}"
        characteristic.value = jsonString.toByteArray(Charsets.UTF_8)

        val success = gatt.writeCharacteristic(characteristic)
        if (!success) {
            Log.e("BLE", "Failed to write characteristic $characteristicUUID (Tried $tryCount times)")
            if(tryCount<3) {
                delay(5)
                BLESendBrightness(serviceUUID, characteristicUUID, value, tryCount + 1)
            }
        } else {
            Log.d("BLE", "Wrote value $jsonString to $characteristicUUID")
        }

        delay(1)
    }

}
