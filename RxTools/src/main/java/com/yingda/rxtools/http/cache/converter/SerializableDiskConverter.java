package com.yingda.rxtools.http.cache.converter;

import com.yingda.rxtools.http.utils.HttpLog;
import com.yingda.rxtools.http.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * author: chen
 * data: 2022/8/24
 * des: 序列化对象的转换器
*/
@SuppressWarnings(value={"unchecked", "deprecation"})
public class SerializableDiskConverter implements IDiskConverter {

    @Override
    public <T> T load(InputStream source, Type type) {
        //序列化的缓存不需要用到clazz
        T value = null;
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(source);
            value = (T) oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            HttpLog.e(e);
        } finally {
            Utils.close(oin);
        }
        return value;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(sink);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            HttpLog.e(e);
        } finally {
            Utils.close(oos);
        }
        return false;
    }

}
