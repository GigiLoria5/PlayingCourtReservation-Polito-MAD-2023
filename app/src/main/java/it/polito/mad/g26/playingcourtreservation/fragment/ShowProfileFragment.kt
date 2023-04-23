package it.polito.mad.g26.playingcourtreservation.fragment

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import org.json.JSONObject


class ShowProfileFragment : Fragment(R.layout.activity_show_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.show_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Edit
                    R.id.edit_menu_item -> {
                        findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // Change Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Profile"

        // Add Tooltips
        val warningIcon = view.findViewById<ImageView>(R.id.warning_icon)
        TooltipCompat.setTooltipText(warningIcon, getString(R.string.warning_icon_tooltip))
        val dangerIcon = view.findViewById<ImageView>(R.id.danger_icon)
        TooltipCompat.setTooltipText(dangerIcon, getString(R.string.danger_icon_tooltip))

        loadProfikeAndImage()
    }

    fun loadProfikeAndImage(){

        //PROFILE MANAGEMENT
        //Load if already exist, otherwise it will load the hardcoded data
        val sharedPref = this.requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
        if(sharedPref.contains("profile")){
            val json= JSONObject(sharedPref.getString("profile","Default"))
            val username =requireView().findViewById<TextView>(R.id.username)
            username.text= json.getString("username")
            val position=requireView().findViewById<TextView>(R.id.position)
            position.text=json.getString("position")
            val age=requireView().findViewById<CustomTextView>(R.id.age).findViewById<TextView>(R.id.value)
            age.text=json.getString("age")
            val gender=requireView().findViewById<CustomTextView>(R.id.gender).findViewById<TextView>(R.id.value)
            gender.text=json.getString("gender")
            val fullName=requireView().findViewById<CustomTextView>(R.id.fullname).findViewById<TextView>(R.id.value)
            fullName.text=json.getString("fullName")
            val location=requireView().findViewById<CustomTextView>(R.id.location).findViewById<TextView>(R.id.value)
            location.text=json.getString("location")
        }

        //IMAGE MANAGEMENT
        //Load if exists, otherwise it will load the hardcoded image
        val file = requireContext().getFileStreamPath("imageBit")
        if(file.exists()){
            val fileInput= requireActivity().openFileInput("imageBit")
            if(fileInput.available()>0){
                val bitmap= BitmapFactory.decodeStream(fileInput)//already decompressed
                val avatarImage = requireView().findViewById<ImageView>(R.id.avatar)
                avatarImage.setImageBitmap(bitmap)
            }
            fileInput.close()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfikeAndImage()
    }

}