package xp.level.booster.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import xp.level.booster.AppBaseActivity
import xp.level.booster.R
import xp.level.booster.extensions.launchActivity
import xp.level.booster.extensions.onClick

class LoginActivity : AppBaseActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    //    private lateinit var binding: ActivityGoogleBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth


        animation_view2.onClick {
            signIn()
        }

    }

    private fun signIn() {
        googleSignInClient = gamesMakeGoogleSignInOptions()

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:$account")
                firebaseAuthWithPlayGames(account)

                var gamesClient = account.let { Games.getGamesClient(this, it) }
                gamesClient =
                    Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                gamesClient.setViewForPopups(findViewById(android.R.id.content))
                gamesClient.setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // Call this both in the silent sign-in task's OnCompleteListener and in the
    // Activity's onActivityResult handler.
    private fun firebaseAuthWithPlayGames(acct: GoogleSignInAccount) {
//        Log.d("TAG", "firebaseAuthWithPlayGames:" + acct.id!!)

        val credential = PlayGamesAuthProvider.getCredential(acct.serverAuthCode!!)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    var user = auth.currentUser

//                    revokeAccess()
                    launchActivity<MainActivity>()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }
    }


    private fun gamesMakeGoogleSignInOptions(): GoogleSignInClient {
        // [START games_google_signin_options]
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .build()
        // [END games_google_signin_options]
        return GoogleSignIn.getClient(this, gso)
    }

    private fun gamesGetUserInfo() {

        // [START games_get_user_info]
        val user = auth.currentUser
        user?.let {
            val playerName = user.displayName

            // The user's Id, unique to the Firebase project.
            // Do NOT use this value to authenticate with your backend server, if you
            // have one; use FirebaseUser.getIdToken() instead.
            val uid = user.uid
        }

        // [END games_get_user_info]
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
        }
    }
}