package com.example.shineauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.FragmentAdminUserListBinding
import com.example.shineauto.model.User
import com.example.shineauto.ui.adapter.AdminUserAdapter
import kotlinx.coroutines.launch

class AdminUserListFragment : Fragment() {

    private var _binding: FragmentAdminUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminUserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminUserAdapter { user ->
            confirmDeleteUser(user)
        }
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.usersRecyclerView.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val users = ShineAutoDatabase.getDatabase(requireContext()).userDao().getAllUsers()
            adapter.submitList(users)
        }
    }

    private fun confirmDeleteUser(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.username}? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    ShineAutoDatabase.getDatabase(requireContext()).userDao().deleteUser(user)
                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                    loadUsers()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}