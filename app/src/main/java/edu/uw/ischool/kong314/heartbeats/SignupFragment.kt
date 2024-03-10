package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.uw.ischool.kong314.heartbeats.databinding.FragmentSignupBinding


class SignupFragment() : Fragment(R.layout.fragment_signup) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignupBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        binding = FragmentSignupBinding.bind(view)

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, LoginFragment()).commit()
        }

//        val nameInput = view.findViewById<EditText>(R.id.nameInput)
//        val emailInput = view.findViewById<EditText>(R.id.emailInput)
//        val passInput = view.findViewById<EditText>(R.id.passInput)
//        val confirmPassInput = view.findViewById<EditText>(R.id.confirmPassInput)


        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passInput.text.toString()
            // gives time to send new user data to database and also only 1 login process
            if (checkFields()) {
                auth.createUserWithEmailAndPassword(email, password)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, ProfileFragment()).commit()
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