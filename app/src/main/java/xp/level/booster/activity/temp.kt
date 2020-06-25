package xp.level.booster.activity

import xp.level.booster.AppBaseActivity

class temp : AppBaseActivity()
/*
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
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
        setupAppFunctions()
    }

    private fun setupAppFunctions() {
        ratingDialog.onClick {
            showRatingDialog()
        }
        feedBack.onClick {
            showFeedbackDialog(this@MainActivity)

        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(this)
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
                    override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                        if (billingResponseCode == BillingClient.BillingResponse.OK) {
                            println("BILLING | startConnection | RESULT OK")
                            //progressBar2.visibility=View.GONE
                            onLoadProductsClicked()
                        } else {
                            println("BILLING | startConnection | RESULT: $billingResponseCode")
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
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
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
                    billingClient.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                        if (responseCode == BillingClient.BillingResponse.OK) {
                            println("querySkuDetailsAsync, responseCode: $responseCode")
                            initProductAdapter(skuDetailsList)
                        } else {
                            println("Can't querySkuDetailsAsync, responseCode: $responseCode")
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


            */
/**
 *
 *//*
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

*//*    private fun clearHistory() {
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
    }*//*

            override fun onPurchaseHistoryResponse(
                billingResult: BillingResult,
                purchaseHistoryList: MutableList<PurchaseHistoryRecord>?
            ) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (!(purchaseHistoryList).isNullOrEmpty()) {
                        for (purchase in purchaseHistoryList) {
                            *//*for (books in booksList) {
                                if (purchase.sku == books.id) {
                                    books.isPurchased = true
                                }
                            }*//*
                        }
                    }
                }
            }


            *//*override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
            override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
                println("onPurchasesUpdated: $responseCode")
                allowMultiplePurchases(purchases)

                toast("onPurchasesUpdated:$responseCode")
                if (responseCode == 0) {
                    //signIn()
                    loader.visibility = View.VISIBLE
                    unlockStatus.visibility = View.VISIBLE
                    buttonTap.addAnimatorListener(object :
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
                            buttonTap.visibility = View.INVISIBLE
                            buttonTap.removeAllAnimatorListeners()
                        }
                    })
                    trophy.visibility = View.VISIBLE
                    trophy.repeatCount = 10
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
                            buttonTap.visibility = View.VISIBLE
                            loader.visibility = View.INVISIBLE
                            unlockStatus.visibility = View.INVISIBLE
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

            }*//*
        }

        private fun allowMultiplePurchases(purchases: MutableList<Purchase>?) {
            val purchase = purchases?.first()
            if (purchase != null) {
                billingClient.consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken ->
                    if (responseCode == BillingClient.BillingResponse.OK && purchaseToken != null) {
                        println("AllowMultiplePurchases success, responseCode: $responseCode")
                        toast("MultiplePurchase:$responseCode")
                    } else {
                        println("Can't allowMultiplePurchases, responseCode: $responseCode")
                    }
                }
            }
        }

        private fun clearHistory() {
            billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
                .forEach {
                    billingClient.consumeAsync(it.purchaseToken) { responseCode, purchaseToken ->
                        if (responseCode == BillingClient.BillingResponse.OK && purchaseToken != null) {
                            println("onPurchases Updated consumeAsync, purchases token removed: $purchaseToken")
                        } else {
                            println("onPurchases some troubles happened: $responseCode")
                        }
                    }
                }
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
            auth.signOut()

            // Google revoke access
            googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            }
        }


x        signOut()
        super.onBackPressed()


    }
}
*/