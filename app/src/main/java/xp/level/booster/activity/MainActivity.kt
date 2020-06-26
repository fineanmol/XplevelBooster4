package xp.level.booster.activity

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.billingclient.api.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.games.Games
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import xp.level.booster.AppBaseActivity
import xp.level.booster.R
import xp.level.booster.adapters.ProductsAdapter
import xp.level.booster.extensions.*

class MainActivity : AppBaseActivity(), PurchasesUpdatedListener, PurchaseHistoryResponseListener {

    private lateinit var billingClient: BillingClient
    private lateinit var productsAdapter: ProductsAdapter

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    protected val RC_LEADERBOARD_UI = 9004
    private val RC_ACHIEVEMENT_UI = 9003

    private val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 9001
    private val skuList = listOf("premium")

    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBillingClient()
        setContentView(R.layout.activity_main)
        unlockStatus.visibility = View.INVISIBLE



        welcomeText.text = """Welcome ${Firebase.auth.currentUser!!.displayName}"""

        setupAppFunctions()
        unlockAchievements()
    }




    @SuppressLint("NewApi")
    private fun setupAppFunctions() {
        ratingLottie.onClick {

            showRatingDialog()
        }
        feedbackLottie.onClick {
            showFeedbackDialog(this@MainActivity)

        }
        instagramLottie.onClick {
            val uri = Uri.parse("http://instagram.com/nightowldevelopers")
            val likeIng = Intent(Intent.ACTION_VIEW, uri)

            likeIng.setPackage("com.instagram.android")

            try {

                startActivity(likeIng)
                Toast.makeText(
                    this@MainActivity,
                    "Follow Us \n& Unlock your Achievement",
                    Toast.LENGTH_LONG
                ).show()
                Games.getAchievementsClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                    .unlock(getString(R.string.achievement_level_20))
                Games.getLeaderboardsClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 50000)
                Handler().postDelayed(Runnable {
                    // Do something after 5s = 5000ms
                    /*val mPlayer =
                        MediaPlayer.create(this@MainActivity, R.raw.ta_da_sound_click)
                    mPlayer.start()*/
                    toast("Hurrah! Your Instagram Achievement is Unlocked !!",Toast.LENGTH_SHORT)

                }, 13000)
            } catch (e: ActivityNotFoundException) {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/nightowldevelopers")
                    )
                )
                Toast.makeText(
                    this@MainActivity,
                    "Follow Us \n& Unlock your Achievement",
                    Toast.LENGTH_LONG
                ).show()
//                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
//                    .unlock(getString(R.string.achievement_instagram_achievement))
                Games.getLeaderboardsClient(this@MainActivity, GoogleSignIn.getLastSignedInAccount(this@MainActivity)!!)
                    .submitScore(getString(R.string.leaderboard_leaderboard), 200000)
                Handler().postDelayed(Runnable {
                    // Do something after 5s = 5000ms
                    /*val mPlayer =
                        MediaPlayer.create(this@MainActivity, R.raw.ta_da_sound_click)
                    mPlayer.start()*/
                    Toast.makeText(
                        this@MainActivity,
                        "Hurrah! Your Instagram Achievement is Unlocked !!",
                        Toast.LENGTH_LONG
                    ).show()
                }, 13000)
            }

        }

        achievementLottie.onClick {
            showAchievements()
        }

    }

    private fun showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .achievementsIntent
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI) }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override
            fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    println("BILLING | startConnection | RESULT OK")
                    //onLoadProductsClicked()
                    loadProducts()
                    // The BillingClient is ready. You can query purchases here.
                    onLoadProductsClicked() // This is used to fetch purchased items from google play store
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                toast("Billing service disconnected...")
            }
        }
        )
    }

    private fun loadProducts() {
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)
    }

    fun onLoadProductsClicked() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { responseResult, skuDetailsList ->
                if (responseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    println("querySkuDetailsAsync, responseResult: $responseResult")
                    if (skuDetailsList != null) {
                        initProductAdapter(skuDetailsList)
                    } else {
                        toast("sku list not found!!!")
                    }
                } else {
                    println("Can't querySkuDetailsAsync, responseResult: $responseResult")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    private fun initProductAdapter(skuDetailsList: List<SkuDetails>) {
        productsAdapter = ProductsAdapter(skuDetailsList) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        }
        products.adapter = productsAdapter
    }


    /**
     *
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                for (purchase in purchaseList!!) {
                    acknowledgePurchase(purchase)
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                toast("You've cancelled the Google play billing process...")
            }
            else -> {
                toast("Item not found or Google play billing error...")

            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        val debugMessage = billingResult.debugMessage
                        toast("Item Purchased")

                        /** Achievements Unlocking*/
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_1))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 10000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_2))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 20000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_3))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 30000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_4))
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_5))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 40000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_6))
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_7))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 60000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_8))
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_10))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 100000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_11))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 110000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_12))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 120000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_13))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 130000)
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_14))
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_15))
                        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .unlock(getString(R.string.achievement_level_16))
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 140000)
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                            .submitScore(getString(R.string.leaderboard_leaderboard), 150000)
                        /** Achievements Unlocked*/
                    }
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
                println("AllowMultiplePurchases success")
                toast("MultiplePurchase:success")
            } else {
                println("Can't allowMultiplePurchases")
                toast("MultiplePurchase:success")
            }
        }
    }

