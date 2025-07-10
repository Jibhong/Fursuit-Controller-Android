package com.jibhong.fursuitController.fragment

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.jibhong.fursuitController.R
import java.util.*

import androidx.fragment.app.activityViewModels
import com.jibhong.fursuitController.sharedData.BluetoothViewModel


class FanPage : Fragment(R.layout.fan_page) {

    private val bluetoothViewModel: BluetoothViewModel by activityViewModels()

    private val FAN_SERVICE_UUID = UUID.fromString("87a728cc-b26b-4f5e-aab7-25b6c82a1434")

    private val characteristicUUIDs = listOf(
        UUID.fromString("6f5a70c8-b114-4c50-ab97-f33a6253d482"),
        UUID.fromString("aadd3fa8-d8be-4d8f-bd36-b2d9df0ca8a8"),
        UUID.fromString("ba96a026-921f-48b9-a6e1-0e496f14264f"),
        UUID.fromString("292842c4-795c-4e2c-b1b4-47a733d41a94"),
        UUID.fromString("d4eebdd0-e824-4754-81f8-f04b1820b57c")
    )

    private lateinit var sliders: List<Slider>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sliders = listOf(
            view.findViewById(R.id.fanSlider1),
            view.findViewById(R.id.fanSlider2),
            view.findViewById(R.id.fanSlider3),
            view.findViewById(R.id.fanSlider4),
            view.findViewById(R.id.fanSlider5)
        )

        sliders.forEachIndexed { index, slider ->
            slider.addOnChangeListener { _, value, fromUser ->
                val gatt = bluetoothViewModel.gatt.value
                if (gatt != null) {
                    sendValueToCharacteristic(gatt, index, value)
                }
            }
        }
    }

    private fun sendValueToCharacteristic(gatt: BluetoothGatt, index: Int, value: Float) {
        Log.d("BLE", "sending value: $value to fan index: $index")
        val characteristicUUID = characteristicUUIDs.getOrNull(index) ?: return

        val characteristic = gatt
            .getService(FAN_SERVICE_UUID)
            ?.getCharacteristic(characteristicUUID) ?: return

        val byteValue = byteArrayOf(value.toInt().toByte()) // Adjust encoding as needed
        characteristic.value = byteValue
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        gatt.writeCharacteristic(characteristic)
    }

}
