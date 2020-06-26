package xp.level.booster

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import xp.level.booster.XpLevelBoosterApp.Companion.noInternetDialog
import xp.level.booster.activity.LoginActivity
import xp.level.booster.extensions.isNetworkAvailable
import xp.level.booster.extensions.launchActivity
import xp.level.booster.extensions.openLottieDialog
import java.util.*


open class AppBaseActivity : FirebaseConfig() {
    private var progressDialog: Dialog? = null
    var language: Locale? = null
    private var themeApp: Int = 0
    var isAdShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noInternetDialog = null
        if (progressDialog == null) {
            progressDialog = Dialog(this)
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(0))
            progressDialog?.setContentView(R.layout.custom_dialog)
        }
    }

    fun showProgress(show: Boolean) {
        when {
            show -> {
                if (!isFinishing && !progressDialog!!.isShowing) {
                    progressDialog?.setCanceledOnTouchOutside(false)
                    progressDialog?.show()
                }
            }
            else -> try {
                if (progressDialog?.isShowing!! && !isFinishing) {
                    progressDialog?.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun networkCheck() {
        if (!isNetworkAvailable()) {
            openLottieDialog {
                launchActivity<LoginActivity>()
            }
        }
    }
}
