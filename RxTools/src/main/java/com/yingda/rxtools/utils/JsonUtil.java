package com.yingda.rxtools.utils;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonParser;

import java.util.List;

public class JsonUtil {

	/**
	 * 把对象转成json字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		String jsonStr = JSON.toJSONString(obj);
		return jsonStr.replaceAll("\"oID\"", "\"OID\"");
	}

	/**
	 * 把json字符串转成对象
	 *
	 * @param type
	 * @param cs
	 * @return
	 */
	public static <T> T fromJson(Class<T> type, CharSequence cs) {
		return JSON.parseObject(cs + "", type);

	}

	/**
	 *
	 * @param eleType
	 * @param cs
	 * @return
	 */

	public static <T> List<T> fromJsonAsList(Class<T> eleType, CharSequence cs) {
		return JSON.parseArray(cs + "", eleType);
	}


	/**
	 * 判断是否为json结构
	 * @param json
	 * @return
	 */
	public static boolean isGoodJson(String json) {
		if (TextUtils.isEmpty(json)) {
			return false;
		}

		try {
			new JsonParser().parse(json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
