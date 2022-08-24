package com.yingda.rxtools.adapter.listener;

import androidx.annotation.Nullable;

/**
 * author: chen
 * data: 2022/8/24
 * des:
*/
public interface DraggableListenerImp {

    void setOnItemDragListener(@Nullable OnItemDragListener onItemDragListener);

    void setOnItemSwipeListener(@Nullable OnItemSwipeListener onItemSwipeListener);
}
