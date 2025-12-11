package com.example.shineauto.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.R
import com.example.shineauto.databinding.ItemAdminUserBinding
import com.example.shineauto.model.User

class AdminUserAdapter(
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    private var users = listOf<User>()

    fun submitList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(private val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userName.text = user.username
            binding.userRole.text = "ROLE: ${user.role}"
            binding.userId.text = "USER ID: ${user.id}"
            binding.userContact.text = user.contactInfo

            if (user.profileImageUri != null) {
                try {
                    binding.userIcon.setImageURI(Uri.parse(user.profileImageUri))
                } catch (e: Exception) {
                    binding.userIcon.setImageResource(R.drawable.profile_avatar)
                }
            } else {
                binding.userIcon.setImageResource(R.drawable.profile_avatar)
            }

            //Hide Delete Button for admin account
            if (user.role == "ADMIN") {
                binding.btnDeleteUser.visibility = View.GONE
                binding.btnDeleteUser.setOnClickListener(null)
            } else {
                binding.btnDeleteUser.visibility = View.VISIBLE
                binding.btnDeleteUser.setOnClickListener { onDeleteClick(user) }
            }
        }
    }
}