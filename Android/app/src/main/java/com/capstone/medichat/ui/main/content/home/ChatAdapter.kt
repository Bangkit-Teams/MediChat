package com.capstone.medichat.ui.main.content.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.medichat.R

class ChatAdapter(val messages: MutableList<ChatMessage> = mutableListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_AI = 2

    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUserMessage) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_chat, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ai_chat, parent, false)
            AIViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserViewHolder) {
            holder.rightChatTextView.text = message.message
        } else if (holder is AIViewHolder) {
            holder.leftChatTextView.text = message.message
        }
    }

    override fun getItemCount(): Int = messages.size

    // Method to add new messages
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rightChatTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }

    class AIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftChatTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
    }
}
