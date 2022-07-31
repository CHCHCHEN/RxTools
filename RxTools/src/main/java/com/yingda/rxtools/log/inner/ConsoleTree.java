package com.yingda.rxtools.log.inner;

/**
 * author: chen
 * data: 2022/7/31
 * des: 系统打印树-输出日志信息到控制台
*/
public class ConsoleTree extends Tree {
    @Override
    protected void log(int type, String tag, String message) {
        System.out.println(tag + "\t" + message);
    }
}
