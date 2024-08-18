package com.yingda.rxtools.oaid.impl;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;

/**
 * author: chen
 * data: 2024/8/18
 * des:
 */
class DefaultImpl implements IOAID {

    @Override
    public boolean supported() {
        return false;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (getter == null) {
            return;
        }
        getter.onOAIDGetError(new OAIDException("Unsupported"));
    }

}
