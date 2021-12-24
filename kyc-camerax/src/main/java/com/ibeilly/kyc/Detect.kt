package com.ibeilly.kyc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.ibeilly.kyc.camerax.CameraXActivity


internal lateinit var appCtx: Context

internal var hasInit = false

typealias KYCDetectSuccess = (String, String) -> Unit

typealias KYCDetectFail = (Int, String) -> Unit

var kycSuccess: KYCDetectSuccess? = null
var kycFail: KYCDetectFail? = null

fun init(ctx: Context): Unit {
    if (hasInit) {
        return
    }

    hasInit = true
    appCtx = ctx.applicationContext
}

fun startFace(success: KYCDetectSuccess, fail: KYCDetectFail): Unit {
    startKYC(0, success, fail)
}

fun startADD(success: KYCDetectSuccess, fail: KYCDetectFail): Unit {
    startKYC(1, success, fail)
}

fun startPAN(success: KYCDetectSuccess, fail: KYCDetectFail): Unit {
    startKYC(2, success, fail)
}

/**
 * 开始KYC
 */
private fun startKYC(cameraxModel: Int, success: KYCDetectSuccess, fail: KYCDetectFail) {
    kycSuccess = success
    kycFail = fail
    appCtx.apply {
        val checkSelfPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (PackageManager.PERMISSION_GRANTED != checkSelfPermission) {
            fail(89, "Camera permisson denied")
            return
        }
        startActivity(
            Intent(
                this,
                CameraXActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    cameraxModel
                ),
        )
    }
}

fun logI(obj: Any): Unit {
    Log.i("DetectCamera", obj.toString())
}

fun Context.toastShort(obj: Any?): Unit {
    Log.i("DetectCamera", obj.toString())
//    Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show()
}

