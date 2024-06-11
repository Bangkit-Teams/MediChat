package com.capstone.medichat.ui.main.content.rekomendasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.capstone.medichat.databinding.FragmentRecomendasiBinding


class RecomendasiFragment : Fragment() {

    private var _binding: FragmentRecomendasiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecomendasiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRekomendasi
            textView.text = "This is Rekomedasi Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



