package com.yingda.rxtools.adapter.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.yingda.rxtools.adapter.BaseQuickAdapter;

/**
 * author: chen
 * data: 2022/8/24
 * des:
*/
public interface OnItemChildClickListener {
    /**
     * callback method to be invoked when an item child in this view has been click
     *
     * @param adapter  BaseQuickAdapter
     * @param view     The view whihin the ItemView that was clicked
     * @param position The position of the view int the adapter
     */
    void onItemChildClick(@NonNull BaseQuickAdapter<?,?> adapter, @NonNull View view, int position);
}
