package com.sjianjun.retrofit.simplify;

import com.sjianjun.retrofit.simple.http.HttpClient;

public class JavaTest {
    public static void test() {
        HttpClient.create().get(String.class,"https://baijiahao.baidu.com/builder/theme/bjh/login");
        HttpClient.create().post(String.class,"https://baijiahao.baidu.com/builder/theme/bjh/login");
    }
}
