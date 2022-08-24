package com.yingda.rxtools.adapter.listener;

import android.view.View;
import androidx.annotation.NonNull;

import com.yingda.rxtools.adapter.BaseQuickAdapter;


/**
 * author: chen
 * data: 2022/8/24
 * des:
*/
public interface OnItemClickListener {
    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adapter
     * @param view     The itemView within the RecyclerView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    void onItemClick(@NonNull BaseQuickAdapter<?,?> adapter, @NonNull View view, int position);
}
