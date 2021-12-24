package com.ibeilly.kyc.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ibeilly.kyc.*
import com.ibeilly.kyc.app.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonFace.setOnClickListener {
            startWithPermissionCheck(100)
        }

        binding.buttonAdd.setOnClickListener {
            startWithPermissionCheck(101)
        }

        binding.buttonPan.setOnClickListener {
            startWithPermissionCheck(102)
        }
    }

    private fun getSuccessCall(tag: String): KYCDetectSuccess {
        return { path, _ ->
            Toast.makeText(context, "[$tag] => $path", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFailCall(tag: String): KYCDetectFail {
        return { code, msg ->
            Toast.makeText(context, "[$tag] => $code: $msg", Toast.LENGTH_SHORT).show()
        }
    }

    fun startWithPermissionCheck(code: Int) {
        // Request camera permissions
        if (allPermissionsGranted()) {
            when (code) {
                100 -> startFace(getSuccessCall("face"), getFailCall("face"))
                101 -> startADD(getSuccessCall("add"), getFailCall("add"))
                102 -> startPAN(getSuccessCall("pan"), getFailCall("pan"))
            }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS, code
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode in arrayListOf(100, 101, 102)) {
            if (allPermissionsGranted()) {
                startWithPermissionCheck(requestCode)
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}