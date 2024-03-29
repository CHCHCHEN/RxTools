package com.yingda.rxtools.http.func;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yingda.rxtools.http.model.ApiResult;
import com.yingda.rxtools.http.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * author: chen
 * data: 2022/8/24
 * des: 定义了ApiResult结果转换Func
*/
@SuppressWarnings("unchecked")
public class ApiResultFunc<T> implements Function<ResponseBody, ApiResult<T>> {
    protected Type type;
    protected Gson gson;

    public ApiResultFunc(Type type) {
        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        this.type = type;
    }

    @Override
    public ApiResult<T> apply(@NonNull ResponseBody responseBody) throws Exception {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(-1);
        if (type instanceof ParameterizedType) {//自定义ApiResult
            final Class<T> cls = (Class) ((ParameterizedType) type).getRawType();
            if (ApiResult.class.isAssignableFrom(cls)) {
                final Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                final Class clazz = Utils.getClass(params[0], 0);
                final Class rawType = Utils.getClass(type, 0);
                try {
                    String json = responseBody.string();
                    //增加是List<String>判断错误的问题
                    if (!List.class.isAssignableFrom(rawType) && clazz.equals(String.class)) {
                        apiResult.setData((T) json);
                        apiResult.setCode(0);
                       /* final Type type = Utils.getType(cls, 0);
                        ApiResult result = gson.fromJson(json, type);
                        if (result != null) {
                            apiResult = result;
                            apiResult.setData((T) json);
                        } else {
                            apiResult.setMsg("json is null");
                        }*/
                    } else {
                        ApiResult result = gson.fromJson(json, type);
                        if (result != null) {
                            apiResult = result;
                        } else {
                            apiResult.setMsg("json is null");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    apiResult.setMsg(e.getMessage());
                } finally {
                    responseBody.close();
                }
            } else {
                apiResult.setMsg("ApiResult.class.isAssignableFrom(cls) err!!");
            }
        } else {//默认Apiresult
            try {
                final String json = responseBody.string();
                final Class<T> clazz = Utils.getClass(type, 0);
                if (clazz.equals(String.class)) {
                    //apiResult.setData((T) json);
                    //apiResult.setCode(0);
                    final ApiResult result = parseApiResult(json, apiResult);
                    if (result != null) {
                        apiResult = result;
                        apiResult.setData((T) json);
                    } else {
                        apiResult.setMsg("json is null");
                    }
                } else {
                    final ApiResult result = parseApiResult(json, apiResult);
                    if (result != null) {
                        apiResult = result;
                        if (apiResult.getData() != null) {
                            T data = gson.fromJson(apiResult.getData().toString(), clazz);
                            apiResult.setData(data);
                        } else {
                            apiResult.setMsg("ApiResult's data is null");
                        }
                    } else {
                        apiResult.setMsg("json is null");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                apiResult.setMsg(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                apiResult.setMsg(e.getMessage());
            } finally {
                responseBody.close();
            }
        }
        return apiResult;
    }

    private ApiResult parseApiResult(String json, ApiResult apiResult) throws JSONException {
        if (TextUtils.isEmpty(json))
            return null;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("code")) {
            apiResult.setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
            apiResult.setData(jsonObject.getString("data"));
        }
        if (jsonObject.has("msg")) {
            apiResult.setMsg(jsonObject.getString("msg"));
        }
        return apiResult;
    }
}
