package com.capstone.medichat.ui.main.content.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.medichat.R

class ChatAdapter(private val messages: MutableList<ChatMessage> = mutableListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View types co nstants
    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_AI = 2
    }

    // Determine the view type based on the message's sender
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUserMessage) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    // Create view holder based on view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_chat, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ai_chat, parent, false)
            AIViewHolder(view)
        }
    }

    // Bind data to the view holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserViewHolder) {
            holder.rightChatTextView.text = message.message
        } else if (holder is AIViewHolder) {
            holder.leftChatTextView.text = message.message
        }
    }

    // Return the number of messages
    override fun getItemCount(): Int = messages.size

    // Add a new message to the list and notify the adapter
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    // ViewHolder for user messages
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rightChatTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }

    // ViewHolder for AI messages
    class AIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftChatTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
    }
}
