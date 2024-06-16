package com.capstone.medichat.ui.main.content.home

import android.os.Bundle
import android.util.Log
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
import com.capstone.medichat.data.api.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatbotFragment : Fragment() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRecyclerView: RecyclerView

    private lateinit var chatAdapter: ChatAdapter
    private val chatViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        messageEditText = view.findViewById(R.id.message_edit_text)
        sendButton = view.findViewById(R.id.send_btn)
        chatRecyclerView = view.findViewById(R.id.chat_rv)

        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.messages.clear()
            chatAdapter.messages.addAll(messages)
            chatAdapter.notifyDataSetChanged()
            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendButton.setOnClickListener {
            val prompt = messageEditText.text.toString()
            if (prompt.isNotBlank()) {

                val userMessage = ChatMessage(prompt, isUserMessage = true)
                chatViewModel.addMessage(userMessage)
                messageEditText.text.clear()

                val typingMessage = ChatMessage("MediChat is Typing...", isUserMessage = false)
                val typingMessagePosition = chatViewModel.addMessage(typingMessage)
                sendMessageToApi(prompt, typingMessagePosition)
            }
        }

        val newButton: ImageButton = view.findViewById(R.id.new_btn)
        newButton.setOnClickListener {
            chatViewModel.clearMessages()
        }
    }

    private fun sendMessageToApi(prompt: String, typingMessagePosition: Int) {
        val json = """
        {
            "user_message": "$prompt"
        }
        """
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val service = ApiConfig.additionalService
        val call = service.sendMessage(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseText = response.body()?.string() ?: "No response from server."
                    val cleanedResponse = cleanApiResponse(responseText)
                    val aiMessage = ChatMessage(cleanedResponse, isUserMessage = false)
                    aiMessage.message?.let {
                        chatViewModel.updateMessage(typingMessagePosition, it)
                    }
                } else {
                    val errorMessage = ChatMessage("Request failed with code: ${response.code()}", isUserMessage = false)
                    errorMessage.message?.let {
                        chatViewModel.updateMessage(typingMessagePosition, it)
                    }
                    Log.e("API_ERROR", "Request failed with code: ${response.code()}, message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMessage = ChatMessage("Failed to get response from API.", isUserMessage = false)
                errorMessage.message?.let { chatViewModel.updateMessage(typingMessagePosition, it) }
                Log.e("API_ERROR", "Failed to get response from API", t)
                t.printStackTrace()
            }
        })
    }

    private fun cleanApiResponse(responseText: String): String {
        var cleanedResponse = responseText


        cleanedResponse = "Hi, saya akan membantu anda terkait hal tersebut.\n"+cleanedResponse
        cleanedResponse = cleanedResponse.replace("response\":", "")
        cleanedResponse = cleanedResponse.replace("{", "")
        cleanedResponse = cleanedResponse.replace("}", "")
        cleanedResponse = cleanedResponse.replace("\\n", "\n")
        cleanedResponse = cleanedResponse.replace("\"", "")
        cleanedResponse = cleanedResponse.replace("\\u00a0", "")

        cleanedResponse = handleNumberedList(cleanedResponse)


        return cleanedResponse
    }

    private fun handleNumberedList(text: String): String {

        var cleanedText = text

        var counter = 1
        while (cleanedText.contains("\\n$counter\\. ".toRegex())) {
            cleanedText = cleanedText.replace("$counter\\. ".toRegex(), "\\$counter\\.")
            counter++
        }


        return cleanedText
    }
}
