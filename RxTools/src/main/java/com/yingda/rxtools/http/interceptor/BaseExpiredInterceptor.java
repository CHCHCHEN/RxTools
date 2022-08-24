package com.yingda.rxtools.http.interceptor;

import com.yingda.rxtools.http.utils.HttpLog;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static com.yingda.rxtools.http.utils.HttpUtil.UTF8;

/**
 * author: chen
 * data: 2022/8/24
 * des: 判断响应是否有效的处理
*/
public abstract class BaseExpiredInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String bodyString = buffer.clone().readString(charset);
        HttpLog.i("网络拦截器:" + bodyString + " host:" + request.url().toString());
        boolean isText = isText(contentType);
        if (!isText) {
            return response;
        }
        //判断响应是否过期（无效）
        if (isResponseExpired(response, bodyString)) {
            return responseExpired(chain, bodyString);
        }
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType == null)
            return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理响应是否有效
     */
    public abstract boolean isResponseExpired(Response response, String bodyString);

    /**
     * 无效响应处理
     */
    public abstract Response responseExpired(Chain chain, String bodyString);
}
