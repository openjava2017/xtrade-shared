package com.diligrp.xtrade.shared.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 资源工厂实现
 *
 * 支持的资源加载器类型FileLoader JarLoader和URLLoader
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
class URLResourceFactory implements IResourceFactory {
    // 类路径
    private final URLClassPath classPath;
    // 加载器列表
    private final List<ILoader> loaders = new ArrayList<>();
    // URL与Loader映射
    private final Map<String, ILoader> loaderMap = new HashMap<>();

    public URLResourceFactory(URLClassPath classPath) {
        this.classPath = classPath;
    }

    @Override
    public Resource getResource(String name) {
        ILoader loader;
        for (int i = 0; (loader = getLoader(i)) != null; i++) {
            Resource res = loader.getResource(name);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> findResources(String name) {
        return new Enumeration<>() {
            private int index = 0;
            private URL url = null;

            private boolean next() {
                if (url != null) {
                    return true;
                } else {
                    ILoader loader;
                    while ((loader = getLoader(index++)) != null) {
                        Resource resource = loader.getResource(name);
                        if (resource != null && (url = resource.getURL()) != null) {
                            return true;
                        }
                    }
                    return false;
                }
            }

            public boolean hasMoreElements() {
                return next();
            }

            public URL nextElement() {
                if (!next()) {
                    throw new NoSuchElementException();
                }
                URL u = url;
                url = null;
                return u;
            }
        };
    }

    private synchronized ILoader getLoader(int index) {
        while (loaders.size() < index + 1) {
            URL url = classPath.pollFirst();
            if (url == null) return null;

            try {
                // 已解析过的URL忽略
                String hashKey = hashKey(url);
                if(loaderMap.containsKey(hashKey)) {
                    continue;
                }

                ILoader loader = getLoader(url);
                // 如果加载器本身(Jar文件)就定义了class path，设置成下一个解析路径;
                URL[] urls = loader.getClassPath();
                if (urls != null) {
                    classPath.addFirst(urls);
                }
                loaders.add(loader);
                loaderMap.put(hashKey, loader);
            } catch (IOException ioe) {
                // Silently ignore for now...
                continue;
            } catch (Exception ex) {
                System.err.println("Failed to access " + url + ", " + ex);
            }
        }
        return loaders.get(index);
    }

    private ILoader getLoader(final URL url) throws IOException {
        String protocol = url.getProtocol();  // lower cased in URL
        String file = url.getFile();
        if (file != null && file.endsWith("/")) {
            if ("file".equals(protocol)) {
                // 处理URL格式 file:/your_path/
                return new Loaders.FileLoader(url);
            } else if ("jar".equals(protocol) && file.endsWith("!/")) {
                // 处理URL格式 jar:file:/your_path/my.jar!/和jar:http://www.hostname.com/your_path/my.jar!/
                // jar:file:/your_path/my.jar!/ 将被转换成 file:/your_path/my.jar; jar:http://***将被保留原有格式
                URL nestedUrl = new URL(file.substring(0, file.length() - 2));
                return new Loaders.JarLoader(nestedUrl, true);
            } else {
                return new Loaders.URLLoader(url);
            }
        } else {
            // 处理URL格式 file:/your_path/my.jar
            return new Loaders.JarLoader(url, true);
        }
    }

    private String hashKey(URL url) {
        StringBuilder key = new StringBuilder();

        String protocol = url.getProtocol();
        if (protocol != null) {
            protocol = protocol.toLowerCase();
            key.append(protocol);
            key.append("://");
        }

        String host = url.getHost();
        if (host != null) {
            host = host.toLowerCase();
            key.append(host);

            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            if (port != -1) {
                key.append(":").append(port);
            }
        }

        String file = url.getFile();
        if (file != null) {
            key.append(file);
        }

        return key.toString();
    }
}
