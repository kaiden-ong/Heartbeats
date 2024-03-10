package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            // gives time to send new user data to database and also only 1 login process
            auth.createUserWithEmailAndPassword("test@test.com", "123456").addOnCompleteListener {

            }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, LoginFragment()).commit()
        }
    }
}