package me.tino.lifecyclekt

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.view.View
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

/**
 * Manage the lifecycle of Coroutine in android
 * mailTo:guochenghaha@gmail.com
 * Created by tino on 2018 March 22, 16:34.
 */

/**
 * base on [LifecycleObserver] to manage lifecycle
 * @param deferred the suspend job
 * @param untilEvent the job should stop what event receive, default is [Lifecycle.Event.ON_DESTROY]
 */
class CoroutineLifecycleListener(
    private val deferred: Deferred<*>,
    private val untilEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) : GenericLifecycleObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event?) {
        //if the event is what you need
        if (event == untilEvent) {
            //if the job is not cancelled
            if (!deferred.isCancelled) {
                deferred.cancel()
            }
            //remove the observer
            source.lifecycle.removeObserver(this)
        }
    }
}

/**
 * base on [View.OnAttachStateChangeListener] to manage lifecycle
 * @param deferred the suspend job
 */
class CoroutineViewListener(private val deferred: Deferred<*>) : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) {
        //if the job is not cancelled
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
        //remove the listener
        v.removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View?) {
    }
}

/**
 *  extension function of [View], to load async task, [loader] must be sync
 *  Attention：if you don't want to call [then]，Please call [Deferred.start] or [Deferred.await]
 *  but why not [async]
 *  @param context default dispatcher [CommonPool]
 *  @param loader the suspend task
 *  @return the lazy task
 */
fun <T> View.load(
    context: CoroutineDispatcher = CommonPool,
    loader: suspend CoroutineScope.() -> T
): Deferred<T> {
    val deferred = async(context = context, start = CoroutineStart.LAZY) {
        loader()
    }
    this.addOnAttachStateChangeListener(CoroutineViewListener(deferred))
    return deferred
}

/**
 *  extension function of [LifecycleOwner], to load async task, [loader] must be sync
 *  Attention：if you don't want to call [then]，Please call [Deferred.start] or [Deferred.await]
 *  but why not [async]
 *  @param context default dispatcher [CommonPool]
 *  @param untilEvent the job should stop what event receive, default is [Lifecycle.Event.ON_DESTROY]
 *  @param loader the suspend task
 *  @return the lazy task
 */
fun <T> LifecycleOwner.load(
    context: CoroutineDispatcher = CommonPool,
    untilEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    loader: suspend CoroutineScope.() -> T
): Deferred<T> {
    if (untilEvent != Lifecycle.Event.ON_STOP
        && untilEvent != Lifecycle.Event.ON_PAUSE
        && untilEvent != Lifecycle.Event.ON_DESTROY) {
        throw LifecycleNotSupportException(
            "Sorry! Please use any of " +
                    "Lifecycle.Event.ON_STOP " +
                    "Lifecycle.Event.ON_PAUSE " +
                    "Lifecycle.Event.ON_DESTROY " +
                    "state!!!"
        )
    }

    val deferred = async(context = context, start = CoroutineStart.LAZY) {
        loader()
    }
    lifecycle.addObserver(CoroutineLifecycleListener(deferred, untilEvent))
    return deferred.also {
        it.invokeOnCompletion(true, true, {})
    }
}

/**
 *  the extension function of [Deferred], it's called from UI thread
 *  !!!Note: it will throw exception, if you don't want throw exception just use [then]
 *  @param block what you want to do task on UI thread
 *  @return the UI [Job]
 */
infix fun <T> Deferred<T>.thenWithException(block: (T) -> Unit): Job {
    return launch(UI) { block(this@thenWithException.await()) }
}

/**
 *  the extension function of [Deferred], it's called from UI thread, it will catch all the exception
 *  @param block what you want to do task on UI thread, and  return the result of UI thread
 *  @return the UI [Job] with the value
 */
infix fun <T, R> Deferred<T>.then(block: (T) -> R): Deferred<R> {
    return async(UI) { block(this@then.await()) }
}

/**
 * custom exception
 */
class LifecycleNotSupportException(message: String) : IllegalStateException(message)