package it.polito.mad.g26.playingcourtreservation.fragment

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ShowProfileAdapter
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import org.json.JSONObject

class ShowProfileFragment : Fragment(R.layout.activity_show_profile) {

    private lateinit var sportList : List<String>
    private lateinit var ratingList: MutableList<Float>
    private lateinit var sportRecycleView: RecyclerView
    private lateinit var avatarImage: ShapeableImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Profile", false)

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

        // Add Tooltips
        val warningIcon = view.findViewById<ImageView>(R.id.warning_icon)
        TooltipCompat.setTooltipText(warningIcon, getString(R.string.warning_icon_tooltip))
        val dangerIcon = view.findViewById<ImageView>(R.id.danger_icon)
        TooltipCompat.setTooltipText(dangerIcon, getString(R.string.danger_icon_tooltip))

        loadProfileAndImage()
    }

    private fun loadProfileAndImage() {

        //PROFILE MANAGEMENT
        //Load if already exist, otherwise it will load the hardcoded data
        val sharedPref = this.requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
        if (sharedPref.contains("profile")) {
            val json = sharedPref.getString("profile", "Default")?.let { JSONObject(it) }
            val username = requireView().findViewById<TextView>(R.id.username)
            username.text = json?.getString("username")
            val position = requireView().findViewById<CustomTextView>(R.id.position)
                .findViewById<TextView>(R.id.value)
            position.text = json?.getString("position")
            val age = requireView().findViewById<CustomTextView>(R.id.age)
                .findViewById<TextView>(R.id.value)
            age.text = json?.getString("age")
            val gender = requireView().findViewById<CustomTextView>(R.id.gender)
                .findViewById<TextView>(R.id.value)
            gender.text = json?.getString("gender")
            val fullName = requireView().findViewById<CustomTextView>(R.id.full_name)
                .findViewById<TextView>(R.id.value)
            fullName.text = json?.getString("fullName")
            val location = requireView().findViewById<CustomTextView>(R.id.location)
                .findViewById<TextView>(R.id.value)
            location.text = json?.getString("location")
            val guide=requireView().findViewById<Guideline>(R.id.vertical_guideline)
            val metric= requireContext().resources.displayMetrics
            // Get the current configuration
            val configuration: Configuration = resources.configuration
            // Check if the orientation is landscape
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // The layout is in landscape mode
                val height=metric.widthPixels
                var pixelsLimit=(height/100)*33
                guide.setGuidelineBegin(pixelsLimit)
                //set the image
                pixelsLimit=(pixelsLimit*70)/100
                avatarImage=requireView().findViewById<ShapeableImageView>(R.id.avatar)
                avatarImage.layoutParams.width=pixelsLimit
                avatarImage.layoutParams.height=pixelsLimit
            } else {
                // The layout is in portrait mode
                val height=metric.heightPixels
                var pixelsLimit=(height/100)*33
                guide.setGuidelineBegin(pixelsLimit)
                //set the image
                pixelsLimit=(pixelsLimit*70)/100
                avatarImage=requireView().findViewById<ShapeableImageView>(R.id.avatar)
                avatarImage.layoutParams.width=pixelsLimit
                avatarImage.layoutParams.height=pixelsLimit
            }


            if(json?.getString("rating")!=null){
                //retrieve name sport
                sportList=resources.getStringArray(R.array.sport_array).toList()
                //retrieve rating from json
                val sublist= json.getString("rating").split(",")
                //transform in float
                ratingList= mutableListOf()
                for(string in sublist)
                    {ratingList.add(string.toFloat())}
                //link the two list and sort by ranking
                val sortedPair=sportList.zip(ratingList).sortedByDescending { it.second }
                val sportList2=sortedPair.map{it.first}
                val sortedRating = sortedPair.map { it.second }
                //filter the 0 rating
                val ratingFinal=sortedRating.filter{it!=0F}

                if(ratingFinal.isEmpty()){
                    val rootView=requireView().findViewById<ConstraintLayout>(R.id.show_profile_main_container)
                    val sportCardRating=requireView().findViewById<MaterialCardView>(R.id.show_profile_sport_card_view)
                    rootView.removeView(sportCardRating)
                }else{
                    //populate recycler view for every rating>0
                    sportRecycleView=requireView().findViewById(R.id.show_profile_recycler_view)
                    sportRecycleView.adapter= ShowProfileAdapter(sportList2,ratingFinal)
                    sportRecycleView.layoutManager=
                        LinearLayoutManager(context)
                }
            }else{
                val rootView=requireView().findViewById<ConstraintLayout>(R.id.show_profile_main_container)
                val sportCardRating=requireView().findViewById<MaterialCardView>(R.id.show_profile_sport_card_view)
                rootView.removeView(sportCardRating)
            }
        }

        //IMAGE MANAGEMENT
        //Load if exists, otherwise it will load the hardcoded image
        val file = requireContext().getFileStreamPath("imageBit")
        if (file.exists()) {
            val fileInput = requireActivity().openFileInput("imageBit")
            if (fileInput.available() > 0) {
                val bitmap = BitmapFactory.decodeStream(fileInput)//already decompressed
                avatarImage = requireView().findViewById(R.id.avatar)
                avatarImage.setImageBitmap(bitmap)
            }
            fileInput.close()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileAndImage()
    }

}