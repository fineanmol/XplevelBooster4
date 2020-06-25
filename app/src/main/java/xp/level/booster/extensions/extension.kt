package xp.level.booster.extensions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.thekhaeng.pushdownanim.PushDownAnim
import com.thekhaeng.pushdownanim.PushDownAnim.*
import kotlinx.android.synthetic.main.dialog_no_internet.*
import xp.level.booster.R
import xp.level.booster.XpLevelBoosterApp.Companion.getAppInstance
import xp.level.booster.XpLevelBoosterApp.Companion.noInternetDialog
import java.util.*

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkAvailable(): Boolean {
    val info = getAppInstance().getConnectivityManager().activeNetworkInfo
    return info != null && info.isConnected
}

fun Context.getConnectivityManager() =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

inline fun <T : View> T.onClick(crossinline func: T.() -> Unit) = setOnClickListener { func() }

fun runDelayed(delayMillis: Long = 200, action: () -> Unit) =
    Handler().postDelayed(Runnable(action), delayMillis)


inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

fun Activity.snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(findViewById(android.R.id.content), msg, length).setTextColor(Color.WHITE).show()


fun Activity.snackBarError(msg: String) {
    val snackBar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
    val sbView = snackBar.view
    sbView.setBackgroundColor(getAppInstance().resources.getColor(R.color.tomato));snackBar.setTextColor(
        Color.WHITE
    );snackBar.show()
}

fun Activity.toast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

fun Activity.toast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, stringRes, duration).show()


fun Snackbar.setTextColor(color: Int): Snackbar {
    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    tv.setTextColor(color)
    return this
}

enum class JsonFileCode {
    NO_INTERNET,
    LOADER
}

fun Activity.openLottieDialog(
    jsonFileCode: JsonFileCode = JsonFileCode.NO_INTERNET,
    onLottieClick: () -> Unit
) {
    val jsonFile: String = when (jsonFileCode) {
        JsonFileCode.NO_INTERNET -> "lottie/no_internet.json"
        JsonFileCode.LOADER -> "lottie/loader.json"
    }

    if (noInternetDialog == null) {
        noInternetDialog = Dialog(this, R.style.FullScreenDialog)
        noInternetDialog?.setContentView(R.layout.dialog_no_internet); noInternetDialog?.setCanceledOnTouchOutside(
            false
        ); noInternetDialog?.setCancelable(false)
        noInternetDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        noInternetDialog?.rlLottie?.onClick {
            if (!isNetworkAvailable()) {
                snackBarError(getAppInstance().getString(R.string.error_no_internet))
                return@onClick
            }
            noInternetDialog?.dismiss()
            onLottieClick()
        }
    }
    noInternetDialog?.lottieNoInternet?.setAnimation(jsonFile)
    if (!noInternetDialog!!.isShowing) {
        noInternetDialog?.show()
    }

}


fun Activity.showFeedbackDialog(activity: Activity) {
    val alertDialog: androidx.appcompat.app.AlertDialog
    val builder =
        androidx.appcompat.app.AlertDialog.Builder(activity)
    val view =
        LayoutInflater.from(activity).inflate(R.layout.dialog_feedback, null)
    builder.setView(view)
    val edt_feedback = view.findViewById<EditText>(R.id.edt_feedback)
    val btn_submit = view.findViewById<TextView>(R.id.btn_submit)
    val btn_cancel = view.findViewById<TextView>(R.id.btn_cancel)
    alertDialog = builder.create()
    Objects.requireNonNull(alertDialog.window)!!.attributes.windowAnimations =
        R.style.DialogAnimationTheme
    setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f)
        .setDurationPush(DEFAULT_PUSH_DURATION)
        .setDurationRelease(DEFAULT_RELEASE_DURATION)
    alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

    alertDialog.show()
    btn_cancel.setOnClickListener { v: View? -> alertDialog.dismiss() }
    btn_submit.setOnClickListener { v: View? ->
        if (!TextUtils.isEmpty(edt_feedback.text.toString())) {
            alertDialog.dismiss()
            sendFeedbackFromUser(activity, edt_feedback.text.toString())
        } else {
            toast("" + activity.getString(R.string.empty_feedback), Toast.LENGTH_SHORT)
        }
    }
}

fun Activity.sendFeedbackFromUser(activity: Activity, text: String) {
    val mailto = "mailto:" + activity.getString(R.string.feedback_mail) +
            "?cc=" + "" +
            "&subject=" + Uri.encode(activity.getString(R.string.app_name)) +
            "&body=" + Uri.encode(text)
    val emailIntent = Intent(Intent.ACTION_SENDTO)
    emailIntent.data = Uri.parse(mailto)

    try {
        activity.startActivity(emailIntent)
    } catch (ignored: ActivityNotFoundException) {
        snackBar(ignored.message.toString())
    }


}

fun Activity.showRatingDialog() {
    val alert_dialog: AlertDialog
    val builder = AlertDialog.Builder(this)
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
    builder.setView(view)
    val rating_bar = view.findViewById<RatingBar>(R.id.rating_bar)
    val btn_submit = view.findViewById<TextView>(R.id.btn_submit)
    val tv_no = view.findViewById<TextView>(R.id.tv_no)
    PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f)
        .setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION)

    alert_dialog = builder.create()
    alert_dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    alert_dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTheme
    alert_dialog.show()

    btn_submit.setOnClickListener { v: View? ->
        if (rating_bar.rating >= 3) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=\$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=\$appPackageName")
                    )
                )
            }
            alert_dialog.dismiss()
        } else if (rating_bar.rating <= 0) {
            toast("" + getString(R.string.rating_error), Toast.LENGTH_SHORT)
        } else if (rating_bar.rating < 3 && rating_bar.rating > 0) {
            alert_dialog.dismiss()
            snackBar("${rating_bar.rating} star Submitted")
        }

    }
    tv_no.setOnClickListener { v: View? -> alert_dialog.dismiss() }

}
