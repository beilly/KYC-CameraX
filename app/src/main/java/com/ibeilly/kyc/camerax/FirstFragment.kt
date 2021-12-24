package com.ibeilly.kyc.camerax

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ibeilly.kyc.camerax.databinding.FragmentFirstBinding

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
            startActivity(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    0
                )
            )
        }

        binding.buttonAdd.setOnClickListener {
            startActivity(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    1
                )
            )
        }

        binding.buttonPan.setOnClickListener {
            startActivity(
                Intent(
                    this@FirstFragment.requireActivity(),
                    CameraXActivity::class.java
                ).putExtra(
                    CameraXActivity.CAMERAX_MODEL,
                    2
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}