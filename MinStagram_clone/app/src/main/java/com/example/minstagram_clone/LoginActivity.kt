package com.example.minstagram_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FacebookAuthProvider
import java.lang.Exception
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.security.auth.callback.CallbackHandler
import java.security.MessageDigest as MessageDigest


class LoginActivity : AppCompatActivity() {
    var auth:FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var Google_LOGIN_CODE = 9001
    //var callbackManager : CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener{
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            //First Step
            googleLogin()
        }
        /*facebook_login_button.setOnClickListener {
            //First Step
            facebookLogin()
        }*/
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("866871640009-q5hg73tmfeuoh84jj1e8lv2g4tja6g76.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //printHashKey()
        //callbackManager = CallbackManager.Factory.create()
    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, Google_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //callbackManager?.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Google_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess) {
                var account = result.signInAccount
                //Second Step
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    //login
                    moveMainPage(task.result?.user)
                } else {
                    //Wrong id and password
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener {
                task ->
            if(task.isSuccessful) {
                //Creating user account
                moveMainPage(task.result?.user)
            } else if(task.exception?.message.isNullOrEmpty()) {
                //show error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                //login
                signinEmail()
            }
        }
    }
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    //login
                    moveMainPage(task.result?.user)
                } else {
                    //Wrong id and password
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /*fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
                //함수들 추가해야 함(페이스북 로그인 영상 참고)
            })
    }
    fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    //Third Step
                    //login
                    moveMainPage(task.result?.user)
                } else {
                    //Wrong id and password
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun printHashKey() = try {
        val info = packageManager
            .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            val hashKey = String(Base64.encode(md.digest(), 0))
            Log.i("TAG", "printHashKey() Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        Log.e("TAG", "printHashKey()", e)
    } catch (e: Exception) {
        Log.e("TAG", "printHashKey()", e)
    }*/
}