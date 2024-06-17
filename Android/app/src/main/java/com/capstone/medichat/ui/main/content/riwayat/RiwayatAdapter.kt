package com.capstone.medichat.ui.main.content.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.medichat.data.database.ChatSaveMessage
import com.capstone.medichat.databinding.ItemUserChatBinding
import com.capstone.medichat.databinding.ItemAiChatBinding

class RiwayatAdapter : ListAdapter<ChatSaveMessage, RecyclerView.ViewHolder>(RiwayatDiffCallback()) {

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_BOT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemUserChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserViewHolder(binding)
            }
            VIEW_TYPE_BOT -> {
                val binding = ItemAiChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BotViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserViewHolder -> holder.bind(message)
            is BotViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isUserMessage) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    inner class UserViewHolder(private val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatSaveMessage: ChatSaveMessage) {
            binding.rightChatTextView.text = chatSaveMessage.message
        }
    }

    inner class BotViewHolder(private val binding: ItemAiChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatSaveMessage: ChatSaveMessage) {
            binding.leftChatTextView.text = chatSaveMessage.message
        }
    }

    class RiwayatDiffCallback : DiffUtil.ItemCallback<ChatSaveMessage>() {
        override fun areItemsTheSame(oldItem: ChatSaveMessage, newItem: ChatSaveMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatSaveMessage, newItem: ChatSaveMessage): Boolean {
            return oldItem == newItem
        }
    }
}
