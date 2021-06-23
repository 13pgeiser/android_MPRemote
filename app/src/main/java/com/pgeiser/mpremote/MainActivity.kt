package com.pgeiser.mpremote

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_PERMISSION = 42 // No comment...
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.i("onCreate!")
        methodRequirePermissions()
        checkLocation()
    }

    private fun checkLocation() {
        // Seems mandatory for the scanning to work...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS must be enabled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Ctrl-o (override members) -> onRequestP...
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    private fun methodRequirePermissions() {
        // Default permissions needed
        var permissionList = arrayListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
        // Starting with Android 6: Coarse location needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        // Starting with Android 10: Fine location needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        val permissions = permissionList.toTypedArray()

        if (EasyPermissions.hasPermissions(this, *permissions)) {
            // Already have permission, do the thing
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                "I need bluetooth permissions...",
                REQUEST_CODE_PERMISSION,
                *permissions
            )
        }
    }
}