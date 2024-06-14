package com.capstone.medichat.ui.main.content.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val chatViewModel: ChatViewModel by activityViewModels()

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

        // Observe messages in the ViewModel
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.messages.clear()
            chatAdapter.messages.addAll(messages)
            chatAdapter.notifyDataSetChanged()
            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }

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

            if (prompt.isNotBlank()) {
                // Add user message immediately
                val userMessage = ChatMessage(prompt, isUserMessage = true)
                chatViewModel.addMessage(userMessage)

                // Clear input field
                messageEditText.text.clear()

                // Show "Typing..." message
                val typingMessage = ChatMessage("Typing...", isUserMessage = false)
                val typingMessagePosition = chatViewModel.addMessage(typingMessage)

                // Call AI and update ViewModel with new data
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = generativeModel.generateContent(prompt)
                        withContext(Dispatchers.Main) {
                            // Ensure response.text is not null; use ?: to provide a default value if it is
                            val aiResponseText = response.text ?: "No response from AI."
                            // Update "Typing..." message with AI response
                            chatViewModel.updateMessage(typingMessagePosition, aiResponseText)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            // Handle error, show an error message
                            chatViewModel.updateMessage(typingMessagePosition, "Failed to get response from AI.")
                        }
                    }
                }
            }
        }
    }
}
