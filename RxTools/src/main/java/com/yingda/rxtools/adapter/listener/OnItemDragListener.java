package com.yingda.rxtools.adapter.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * author: chen
 * data: 2022/8/24
 * des:
*/
public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