/*    private fun clearHistory() {
        billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            ?.forEach {
                billingClient.consumeAsync(it.purchaseToken) { responseCode, purchaseToken ->
                    if (responseCode == BillingClient.BillingResponse.OK && purchaseToken != null) {
                        println("onPurchases Updated consumeAsync, purchases token removed: $purchaseToken")
                    } else {
                        println("onPurchases some troubles happened: $responseCode")
                    }
                }
            }
    }*/

    override fun onPurchaseHistoryResponse(
        billingResult: BillingResult,
        purchaseHistoryList: MutableList<PurchaseHistoryRecord>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (!(purchaseHistoryList).isNullOrEmpty()) {
                for (purchase in purchaseHistoryList) {
                    /*for (books in booksList) {
                        if (purchase.sku == books.id) {
                            books.isPurchased = true
                        }
                    }*/
                }
            }
        }
    }


    private fun unlockAchievements() {
        var googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        var username = Firebase.auth.currentUser!!.displayName

        loader.visibility = View.VISIBLE
        unlockStatus.visibility = View.VISIBLE
        landingPage.visibility = View.VISIBLE
        achievementLottie.visibility = View.INVISIBLE
        achievementText.visibility = View.INVISIBLE
        instagramLottie.visibility = View.INVISIBLE
        instagramText.visibility = View.INVISIBLE
        /*buttonTap.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "end")
                //Your code for remove the fragment
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
                loader.visibility = View.VISIBLE
                products.visibility = View.INVISIBLE
                *//*buttonTap.visibility = View.INVISIBLE
                buttonTap.removeAllAnimatorListeners()*//*
            }
        })*/

        loader.visibility = View.VISIBLE
        products.visibility = View.INVISIBLE
        trophy.visibility = View.VISIBLE
        trophy.repeatCount = 20
        trophy.playAnimation()
        trophy.addAnimatorListener(object :
            Animator.AnimatorListener {
            var i = 1
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
                unlockStatus.visibility = View.VISIBLE
                unlockStatus.text = "Unlocking achievemnet " + i
                i++
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "end")
                //Your code for remove the fragment
                trophy.visibility = View.INVISIBLE
                successStar.visibility = View.VISIBLE
                successStar.playAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
                unlockStatus.text = "Unlocking achievemnet " + i
                i++
            }
        })
        successStar.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
                unlockStatus.text = "Hurray! All achievements unlocked!"
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "end")
                //Your code for remove the fragment
                products.visibility = View.VISIBLE
//                buttonTap.visibility = View.VISIBLE
                loader.visibility = View.INVISIBLE
                unlockStatus.visibility = View.INVISIBLE
                landingPage.visibility = View.INVISIBLE
                achievementLottie.visibility = View.VISIBLE
                achievementText.visibility = View.VISIBLE
                instagramLottie.visibility = View.VISIBLE
                instagramText.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
            }
        })

        println("Purchase Done!")
    }


    private fun signOut() {
        // Firebase sign out
        //signInButton.reset()
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
            products.visibility = View.GONE
        }
    }


    override fun onBackPressed() {
        revokeAccess()
        super.onBackPressed()

    }

    private fun revokeAccess() {
        // Firebase sign out
        auth = Firebase.auth
        auth.signOut()

        // Google revoke access
        /*googleSignInClient.revokeAccess().addOnCompleteListener(this) {
        }*/
    }


}
