package com.capstone.medichat.ui.main.content.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.medichat.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatbotFragment : Fragment() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRecyclerView: RecyclerView

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var generativeModel: GenerativeModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        // Find views by ID
        messageEditText = view.findViewById(R.id.message_edit_text)
        sendButton = view.findViewById(R.id.send_btn)
        chatRecyclerView = view.findViewById(R.id.chat_rv)

        // Initialize adapter and set layout manager for RecyclerView
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        // Initialize the generative model (Replace API key with your actual key)
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyAydROcl-fU9_TR4vgWj8W5SRhi6oZXtY4"
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendButton.setOnClickListener {
            val prompt = messageEditText.text.toString()

            // Call AI and update adapter with new data
            CoroutineScope(Dispatchers.IO).launch {
                val response = generativeModel.generateContent(prompt)
                withContext(Dispatchers.Main) {
                    val newMessage = ChatMessage(prompt, true) // User message
                    chatAdapter.addMessage(newMessage)

                    val generatedResponse = ChatMessage(response.text, false) // AI response
                    chatAdapter.addMessage(generatedResponse)

                    messageEditText.text.clear() // Optionally clear the input field
                }
            }
        }
    }
}

// Data class for chat messages
data class ChatMessage(val message: String?, val isUserMessage: Boolean)