package co.aospa.sense.camera

import android.content.Context
import android.hardware.Camera
import android.os.SystemProperties
import android.util.Log
import java.util.ArrayList
import java.util.Comparator
import kotlin.math.abs

object CameraUtil {

    fun getBestPreviewSize(parameters: Camera.Parameters?, width: Int, height: Int): Camera.Size {
        val supportedPreviewSizes = parameters!!.supportedPreviewSizes
        val previewSizes = ArrayList<Camera.Size>()
        for (size in supportedPreviewSizes) {
            if (size.width > size.height) {
                previewSizes.add(size)
            }
        }
        previewSizes.sortWith(Comparator.comparingInt { size: Camera.Size ->
            abs(
                size.width * size.height - width * height
            )
        })
        return previewSizes[0]
    }

    fun getCameraId(context: Context?): Int {
        if (context == null) return -1

        try {
            // Camera.open() takes an API1 index, not a Camera2 device ID. These
            // differ when logical-camera filtering hides physical cameras.
            val cameraCount = Camera.getNumberOfCameras()
            val override = SystemProperties.get("ro.face.sense_service.camera_id")
            if (override.isNotEmpty()) {
                val cameraIndex = override.toIntOrNull()
                if (cameraIndex != null && cameraIndex in 0 until cameraCount) {
                    return cameraIndex
                }
                Log.w("CameraUtil", "Ignoring invalid API1 camera index: $override")
            }

            val info = Camera.CameraInfo()
            for (cameraIndex in 0 until cameraCount) {
                Camera.getCameraInfo(cameraIndex, info)
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return cameraIndex
                }
            }
        } catch (e: RuntimeException) {
            Log.e("CameraUtil", "Unable to enumerate API1 cameras", e)
        }

        return -1
    }
}
