package edu.uw.ischool.kong314.heartbeats

import android.app.ProgressDialog
import android.content.ClipDescription
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.UUID

class PostFragment() : Fragment(R.layout.fragment_post) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        val selectImgBtn = view.findViewById<Button>(R.id.select_image)
        val cameraBtn = view.findViewById<Button>(R.id.capture_image)
        val imageView = view.findViewById<ImageView>(R.id.post_image)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).addToBackStack(null).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment()).addToBackStack(null).commit()
        }

        val title = view.findViewById<EditText>(R.id.title)
        val description = view.findViewById<EditText>(R.id.description)
        val postImgBtn = view.findViewById<ImageButton>(R.id.post_button)

        var imageUri : Uri? = null
        val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
            imageUri = uri
            imageView.setImageURI(uri)
        }

        val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
            imageView.setImageURI(null)
            imageView.setImageURI(imageUri)
        }

        val textChange: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isEmpty()) {
                    postImgBtn.visibility = View.GONE
                } else {
                    postImgBtn.visibility = View.VISIBLE
                }
            }
        }

        title.addTextChangedListener(textChange)
        description.addTextChangedListener(textChange)

        selectImgBtn.setOnClickListener {
            selectImageIntent.launch("image/*")
        }

        imageUri = createImageUri()
        cameraBtn.setOnClickListener {
            contract.launch(imageUri)
        }



        postImgBtn.setOnClickListener {
            uploadImageToFirebase(imageUri, title.text.toString(), description.text.toString())
        }
    }

    override fun onStop() {
        super.onStop()
        val title = view?.findViewById<EditText>(R.id.title)
        val description = view?.findViewById<EditText>(R.id.description)
        title?.setText("")
        description?.setText("")
    }
    private fun uploadImageToFirebase(uri: Uri?, title: String, desc: String) {

        if (uri != null) {
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Posting")
            progressDialog.show()
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(uri)
                .addOnSuccessListener (
                    OnSuccessListener<UploadTask.TaskSnapshot> { it ->
                        it.storage.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            val database = FirebaseDatabase.getInstance().getReference()

                            val postId = database.push().key

                            val hashMap = HashMap<String, Any>()
                            hashMap["image"] = imageUrl
                            hashMap["title"] = title
                            hashMap["desc"] = desc
                            Firebase.auth.currentUser?.let { it1 -> hashMap.put("by", it1.uid) }

                            database.child("Posts").child(postId!!).setValue(hashMap)

                            database.child("user_info").child(Firebase.auth.currentUser!!.uid).child("heartbeats")
                                .get().addOnSuccessListener {
                                    val heartPoints = it.value.toString().toInt() + 1

                                    val newHashMap = HashMap<String, Any>()
                                    newHashMap["heartbeats"] = heartPoints
                                    database.child("user_info").child(Firebase.auth.currentUser!!.uid).updateChildren(newHashMap)
                                }

//                            val newHashMap = HashMap<String, Any>()
//                            newHashMap.put("hearbeats", heartbeats)
//
//                            database.child(postId!!).setValue(hashMap)
//                            Firebase.auth.currentUser?.let { it1 ->
//                                database.child(it1.uid)
//                                    .updateChildren(newHashMap)
//                            }

                            progressDialog.dismiss()
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.container, HomeFragment()).commit()
                        }
                    }
                ).addOnFailureListener(
                    OnFailureListener {
                        Log.e("Upload Image", it.message.toString())
                    }
                )
        } else {
            Toast.makeText(activity,"Please select an image", Toast.LENGTH_LONG).show()
        }
    }

    private fun createImageUri(): Uri {
        val image = File(activity?.filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(requireContext(),
            "edu.uw.ischool.kong314.heartbeats.FileProvider",
            image)
    }

}