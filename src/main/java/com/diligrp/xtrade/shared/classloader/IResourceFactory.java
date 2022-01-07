package com.diligrp.xtrade.shared.classloader;

import java.io.IOException;
import java.net.URL;

/**
 * 资源工厂接口, 利用资源加载器完成资源加载
 * 支持的资源加载器类型FileLoader JarLoader和URLLoader
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
public interface IResourceFactory {
    Resource getResource(String name);

    interface ILoader {
        URL getBaseURL();

        Resource getResource(String name);

        URL[] getClassPath() throws IOException;
    }
}
