package com.yingda.rxtools.http.subsciber;

import android.content.Context;

import com.yingda.rxtools.http.callback.CallBack;
import com.yingda.rxtools.http.callback.ProgressDialogCallBack;
import com.yingda.rxtools.http.exception.ApiException;
import com.yingda.rxtools.http.utils.HttpLog;
import com.yingda.rxtools.log.ViseLog;

import io.reactivex.annotations.NonNull;

/**
 * author: chen
 * data: 2022/8/24
 * des: 带有callBack的回调
*/
public class CallBackSubsciber<T> extends BaseSubscriber<T> {
    public CallBack<T> mCallBack;
    

    public CallBackSubsciber(Context context, CallBack<T> callBack) {
        super(context);
        mCallBack = callBack;
        if (callBack instanceof ProgressDialogCallBack) {
            ((ProgressDialogCallBack) callBack).subscription(this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }
    
    @Override
    public void onError(ApiException e) {
        if (mCallBack != null) {
            mCallBack.onError(e);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        super.onNext(t);
        try {
            if (mCallBack != null) {
                mCallBack.onSuccess(t);
            }
        } catch (Exception e) {
            ViseLog.e(t.toString());
            ViseLog.e("API访问异常"+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mCallBack != null) {
            mCallBack.onCompleted();
        }
    }
}
