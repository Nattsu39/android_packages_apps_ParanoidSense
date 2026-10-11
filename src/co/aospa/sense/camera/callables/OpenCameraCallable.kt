package co.aospa.sense.camera.callables

import android.hardware.Camera
import android.util.Log
import co.aospa.sense.camera.listeners.CameraEventListener
import co.aospa.sense.camera.listeners.CameraListener
import co.aospa.sense.util.Util
import java.lang.Exception

class OpenCameraCallable(
    private val mCameraId: Int,
    private val mErrorListener: CameraEventListener?,
    listener: CameraListener?
) : CameraCallable(listener) {

    override fun call(): CallableReturn {
        if (Util.IS_DEBUG_LOGGING) Log.d(tag, "device: connect device async task: start")
        return if (cameraData.mCamera != null && cameraData.mCameraId == mCameraId) {
            if (Util.IS_DEBUG_LOGGING) Log.d(tag, "Camera is already opened")
            setErrorCallback(cameraData.mCamera)
            CallableReturn(null)
        } else if (cameraData.mCamera != null) {
            CallableReturn(Exception("Other camera is all ready opened"))
        } else {
            try {
                openCamera()
                if (Util.IS_DEBUG_LOGGING) Log.d(tag, "device: connect device async task:open camera complete")
                CallableReturn(null)
            } catch (e: Exception) {
                CallableReturn(e)
            }
        }
    }

    override val tag: String?
        get() = OpenCameraCallable::class.java.simpleName

    private fun openCamera() {
        val cameraData = cameraData
        if (Util.IS_DEBUG_LOGGING) Log.d(tag, "open camera $mCameraId")
        // call() already handles an existing camera and reports open failures.
        // Do not swallow the exception and report a successful open with null data.
        cameraData.mCamera = openCamera(mCameraId)
        cameraData.mCameraId = mCameraId
        if (Util.IS_DEBUG_LOGGING) Log.d(tag, "open camera success, id: " + cameraData.mCameraId)
        setErrorCallback(cameraData.mCamera)
    }

    private fun setErrorCallback(camera: Camera?) {
        if (Util.IS_DEBUG_LOGGING) Log.d(tag, "set error callback")
        camera!!.setErrorCallback { data: Int, _: Camera? ->
            mErrorListener?.onEventCallback(
                data,
                null
            )
        }
    }

    companion object {
        private fun openCamera(id: Int): Camera {
            return Camera.open(id)
        }
    }
}
