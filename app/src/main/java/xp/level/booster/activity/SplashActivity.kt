package xp.level.booster.activity

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import xp.level.booster.AppBaseActivity
import xp.level.booster.BuildConfig
import xp.level.booster.R
import xp.level.booster.extensions.launchActivity
import xp.level.booster.extensions.runDelayed
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppBaseActivity() {

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var VersionCode = "versionCode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


        mFirebaseRemoteConfig = getRemoteConfigValues()
        setRemoteConfigValues()

    }

    //region Firebase Config Method 2
    private fun setRemoteConfigValues() {
        //region Fetching Values
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val AlertTitle = mFirebaseRemoteConfig.getString("Alert_Title")
        val AlertMessage = mFirebaseRemoteConfig.getString("Alert_Message")
        val Alert_Ok_btn = mFirebaseRemoteConfig.getString("Alert_Ok_Btn")
        val Alert_No_btn = mFirebaseRemoteConfig.getString("Alert_No_Btn")
        val Rate_Text = mFirebaseRemoteConfig.getString("rate_us")
        val versionCode = BuildConfig.VERSION_CODE
        //endregion
        System.out.println(versionCode)
        if (remoteCodeVersion > versionCode) {

            val versionCode = BuildConfig.VERSION_CODE
            if (remoteCodeVersion > versionCode) {
                val dialogBuilder = AlertDialog.Builder(this)


                // set message of alert dialog
                dialogBuilder.setMessage(AlertMessage)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(Alert_Ok_btn, DialogInterface.OnClickListener { _, _ ->
                        val uri =
                            Uri.parse("market://details?id=" + this@SplashActivity.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@SplashActivity.packageName)
                                )
                            )
                        }
                    })
                    // negative button text and action
                    .setNegativeButton(Alert_No_btn, // do something when the button is clicked
                        DialogInterface.OnClickListener { _, _ ->
                            finishAffinity()
                        })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(AlertTitle)
                // show alert dialog
                alert.show()
            }
            //  main_layout!!.setBackgroundColor(Color.parseColor(remoteValueText))
        } else {
            runDelayed(3000) {
                launchActivity<LoginActivity>()
                finish()

            }
        }
    }
    //endregion

    //region Firebase Config Method 3
    override fun onStart() {

        super.onStart()
        mFirebaseRemoteConfig = getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        }
        //endregion
    }
    //endregion

    override fun onBackPressed() {
        finishAffinity()
        finish()
    }

    override fun onResume() {


        mFirebaseRemoteConfig = getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong(VersionCode)
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        }
        super.onResume()
    }

}


