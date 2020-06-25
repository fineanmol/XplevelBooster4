package xp.level.booster

import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class XpLevelBoosterApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        // Set Custom Font
        CalligraphyConfig.initDefault(

            CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_regular))
                .setFontAttrId(
                    R.attr.fontPath
                ).build()
        )
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        var noInternetDialog: Dialog? = null
        private lateinit var appInstance: XpLevelBoosterApp
        fun getAppInstance(): XpLevelBoosterApp {
            return appInstance
        }

    }


}