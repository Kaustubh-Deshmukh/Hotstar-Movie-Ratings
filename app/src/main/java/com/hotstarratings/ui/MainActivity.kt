package com.hotstarratings.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.hotstarratings.R
import com.hotstarratings.services.AccessScreenService


class MainActivity : AppCompatActivity() {

    companion object {
        val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + packageName))
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                startService(Intent(this@MainActivity, AccessScreenService::class.java))
            } else { //Permission is not available
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
