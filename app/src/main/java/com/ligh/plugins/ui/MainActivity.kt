package com.ligh.plugins.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.cxzheng.asmtraceman.test.RandomTest
import cn.cxzheng.tracemanui.MethodTraceServerManager
import com.ligh.plugins.R
import java.util.Random

class MainActivity : AppCompatActivity() {
    private val randomTest by lazy { RandomTest() }
    private val random by lazy { Random() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MethodTraceServerManager.logLevel = MethodTraceServerManager.MTM_LOG_DETAIL

        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        findViewById<View>(R.id.btn_click).setOnClickListener {

            val randomMill = rand(0, 30)
            Toast.makeText(this, "已执行随机耗时方法", Toast.LENGTH_SHORT).show()
            randomTest.randomSleep(randomMill.toLong())
        }
    }

    private fun rand(from: Int, to: Int): Int {
        return random.nextInt(to - from) + from
    }


}