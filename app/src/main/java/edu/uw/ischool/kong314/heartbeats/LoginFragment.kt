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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.uw.ischool.kong314.heartbeats.databinding.FragmentLoginBinding

class LoginFragment() : Fragment(R.layout.fragment_login) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        binding = FragmentLoginBinding.bind(view)

        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SignupFragment()).addToBackStack(null).commit()
        }

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pwd = binding.passInput.text.toString()
            Log.d("FROMLOGIN", email.toString())
            if (checkFields()) {
                auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Successfully logged in!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val fm = requireActivity().supportFragmentManager
                        val transaction = fm.beginTransaction()
                        for (i in 0 until fm.backStackEntryCount) {
                            fm.popBackStack()
                        }
                        transaction.replace(R.id.container, HomeFragment()).commit()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Email/Password are incorrect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkFields(): Boolean {
        val email = binding.emailInput.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.passInput.text.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}