package com.yingda.rxtools.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


const val INFLATE_NAME = "inflate"
const val BIND_NAME = "bind"

fun <T> Class<T>.inflateMethod() = getMethod(INFLATE_NAME, LayoutInflater::class.java)

fun <T> Class<T>.inflateMethodWithViewGroup() =
    getMethod(INFLATE_NAME, LayoutInflater::class.java, ViewGroup::class.java)

fun <T> Class<T>.bindMethod() = getMethod(BIND_NAME, View::class.java)