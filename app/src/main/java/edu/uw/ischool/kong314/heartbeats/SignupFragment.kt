package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.uw.ischool.kong314.heartbeats.databinding.FragmentSignupBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignupFragment() : Fragment(R.layout.fragment_signup) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignupBinding
    private lateinit var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        binding = FragmentSignupBinding.bind(view)
        database = Firebase.database.reference

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, LoginFragment()).commit()
        }

        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            val username = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passInput.text.toString()
            // gives time to send new user data to database and also only 1 login process
            if (checkFields()) {
                checkUsername(username) { usernameExists ->
                    if (!usernameExists) {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if(it.isSuccessful) {
                                Toast.makeText(requireContext(), "Successfully created account!", Toast.LENGTH_SHORT).show()
                                val user = auth.currentUser
                                val friends = listOf<String>("dummy")
                                database.child("user_info").child(user!!.uid).setValue(
                                    mapOf(
                                        "username" to username,
                                        "heartbeats" to 0,
                                        "friends" to friends
                                    )
                                )
                                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.container, ProfileFragment()).commit()
                            } else {
                                Toast.makeText(requireContext(), "Email already exists. Click Login!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Username already exists.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkUsername(username: String, callback: (Boolean) -> Unit) {
        val userRef = database.child("user_info")
        userRef.orderByChild("username").equalTo(username).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result.exists())
            } else {
                Log.d("CheckUsername", "Failed to check usernames")
                callback(false)
            }
        }
    }

    private fun checkFields(): Boolean {
        val email = binding.emailInput.text.toString()
        if (binding.nameInput.text.toString() == "") {
            Toast.makeText(requireContext(), "Name is a required field", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.passInput.text.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.passInput.text.toString() != binding.confirmPassInput.text.toString()) {
            Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}