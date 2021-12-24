package com.ibeilly.kyc.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ibeilly.kyc.app.databinding.FragmentFirstBinding
import com.ibeilly.kyc.camerax.CameraXActivity


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
            startActivityForResult(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    0
                ), 100
            )
        }

        binding.buttonAdd.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    1
                ), 101
            )
        }

        binding.buttonPan.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    2
                ), 102
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (100 == requestCode) {
            if (Activity.RESULT_OK == resultCode && data?.getStringExtra("code") == "200") {
                Toast.makeText(context, data?.getStringExtra("path"), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, data?.getStringExtra("msg"), Toast.LENGTH_SHORT).show()
            }
        } else if (101 == requestCode) {
            if (Activity.RESULT_OK == resultCode && data?.getStringExtra("code") == "200") {
                Toast.makeText(context, data?.getStringExtra("path"), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, data?.getStringExtra("msg"), Toast.LENGTH_SHORT).show()
            }
        } else if (102 == requestCode) {
            if (Activity.RESULT_OK == resultCode && data?.getStringExtra("code") == "200") {
                Toast.makeText(context, data?.getStringExtra("path"), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, data?.getStringExtra("msg"), Toast.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}