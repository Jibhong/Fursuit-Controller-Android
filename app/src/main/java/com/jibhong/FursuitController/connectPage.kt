package com.jibhong.FursuitController

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.TextView

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile

class ConnectPage : Fragment(R.layout.connect_page) {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var scanner: BluetoothLeScanner? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scanPeriod: Long = 10000

    private val deviceList = mutableListOf<ScannedDevice>()
    private lateinit var adapter: DeviceAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var bluetoothGatt: BluetoothGatt? = null

    private lateinit var connectedDeviceText: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.deviceRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        adapter = DeviceAdapter(deviceList) { scannedDevice ->
            if(bluetoothGatt!=null){
                try {
                    Toast.makeText(
                        requireContext(),
                        "Disconnect from ${bluetoothGatt?.device?.name} first",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w("BLE", "Failed Connecting to device: ${scannedDevice.name}\nDisconnect from ${bluetoothGatt?.device?.name} first")
                } catch (e: SecurityException) {
                    Log.e("BLE", "SecurityException: ${e.message}")
                }
            }else {
                Toast.makeText(
                    requireContext(),
                    "Connecting to: ${scannedDevice.name}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("BLE", "Connecting to device: ${scannedDevice.name}")
                connectToDevice(scannedDevice.device)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            startScan()
        }

        requestPermissions() // Request permissions immediately on page open

        val disconnectButton = view.findViewById<View>(R.id.disconnectButton)
        disconnectButton.setOnClickListener {
            // Handle disconnect button click
            disconnectFromDevice()
        }

        connectedDeviceText = view.findViewById(R.id.connectedDeviceText)

    }

    private fun startScan() {
        deviceList.clear()
        adapter.notifyDataSetChanged()

        bluetoothAdapter = requireActivity().getSystemService(BluetoothManager::class.java).adapter

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(requireContext(), "Bluetooth is OFF", Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
            return
        }

        scanner = bluetoothAdapter.bluetoothLeScanner

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val name = device.name ?: "Unknown"
                val address = device.address

                // Avoid duplicates
                if (deviceList.none { it.address == address } && name != "Unknown") {
                    deviceList.add(ScannedDevice(device))
                    adapter.notifyItemInserted(deviceList.size - 1)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE", "Scan failed with error: $errorCode")
            }
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val filters = listOf(ScanFilter.Builder().build())

        scanner?.startScan(filters, settings, scanCallback)

        handler.postDelayed({
            scanner?.stopScan(scanCallback)
            swipeRefreshLayout.isRefreshing = false
        }, scanPeriod)

        swipeRefreshLayout.isRefreshing = true
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        ActivityCompat.requestPermissions(requireActivity(), permissions.toTypedArray(), 1)
    }

    override fun onResume() {
        super.onResume()
        if (hasPermissions()) {
            startScan()
        }
    }

    private fun hasPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        val context = requireContext()

        // Check Bluetooth permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, handle accordingly
                Log.e("BLE", "BLUETOOTH_CONNECT permission not granted")
                return
            }
        }

        try {
            bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)

                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            Log.d("BLE", "Connected to ${gatt.device.address}")
                            bluetoothGatt = gatt // Store the reference if not already stored
                            gatt.discoverServices()
                            connectedDeviceText.text = "Connected to ${gatt.device.name}"
                        }

                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d("BLE", "Device fully disconnected: ${gatt.device.address}")
                            // **Now** it’s safe to close()
                            gatt.close()
                            // update UI on main thread
                            Handler(Looper.getMainLooper()).post {
                                connectedDeviceText.text = "Disconnected"
                            }
                            // clear our reference so we know there’s no active connection
                            if (bluetoothGatt == gatt) {
                                bluetoothGatt = null
                            }
                        }
                    }
                }


                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d("BL    E", "Services discovered: ${gatt.services}")
                    } else {
                        Log.e("BLE", "Service discovery failed: $status")
                    }
                }
            })
        } catch (e: SecurityException) {
            Log.e("BLE", "SecurityException: ${e.message}")
        }
    }

    private fun disconnectFromDevice() {
        val context = requireContext()

        // Android 12+ requires BLUETOOTH_CONNECT permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE", "disconnectFromDevice: Missing BLUETOOTH_CONNECT permission")
            return
        }

        if (bluetoothGatt == null) {
            Log.w("BLE", "disconnectFromDevice: No device is connected")
            return
        }
        try {
            Log.d("BLE", "Disconnecting from ${bluetoothGatt?.device?.name ?: "Unknown"}")

            // Only disconnect here. Close in the callback.
            bluetoothGatt?.disconnect()
        } catch (e: SecurityException) {
            Log.e("BLE", "SecurityException: ${e.message}")
        }
    }

}

class Page1Fragment : Fragment(R.layout.page1)

class Page2Fragment : Fragment(R.layout.page2)