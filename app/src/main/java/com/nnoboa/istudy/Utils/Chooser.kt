package com.nnoboa.istudy.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class Chooser {
    public final val REQUEST_CODE = 2217;

    public final fun startChooser(context :Context) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        (context as Activity).startActivityForResult(intent,REQUEST_CODE, Bundle())
        context.onActivityReenter(REQUEST_CODE,intent)
    }
}