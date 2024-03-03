package edu.uw.ischool.kong314.heartbeats

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class PostFragment() : Fragment(R.layout.fragment_post)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val selectImgBtn = view.findViewById<Button>(R.id.select_image)
        val imageView = view.findViewById<ImageView>(R.id.post_image)

        val title = view.findViewById<EditText>(R.id.title)
        val description = view.findViewById<EditText>(R.id.description)
        val postImgBtn = view.findViewById<ImageButton>(R.id.post_button)

        val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
            imageView.setImageURI(uri)
        }

        val textChange: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length == 0) {
                    postImgBtn.setVisibility(View.GONE)
                } else {
                    postImgBtn.setVisibility(View.VISIBLE)
                }
            }
        }

        title.addTextChangedListener(textChange)
        description.addTextChangedListener(textChange)

        selectImgBtn.setOnClickListener {
            selectImageIntent.launch("image/*")
        }

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).commit()
        }

        postImgBtn.setOnClickListener {
            Toast.makeText(activity, "Title: ${title.getText().toString()} and Description: ${description.getText().toString()}", Toast.LENGTH_LONG).show()
        }
    }


}