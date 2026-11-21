package com.inuappcenter.gravit

import android.app.Application
import com.inuappcenter.gravit.api.RetrofitInstance

class GravitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RetrofitInstance.init(this)
    }
}