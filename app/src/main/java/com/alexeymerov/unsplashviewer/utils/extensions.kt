package com.alexeymerov.unsplashviewer.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.AnimRes
import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.alexeymerov.unsplashviewer.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


inline fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    editor.apply()
}

fun Activity.hideKeyboardEx() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.apply { imm.hideSoftInputFromWindow(windowToken, 0) }
}

fun FragmentManager.replaceFragment(containerViewId: Int, fragment: Fragment, addToBackStack: Boolean, needAnimate: Boolean) {
    var ft = this.beginTransaction()
    val fragmentName = fragment.javaClass.simpleName
    if (addToBackStack) ft = ft.addToBackStack(fragmentName)
    if (needAnimate) ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.pop_out_right, R.animator.pop_in_left)
    ft.replace(containerViewId, fragment, fragmentName).commit()
}

fun Context.showToast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()

fun View.showToast(text: Any) = Toast.makeText(this.context, text.toString(), Toast.LENGTH_SHORT).show()

inline fun Activity.showSnack(message: String, isLong: Boolean = false, f: Snackbar.() -> Unit = {}) {
    (this.findViewById<View>(android.R.id.content))?.showSnack(message, isLong, f)
}

inline fun View.showSnack(message: String, isLong: Boolean = false, f: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, "<font color=\"#549bdf\">$message</font>".fromHtml(), if (isLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
    snack.f()
    snack.show()
}

@SuppressLint("InlinedApi")
@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    else -> Html.fromHtml(this)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

fun Context.getColorEx(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline fun EditText.textWatcher(f: EditTextWatcher.() -> Unit) = addTextChangedListener(EditTextWatcher().apply(f))

class EditTextWatcher : TextWatcher {

    var before: (String.() -> Unit)? = null
    var onChanged: (String.() -> Unit)? = null
    var after: (String.() -> Unit?)? = null

    fun before(f: String.() -> Unit) {
        before = f
    }

    fun onChanged(f: String.() -> Unit = {}) {
        onChanged = f
    }

    fun after(f: String.() -> Unit = {}) {
        after = f
    }

    override fun afterTextChanged(s: Editable?) {
        s?.toString()?.apply { after?.invoke(this) }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        s?.toString()?.apply { before?.invoke(this) }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.toString()?.apply { onChanged?.invoke(this) }
    }
}

fun Activity.copyToClipBoard(text: String, label: String = "simpleLabel") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.primaryClip = ClipData.newPlainText(label, text)
}

fun View.hideDown() {
    animate().translationY(this.height.toFloat()).alpha(0f).duration = 175
}

fun View.showUp() {
    animate().translationYBy(-this.height.toFloat()).alpha(1f).duration = 175
}

fun View.isVisible() = visibility == VISIBLE

fun View.playAnimation(@AnimRes resId: Int, onEnd: () -> Unit = {}) {
    startAnimation(AnimationUtils.loadAnimation(this.context, resId).apply {
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                onEnd.invoke()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    })
}

fun postDelayedMain(millis: Long, f: () -> Unit) = postDelayed(millis, true) { f.invoke() }

fun postDelayed(millis: Long, onMainThread: Boolean = false, f: () -> Unit) {
    val handler = if (onMainThread) Handler(Looper.getMainLooper()) else Handler()
    handler.postDelayed({ f.invoke() }, millis)
}

fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)


fun View.setCircleColor(colorHexString: String) = setRoundedBackground(Color.parseColor(colorHexString))
fun View.setRoundedBackground(color: Int) {
    when (this.background) {
        is GradientDrawable -> (this.background as GradientDrawable).setColor(color)
        else -> this.background.setColorFilter(color, PorterDuff.Mode.OVERLAY)
    }
}

fun <T> T.or(any: T) = if (Random().nextBoolean()) this else any

interface AutoUpdatableAdapter<in T> {
    fun RecyclerView.Adapter<*>.autoNotify(oldList: List<T>, newList: List<T>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int) = compareItems(oldList[oldPosition], newList[newPosition])
            override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = oldList[oldPosition] == newList[newPosition]
            override fun getOldListSize() = oldList.size
            override fun getNewListSize() = newList.size
            override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? = compareContent(oldList[oldPosition], newList[newPosition])

        }).dispatchUpdatesTo(this)
    }

    fun compareItems(old: T, new: T): Boolean

    fun compareContent(old: T, new: T): Any? = null
}

interface AutoUpdatableAdapterSet<T> {
    fun RecyclerView.Adapter<*>.autoNotifySet(oldSet: LinkedHashSet<T>, newSet: LinkedHashSet<T>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int) = compareItems(oldSet.elementAt(oldPosition), newSet.elementAt(newPosition))
            override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = oldSet.elementAt(oldPosition) == newSet.elementAt(newPosition)
            override fun getOldListSize() = oldSet.size
            override fun getNewListSize() = newSet.size
            override fun getChangePayload(oldPosition: Int, newPosition: Int) = compareContent(oldSet.elementAt(oldPosition), newSet.elementAt(newPosition))

        }).dispatchUpdatesTo(this)
    }

    fun compareItems(old: T, new: T): Boolean

    fun compareContent(old: T, new: T): LinkedHashSet<T>? = null
}

inline fun doOnRxAsync(millis: Long = 0, crossinline f: () -> Unit) {
    Observable.create<Any> { f.invoke() }
            .delaySubscription(millis, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
}


