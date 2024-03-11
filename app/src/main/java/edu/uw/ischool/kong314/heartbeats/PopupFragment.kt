package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class PopupFragment() : DialogFragment(R.layout.fragment_popup) {
    private lateinit var auth: FirebaseAuth
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private lateinit var usersPrivacy: Map<String, Boolean>
    private val TAG: String = "PopupFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        val currUID = auth.currentUser!!.uid

        val publicBtn = view.findViewById<RadioButton>(R.id.publicChoice)
        val privateBtn = view.findViewById<RadioButton>(R.id.privateChoice)


        databaseRepo.getUserPrivacy { users, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving challenges: ${error.message}")
            } else {
                if (users != null) {
                    println("$users")
                    usersPrivacy = users
                } else {
                    Log.e(TAG, "No challenges found.")
                }
                val privacyState = usersPrivacy.get(currUID)
                privateBtn.isChecked = privacyState!!
                publicBtn.isChecked = !privacyState
            }
        }

        val closeBtn = view.findViewById<Button>(R.id.applyBtn)

        closeBtn.setOnClickListener {
            if(privateBtn.isChecked) {
                databaseRepo.setUserPrivacy(currUID, true)
            } else {
                databaseRepo.setUserPrivacy(currUID, false)
            }
            Toast.makeText(context, "Settings Applied", Toast.LENGTH_LONG).show()
            dismiss()
        }
    }


}