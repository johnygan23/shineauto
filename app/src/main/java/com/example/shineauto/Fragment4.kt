package com.example.shineauto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shineauto.databinding.Fragment4Binding
import com.example.shineauto.databinding.ItemProfileOptionBinding

// 1. Data class for a profile menu option
data class ProfileOption(
    val title: String,
    @DrawableRes val iconResId: Int,
    val action: () -> Unit
)

// 2. The Fragment
class Fragment4 : Fragment() {

    private var _binding: Fragment4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Create the list of profile options
        val options = listOf(
            ProfileOption("Manage My Cars", R.drawable.manage_cars) {
                Toast.makeText(context, "Navigating to My Cars...", Toast.LENGTH_SHORT).show()
            },
            ProfileOption("Manage Profile", R.drawable.manage_profile) {
                Toast.makeText(context, "Navigating to Settings...", Toast.LENGTH_SHORT).show()
            },
            ProfileOption("Log Out", R.drawable.logout) {
                Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show()
            }
        )

        // 4. Set up RecyclerView
        val adapter = ProfileOptionsAdapter(options)
        binding.profileOptionsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// 5. RecyclerView Adapter for the options
class ProfileOptionsAdapter(private val options: List<ProfileOption>) :
    RecyclerView.Adapter<ProfileOptionsAdapter.OptionViewHolder>() {

    class OptionViewHolder(private val binding: ItemProfileOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: ProfileOption) {
            binding.optionIcon.setImageResource(option.iconResId)
            binding.optionTitle.text = option.title
            itemView.setOnClickListener {
                option.action.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemProfileOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount() = options.size
}