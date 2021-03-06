package com.ibeilly.kyc.camerax

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ibeilly.kyc.camerax.databinding.ActivityCameraXactivityBinding
import com.ibeilly.kyc.kycFail
import com.ibeilly.kyc.kycSuccess
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraXActivity : BasePreviewActivity() {

    protected lateinit var previewView: PreviewView
    protected lateinit var switchCamera: View
    protected lateinit var bgMask: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindView()

        Log.d(TAG, "onCreate $requestedOrientation")
        if (null == savedInstanceState) {
            initWithCameraXOption()
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initWithCameraXOption() {
        val cameraxModel = intent.getIntExtra(CAMERAX_MODEL, CameraXOption.FACE.ordinal)
        val cameraXOption =
            CameraXOption.values().elementAtOrElse(cameraxModel) { i -> CameraXOption.FACE }

        requestedOrientation =
            if (cameraXOption.requestedOrientation) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        switchCamera.visibility = if (cameraXOption.switchCamera) View.VISIBLE else View.GONE
        lensFacing =
            if (cameraXOption.defaultBackCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        bgMask.visibility = if (cameraXOption.showMask) View.VISIBLE else View.GONE
    }

    open fun bindView(): Unit {
        val binding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewView = binding.previewView
        switchCamera = binding.switchCamera
        bgMask = binding.bgMask

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.takePicture.setOnClickListener {
            takePicture()
        }
        binding.switchCamera.setOnClickListener {
            switchCamera()
        }
    }

    override fun onBackPressed() {
        finishByData(RESULT_CANCELED, Bundle().apply {
            putInt("code", 510)
            putString("err", "Cancel by user")
        })
    }

    /**
     * ???????????????
     */
    protected fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        //????????????????????????
        bindCameraUseCases()
    }

    /**
     * ??????
     */
    protected fun takePicture() {
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val metadata = ImageCapture.Metadata().apply {
            //????????????
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        imageCapture?.takePicture(
            outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    finishByData(RESULT_CANCELED, Bundle().apply {
                        putInt("code", 500)
                        putString("msg", "Capture error")
                    })
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")

                    finishByData(RESULT_OK, Bundle().apply {
                        putInt("code", 200)
                        putString("path", savedUri.path)
                        putString("msg", "Capture success")
                    })
                }
            })
    }

    private val uiHandler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Handler.createAsync(Looper.getMainLooper()) {
            return@createAsync true
        }
    } else {
        Handler()
    }

    fun finishByData(resultCode: Int, bundle: Bundle) {
        uiHandler.postAtFrontOfQueue {
            val path = bundle.getString("path")
            if (resultCode == RESULT_OK && null != path) {
                kycSuccess?.invoke(path, "")
            } else {
                kycFail?.invoke(bundle.getInt("code"), bundle.getString("msg") ?: "Unknown error")
            }
            setResult(resultCode, Intent().putExtras(bundle))
            finish()
        }
    }

    override fun getTargetResolution() =
        if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == requestedOrientation) Size(
            1080,
            1920
        ) else Size(1920, 1080)

    override fun onPreviewPrepared(preview: Preview?) {
        super.onPreviewPrepared(preview)
        mPreview?.setSurfaceProvider(previewView.surfaceProvider)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }
        }
    }

    companion object {
        /**
         * 0?????????????????????????????????(face)???1????????????????????????????????????(add)???2????????????????????????????????????(PAN)
         */
        const val CAMERAX_MODEL = "camerax_model"

        private const val FILENAME_FORMAT = "yyyyMMdd-HHmmssSSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

enum class CameraXOption(
    /**
     * true ?????????false ??????
     */
    var requestedOrientation: Boolean = false,
    /**
     * ?????????????????????????????????
     */
    var switchCamera: Boolean = false,
    /**
     * true ???????????????????????? false ?????????????????????
     */
    var defaultBackCamera: Boolean = false,

    /**
     * true ??????????????????
     */
    var showMask: Boolean = true,
) {
    FACE(switchCamera = true, showMask = false),
    ADD(defaultBackCamera = true),
    PAN(true, defaultBackCamera = true)
}
