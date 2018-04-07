package me.tino.lifecyclekt

import android.app.ProgressDialog
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.delay
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickLifecycle.setOnClickListener {
            load(untilEvent = Lifecycle.Event.ON_DESTROY) {
                Timber.e("start simulate IO")
                //模拟耗时操作
                delay(1_000)
                getTaskResult()
            } complete {
                if (it != null) {
                    Timber.e(it, "complete with error")
                } else {
                    Timber.e("complete without error")
                }
            } then {
                Timber.e("start UI")
                //UI操作
                textView.text = it.toString()
            }
        }

        clickView.setOnClickListener {
            customView.loadTask(5)
        }
    }

    private fun getTaskResult(): Int = 100
}
