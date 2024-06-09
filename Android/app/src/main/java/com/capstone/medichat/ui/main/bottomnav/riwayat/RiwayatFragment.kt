package com.capstone.medichat.ui.main.bottomnav.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.medichat.databinding.FragmentRiwayatBinding

class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
            textView.text = "This is Riwayat Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}