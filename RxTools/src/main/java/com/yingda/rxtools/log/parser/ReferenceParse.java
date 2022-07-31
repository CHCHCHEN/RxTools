package com.yingda.rxtools.log.parser;

import com.yingda.rxtools.log.common.LogConvert;

import java.lang.ref.Reference;

/**
 * author: chen
 * data: 2022/7/31
 * des: Reference解析器
*/
public class ReferenceParse implements Parser<Reference> {
    @Override
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override
    public String parseString(Reference reference) {
        Object actual = reference.get();
        StringBuilder builder = new StringBuilder(reference.getClass().getSimpleName() + "<"
                + actual.getClass().getSimpleName() + "> {");
        builder.append("→").append(LogConvert.objectToString(actual));
        return builder.toString() + "}";
    }
}
