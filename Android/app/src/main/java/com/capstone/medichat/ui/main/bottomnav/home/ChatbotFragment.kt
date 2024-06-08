package com.capstone.medichat.ui.main.bottomnav.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.medichat.databinding.FragmentChatbotBinding


class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome

            textView.text = "this is Chatbot Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}