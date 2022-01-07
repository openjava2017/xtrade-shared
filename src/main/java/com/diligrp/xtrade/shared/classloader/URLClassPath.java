package com.diligrp.xtrade.shared.classloader;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 用于维护加载类的搜索路径, URL格式:
 *
 * file:/your_path/my.jar
 * file:/your_path/
 * jar:file:/your_path/my.jar!/
 * jar:http://www.hostname.com/your_path/my.jar!/
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
class URLClassPath {
    // 所有搜索路径
    private final List<URL> urls;
    // 待处理的搜索路径
    private final Deque<URL> pendingUrls;

    public URLClassPath(URL[] urls) {
        this.urls = new ArrayList<>(urls.length);
        this.pendingUrls = new ArrayDeque<>(urls.length);

        for (URL url : urls) {
            this.urls.add(url);
            this.pendingUrls.add(url);
        }
    }

    public void addLast(URL url) {
        synchronized (pendingUrls) {
            if (!this.urls.contains(url)) {
                this.pendingUrls.addLast(url);
                this.urls.add(url);
            }
        }
    }

    public void addFirst(URL[] urls) {
        synchronized (pendingUrls) {
            for (int i = urls.length - 1; i >= 0; --i) {
                pendingUrls.addFirst(urls[i]);
            }
        }
    }

    public URL pollFirst() {
        synchronized (pendingUrls) {
            return this.pendingUrls.pollFirst();
        }
    }
}
