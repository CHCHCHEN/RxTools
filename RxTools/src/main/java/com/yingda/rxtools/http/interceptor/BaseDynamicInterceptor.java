package com.yingda.rxtools.http.interceptor;

import com.yingda.rxtools.http.utils.HttpLog;
import com.yingda.rxtools.http.utils.HttpUtil;
import com.yingda.rxtools.http.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import static com.yingda.rxtools.http.utils.HttpUtil.UTF8;

/**
 * author: chen
 * data: 2022/8/24
 * des: 动态拦截器
*/
@SuppressWarnings(value={"unchecked", "deprecation"})
public abstract class BaseDynamicInterceptor<R extends BaseDynamicInterceptor> implements Interceptor {
    private HttpUrl httpUrl;

    private boolean isSign = false;    //是否需要签名
    private boolean timeStamp = false;    //是否需要追加时间戳
    private boolean accessToken = false;    //是否需要添加token

    public BaseDynamicInterceptor() {
    }

    public boolean isSign() {
        return isSign;
    }

    public R sign(boolean sign) {
        isSign = sign;
        return (R) this;
    }

    public boolean isTimeStamp() {
        return timeStamp;
    }

    public R timeStamp(boolean timeStamp) {
        this.timeStamp = timeStamp;
        return (R) this;
    }

    public R accessToken(boolean accessToken) {
        this.accessToken = accessToken;
        return (R) this;
    }

    public boolean isAccessToken() {
        return accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.method().equals("GET")) {
            this.httpUrl = HttpUrl.parse(parseUrl(request.url().url().toString()));
            request = addGetParamsSign(request);
        } else if (request.method().equals("POST")) {
            this.httpUrl = request.url();
            request = addPostParamsSign(request);
        }
        return chain.proceed(request);
    }

    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    //get 添加签名和公共动态参数
    private Request addGetParamsSign(Request request) throws UnsupportedEncodingException {
        HttpUrl httpUrl = request.url();
        HttpUrl.Builder newBuilder = httpUrl.newBuilder();

        //获取原有的参数
        Set<String> nameSet = httpUrl.queryParameterNames();
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(nameSet);
        TreeMap<String, String> oldparams = new TreeMap<>();
        for (int i = 0; i < nameList.size(); i++) {
            String value = httpUrl.queryParameterValues(nameList.get(i)) != null && httpUrl.queryParameterValues(nameList.get(i)).size() > 0 ? httpUrl.queryParameterValues(nameList.get(i)).get(0) : "";
            oldparams.put(nameList.get(i), value);
        }
        String nameKeys = Collections.singletonList(nameList).toString();
        //拼装新的参数
        TreeMap<String, String> newParams = dynamic(oldparams);
        Utils.checkNotNull(newParams, "newParams==null");
        for (Map.Entry<String, String> entry : newParams.entrySet()) {
            String urlValue = URLEncoder.encode(entry.getValue(), UTF8.name());
            //原来的URl: https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
            if (!nameKeys.contains(entry.getKey())) {//避免重复添加
                newBuilder.addQueryParameter(entry.getKey(), urlValue);
            }
        }

        httpUrl = newBuilder.build();
        request = request.newBuilder().url(httpUrl).build();
        return request;
    }

    //post 添加签名和公共动态参数
    private Request addPostParamsSign(Request request) throws UnsupportedEncodingException {
        if (request.body() instanceof FormBody) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            FormBody formBody = (FormBody) request.body();

            //原有的参数
            TreeMap<String, String> oldparams = new TreeMap<>();
            for (int i = 0; i < formBody.size(); i++) {
                oldparams.put(formBody.encodedName(i), formBody.encodedValue(i));
            }

            //拼装新的参数
            TreeMap<String, String> newParams = dynamic(oldparams);
            Utils.checkNotNull(newParams, "newParams==null");
            //Logc.i("======post请求参数===========");
            for (Map.Entry<String, String> entry : newParams.entrySet()) {
                String value = URLDecoder.decode(entry.getValue(), UTF8.name());
                bodyBuilder.addEncoded(entry.getKey(), value);
                //Logc.i(entry.getKey() + " -> " + value);
            }
            String url = HttpUtil.createUrlFromParams(httpUrl.url().toString(), newParams);
            HttpLog.i(url);
            formBody = bodyBuilder.build();
            request = request.newBuilder().post(formBody).build();
        } else if (request.body() instanceof MultipartBody) {
            MultipartBody multipartBody = (MultipartBody) request.body();
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            List<MultipartBody.Part> oldparts = multipartBody.parts();

            //拼装新的参数
            List<MultipartBody.Part> newparts = new ArrayList<>();
            newparts.addAll(oldparts);
            TreeMap<String, String> oldparams = new TreeMap<>();
            TreeMap<String, String> newParams = dynamic(oldparams);
            for (Map.Entry<String, String> stringStringEntry : newParams.entrySet()) {
                MultipartBody.Part part = MultipartBody.Part.createFormData(stringStringEntry.getKey(), stringStringEntry.getValue());
                newparts.add(part);
            }
            for (MultipartBody.Part part : newparts) {
                bodyBuilder.addPart(part);
            }
            multipartBody = bodyBuilder.build();
            request = request.newBuilder().post(multipartBody).build();
        }
        return request;
    }

    //解析前：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
    //解析后：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult
    private String parseUrl(String url) {
        if (!"".equals(url) && url.contains("?")) {// 如果URL不是空字符串
            url = url.substring(0, url.indexOf('?'));
        }
        return url;
    }


    /**
     * 动态处理参数
     *
     * @param dynamicMap
     * @return 返回新的参数集合
     */
    public abstract TreeMap<String, String> dynamic(TreeMap<String, String> dynamicMap);
}
