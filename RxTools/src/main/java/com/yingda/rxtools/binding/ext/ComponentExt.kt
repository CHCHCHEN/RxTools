package com.yingda.rxtools.binding

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.yingda.rxtools.binding.databind.*
import com.yingda.rxtools.binding.viewbind.*


inline fun <reified T : ViewDataBinding> Fragment.databind() =
    FragmentDataBinding<T>(T::class.java, this)

inline fun <reified T : ViewDataBinding> Fragment.databind(noinline block: T.() -> Unit) =
    FragmentDataBinding<T>(T::class.java, this, block = block)

inline fun <reified T : ViewDataBinding> Activity.databind(@LayoutRes resId: Int) =
    ActivityDataBinding<T>(this, resId)

inline fun <reified T : ViewDataBinding> Dialog.databind(
    @LayoutRes resId: Int
) = DialogDataBinding(
    classes = T::class.java,
    inflater = this.layoutInflater,
    resId = resId,
)

inline fun <reified T : ViewDataBinding> Activity.databind(
    @LayoutRes resId: Int,
    noinline block: T.() -> Unit
) = ActivityDataBinding<T>(this, resId, block)

inline fun <reified T : ViewDataBinding> Dialog.databind(
    @LayoutRes resId: Int,
    noinline block: T.() -> Unit
) = DialogDataBinding(
    classes = T::class.java,
    inflater = this.layoutInflater,
    resId = resId,
    block = block,
)

inline fun <reified T : ViewDataBinding> Dialog.databind(
    @LayoutRes resId: Int,
    lifecycle: Lifecycle
) = DialogDataBinding(
    classes = T::class.java,
    inflater = this.layoutInflater,
    resId = resId,
    lifecycle = lifecycle
)

inline fun <reified T : ViewDataBinding> RecyclerView.ViewHolder.databind() =
    ViewHolderDataBinding(T::class.java)

inline fun <reified T : ViewDataBinding> RecyclerView.ViewHolder.databind(noinline block: (T.() -> Unit)) =
    ViewHolderDataBinding(T::class.java, block)

inline fun <reified T : ViewBinding> ViewGroup.databind(@LayoutRes resId: Int) =
    ViewGroupDataBinding(
        classes = T::class.java,
        resId = resId,
        inflater = LayoutInflater.from(getContext())
    )

inline fun <reified T : ViewBinding> ViewGroup.databind(
    @LayoutRes resId: Int,
    noinline block: (T.() -> Unit)
) = ViewGroupDataBinding(
    classes = T::class.java,
    resId = resId,
    inflater = LayoutInflater.from(getContext()),
    viewGroup = this,
    block = block
)

inline fun <reified T : ViewBinding> Activity.viewbind() =
    ActivityViewBinding(T::class.java, this)

//inline fun <reified T : ViewBinding> AppCompatActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)
//
//inline fun <reified T : ViewBinding> FragmentActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)

inline fun <reified T : ViewBinding> Fragment.viewbind() =
    FragmentViewBinding(T::class.java, this)

inline fun <reified T : ViewBinding> Dialog.viewbind() =
    DialogViewBinding(T::class.java)

inline fun <reified T : ViewBinding> Dialog.viewbind(lifecycle: Lifecycle) =
    DialogViewBinding(T::class.java, lifecycle)

inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.viewbind() =
    ViewHolderViewBinding(T::class.java)

inline fun <reified T : ViewBinding> ViewGroup.viewbind() = ViewGroupViewBinding(
    classes = T::class.java,
    inflater = LayoutInflater.from(getContext())
)

inline fun <reified T : ViewBinding> ViewGroup.viewbind(viewGroup: ViewGroup) =
    ViewGroupViewBinding(
        classes = T::class.java,
        inflater = LayoutInflater.from(getContext()),
        viewGroup = viewGroup
    )