package com.example.shineauto.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shineauto.R
import com.example.shineauto.data.ShineAutoDatabase
import com.example.shineauto.databinding.DialogEditProfileBinding
import com.example.shineauto.databinding.FragmentProfileBinding
import com.example.shineauto.model.User
import com.example.shineauto.utils.AppUtils
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var currentUser: User? = null
    private val PREFS_NAME = "ShineAutoPrefs"

    // Image Picker Launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val path = AppUtils.saveImageToInternalStorage(requireContext(), it)
            path?.let { imagePath ->
                // 1. Update UI
                binding.profileImage.setImageURI(Uri.parse(imagePath))

                // 2. Update Database
                currentUser?.let { user ->
                    val updatedUser = user.copy(profileImageUri = imagePath)
                    lifecycleScope.launch {
                        ShineAutoDatabase.getDatabase(requireContext()).userDao().updateUser(updatedUser)
                        currentUser = updatedUser
                        Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserData()

        // Button: Change Picture
        binding.btnChangePicture.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Button: Edit Details
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Button: Logout
        binding.logoutButton.setOnClickListener {
            val act = activity
            when (act) {
                is CustomerActivity -> act.logout()
                is ProviderActivity -> act.logout()
                is AdminActivity -> act.logout()
            }
        }
    }

    private fun loadUserData() {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        if (userId != -1) {
            lifecycleScope.launch {
                currentUser = ShineAutoDatabase.getDatabase(requireContext()).userDao().getUserById(userId)
                currentUser?.let { user ->
                    binding.userNameText.text = user.username
                    binding.userEmailText.text = user.contactInfo

                    if (user.profileImageUri != null) {
                        try {
                            binding.profileImage.setImageURI(Uri.parse(user.profileImageUri))
                        } catch (e: Exception) {
                            binding.profileImage.setImageResource(R.drawable.profile_avatar)
                        }
                    } else {
                        binding.profileImage.setImageResource(R.drawable.profile_avatar)
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        currentUser?.let { user ->
            dialogBinding.editProfileUsername.setText(user.username)
            dialogBinding.editProfileContact.setText(user.contactInfo)
        }

        dialogBinding.btnSaveProfile.setOnClickListener {
            val newUsername = dialogBinding.editProfileUsername.text.toString()
            val newContact = dialogBinding.editProfileContact.text.toString()
            val newPassword = dialogBinding.editProfilePassword.text.toString()

            if (newUsername.isEmpty() || newContact.isEmpty()) {
                Toast.makeText(context, "Username and contact cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                val finalPassword = if (newPassword.isNotEmpty()) newPassword else user.password

                val updatedUser = user.copy(
                    username = newUsername,
                    contactInfo = newContact,
                    password = finalPassword
                )

                lifecycleScope.launch {
                    ShineAutoDatabase.getDatabase(requireContext()).userDao().updateUser(updatedUser)
                    currentUser = updatedUser

                    if (newUsername != user.username) {
                        requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit()
                            .putString("USERNAME", newUsername).apply()
                    }
                    loadUserData() // Refresh UI
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}