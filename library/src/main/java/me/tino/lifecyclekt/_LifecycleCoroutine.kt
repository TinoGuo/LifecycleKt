package me.tino.lifecyclekt

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
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
 */
class CoroutineLifecycleListener(private val deferred: Deferred<*>) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
    }
}

/**
 * base on [View.OnAttachStateChangeListener] to manage lifecycle
 */
class CoroutineViewListener(private val deferred: Deferred<*>) : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View?) {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
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
 *  @param loader the suspend task
 *  @return the lazy task
 */
fun <T> LifecycleOwner.load(
    context: CoroutineDispatcher = CommonPool,
    loader: suspend CoroutineScope.() -> T
): Deferred<T> {
    val deferred = async(context = context, start = CoroutineStart.LAZY) {
        loader()
    }
    lifecycle.addObserver(CoroutineLifecycleListener(deferred))
    return deferred
}

/**
 *  the extension function of [Deferred], it's called from UI thread
 *  @param block what you want to do task on UI thread
 *  @return the UI [Job]
 */
infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
    return launch(UI) { block(this@then.await()) }
}

/**
 *  the extension function of [Deferred], it's called from UI thread, it will catch all the exception
 *  @param block what you want to do task on UI thread, and  return the result of UI thread
 *  @return the UI [Job]
 */
infix fun <T, R> Deferred<T>.then(block: (T) -> R): Deferred<R> {
    return async(UI) { block(this@then.await()) }
}