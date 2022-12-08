package com.example.finalterm

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.finalterm.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    lateinit var mAuth: FirebaseAuth

    //구글 로그인
    var mGoogleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //로그인 버튼 이벤트
        binding.loginBtn.setOnClickListener {

            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            login(email, password)
        }

        //회원가입 버튼 이벤트
        binding.signUpBtn.setOnClickListener {
            val intent: Intent = Intent(this@LogInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.gloginBtn.setOnClickListener{
            googleLogin()
        }
    }

    /**
     * 로그인
     */
    private fun login(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //성공 시 실행
                    val intent: Intent = Intent(this@LogInActivity,
                        MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    //실패 시 실행
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "Error: ${task.exception}")
                }
            }
    }

    fun googleLogin() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleLoginLauncher.launch(signInIntent)
        val intent: Intent = Intent(this@LogInActivity,
            MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
    }

    var googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == -1) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            getGoogleInfo(task)
        }
    }
    fun getGoogleInfo(completedTask: Task<GoogleSignInAccount>) {
        try {
            val TAG = "구글 로그인 결과"
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.id!!)
            Log.d(TAG, account.familyName!!)
            Log.d(TAG, account.givenName!!)
            Log.d(TAG, account.email!!)
        }
        catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}