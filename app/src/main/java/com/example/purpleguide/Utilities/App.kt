package com.example.purpleguide.Utilities

import android.app.Application
import com.example.purpleguide.R
import com.parse.Parse




public class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(R.string.APP_ID.toString()) // if desired
                .clientKey(R.string.CLIENT_KEY.toString())
                .server(R.string.SERVER_URL.toString())
                .build()
        )
    }


}