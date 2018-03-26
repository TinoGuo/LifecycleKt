package me.tino.lifecyclekt

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickLifecycle.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            with(progressDialog) {
                setMessage("loading...")
                show()
            }
            progressDialog.show()
            load {
                //模拟耗时操作
                delay(10_000)
                getTaskResult()
            } then {
                progressDialog.dismiss()
                //UI操作
                textView.text = it.toString()
            }
        }

        clickView.setOnClickListener {
            customView.loadTask()
        }
    }

    private fun getTaskResult(): Int = 100
}
