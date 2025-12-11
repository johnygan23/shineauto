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

    // [Requirement 3: Image Upload] FileProvider Mechanism
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

        // Feature 3: Image Upload
        binding.profileImage.setOnClickListener {
            pickImage.launch("image/*") // Opens gallery for image selection
        }

        // Assuming you made the username/email/contact views clickable to open edit dialog
        binding.userNameText.setOnClickListener { showEditProfileDialog() }
        binding.userEmailText.setOnClickListener { showEditProfileDialog() }

        // Logout button logic (already discussed)
        binding.logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logout() ?: (activity as? ProviderActivity)?.logout()
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
                        binding.profileImage.setImageURI(Uri.parse(user.profileImageUri))
                    } else {
                        // Default avatar if no URI exists
                        binding.profileImage.setImageResource(com.example.shineauto.R.drawable.profile_avatar)
                    }
                }
            }
        }
    }

    // [Requirement 4: Edit Details]
    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        currentUser?.let { user ->
            // Pre-fill fields
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
                    // Update SharedPreferences if username changed
                    if (newUsername != user.username) {
                        requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit().putString("USERNAME", newUsername).apply()
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