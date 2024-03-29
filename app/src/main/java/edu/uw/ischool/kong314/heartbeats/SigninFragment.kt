package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible


class SigninFragment() : Fragment(R.layout.fragment_signin) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setBottomNavigationBarVisibility(View.GONE)

        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        signupBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SignupFragment()).addToBackStack(null).commit()
        }

        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, LoginFragment()).addToBackStack(null).commit()
        }
    }
}