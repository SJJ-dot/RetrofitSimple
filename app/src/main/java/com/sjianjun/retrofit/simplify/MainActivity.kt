package com.sjianjun.retrofit.simplify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sjj.alog.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            try {
//                val resp1 = HttpClient().post<Resp>("https://www.biquge5200.cc/95_95192/")
//                Log.e(resp1)
//                val resp = HttpClient().get<String>("https://www.biquge5200.cc/95_95192/")
//                Log.e(resp)
                JavaTest.test()
            } catch (e: Throwable) {
                Log.e(e,e)
            }
        }


    }

    data class Resp(var result: Result? = null)

    data class Result(var status: Status? = null)

    data class Status(var code: Int = 0, var msg: String? = null)
}
