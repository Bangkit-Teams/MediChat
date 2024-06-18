package com.capstone.medichat.ui.main.content.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.capstone.medichat.data.database.ChatDatabase
import com.capstone.medichat.databinding.FragmentRiwayatBinding


class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatDatabase: ChatDatabase
    private lateinit var riwayatAdapter: RiwayatAdapter

    private val riwayatViewModel: RiwayatViewModel by viewModels {
        RiwayatViewModelFactory(chatDatabase)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        val view = binding.root

        chatDatabase = Room.databaseBuilder(
            requireContext().applicationContext,
            ChatDatabase::class.java, "chat_database"
        ).build()

        setupRecyclerView()
        observeRiwayatMessages()

        return view
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatAdapter()

        binding.rvRiwayat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = riwayatAdapter
        }
    }

    private fun observeRiwayatMessages() {
        riwayatViewModel.allRiwayatMessages.observe(viewLifecycleOwner, { messages ->
            riwayatAdapter.submitList(messages)
            binding.rvRiwayat.scrollToPosition(messages.size - 1)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


