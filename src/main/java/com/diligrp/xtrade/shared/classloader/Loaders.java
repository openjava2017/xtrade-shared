package com.diligrp.xtrade.shared.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

/**
 * 资源加载器定义
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
class Loaders {
    static class FileLoader extends URLLoader {

        private File dir;

        public FileLoader(URL url) throws IOException {
            // file:/your_path/../my_path/ -> file:/your_path/
            super(new URL(url, "."));

            if (!"file".equals(url.getProtocol())) {
                throw new IllegalArgumentException("invalid file url: " + url);
            }

            String path = checkPath(getBaseURL().getFile());
            dir = new File(path).getCanonicalFile();
        }

        @Override
        public Resource getResource(String name) {
            try {
                URL url = new URL(getBaseURL(), name);
                if (url.getFile().contains("%") || !url.getFile().startsWith(getBaseURL().getFile())) {
                    // 路径中包含特殊字符(已被编码成%xx)或../..，则返回空
                    return null;
                }

                File file = new File(dir, name.replace('/', File.separatorChar)).getCanonicalFile();
                if (!((file.getPath()).startsWith(dir.getPath())) ) {
                    /* outside of base dir */
                    return null;
                }

                if (file.exists() && file.isFile()) {
                    return new Resource() {
                        @Override
                        public String getName() {
                            return name;
                        }

                        @Override
                        public URL getURL() {
                            return url;
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new FileInputStream(file);
                        }

                        @Override
                        public int getContentLength() {
                            return (int)file.length();
                        }

                        @Override
                        public URL getSourceURL() {
                            return getBaseURL();
                        }
                    };
                }
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    static class JarLoader extends URLLoader {
        private static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
        private static final String JAVA_VERSION = "11.0.12";

        private final URL sourceURL;
        private final JarFile jarFile;
        private final boolean parseClassPath;
        /**
         * url格式: file:/your_path/my.jar; http://www.hostname.com/your_path/my.jar
         */
        public JarLoader(URL url, boolean parseClassPath) throws IOException {
            super(new URL("jar", "", -1, url + "!/"));
            this.sourceURL = url;
            this.parseClassPath = parseClassPath;
            this.jarFile = openJarFile(sourceURL);
            //TODO: 处理解析jar index
        }

        @Override
        public Resource getResource(String name) {
            final JarEntry entry = jarFile.getJarEntry(name);
            if (entry != null) {
                final URL url;
                try {
                    url = new URL(getBaseURL(), jarFile.isMultiRelease() ? entry.getRealName() : name);
                } catch (IOException iex) {
                    return null;
                }

                return new Resource() {
                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public URL getURL() {
                        return url;
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return jarFile.getInputStream(entry);
                    }

                    @Override
                    public int getContentLength() {
                        return (int)entry.getSize();
                    }

                    @Override
                    public URL getSourceURL() {
                        return sourceURL;
                    }
                };
            }
            // TODO: 处理签名过的JAR文件

            return null;
        }

        @Override
        public URL[] getClassPath() throws IOException {
            // JarFile.hasClassPathAttribute方法不能被外部访问, 为优化类加载性能暂屏蔽此方法
            if (!parseClassPath) return null;

            Manifest man = jarFile.getManifest();
            if (man == null) return null;
            Attributes attr = man.getMainAttributes();
            if (attr == null) return null;
            String value = attr.getValue(Attributes.Name.CLASS_PATH);
            if (value == null) return null;

            StringTokenizer tokenizer = new StringTokenizer(value);
            URL[] urls = new URL[tokenizer.countTokens()];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken();
                // (file:/my_path/my.jar, your_path/your.jar) -> file:/my_path/your_path/your.jar
                try {
                    URL url = new URL(sourceURL, path);
                    if (url != null) {
                        urls[i++] = url;
                    }
                } catch (IOException iex) {
                    continue;
                }
            }
            if (i == 0) {
                urls = null;
            } else if (i != urls.length) {
                // Truncate nulls from end of array
                urls = Arrays.copyOf(urls, i);
            }
            return urls;
        }

        private JarFile openJarFile(URL url) throws IOException {
            if ("file".equals(url.getProtocol())) {
                // 本地文件系统加载以优化加载性能
                String path = checkPath(url.getFile());
                if (path == null) {
                    throw new FileNotFoundException();
                }

                File file = new File(path);
                if (!file.exists()) {
                    throw new FileNotFoundException(path);
                }
                return new JarFile(file, true, ZipFile.OPEN_READ, JarFile.runtimeVersion());
            }

            URLConnection uc = (new URL(getBaseURL(), "#runtime")).openConnection();
            uc.setRequestProperty(USER_AGENT_JAVA_VERSION, JAVA_VERSION);
            return ((JarURLConnection)uc).getJarFile();
        }
    }

    static class URLLoader implements IResourceFactory.ILoader {

        protected URL baseURL;

        public URLLoader(URL url) {
            this.baseURL = url;
        }

        @Override
        public URL getBaseURL() {
            return baseURL;
        }

        @Override
        public Resource getResource(String name) {
            try {
                final URL url = new URL(getBaseURL(), name);
                final URLConnection connection = url.openConnection();
                final InputStream in = connection.getInputStream();

                return new Resource() {
                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public URL getURL() {
                        return url;
                    }

                    @Override
                    public InputStream getInputStream() {
                        return in;
                    }

                    @Override
                    public int getContentLength() {
                        return connection.getContentLength();
                    }

                    @Override
                    public URL getSourceURL() {
                        return getBaseURL();
                    }
                };
            } catch (IOException iex) {
                return null;
            }
        }

        @Override
        public URL[] getClassPath() throws IOException {
            return null;
        }

        /**
         * 本地文件系统路径目前不支持特殊字符, URL.getPath/getFile不允许特殊字符; URL路径中的特殊字符都已经编码成%xx
         */
        protected String checkPath(String path) {
            if (path != null) {
                if (path.contains("%")) {
                    throw new IllegalArgumentException("file path not supported: " + path);
                }
                // 替换成本地文件目录分隔符
                return path.replace('/', File.separatorChar);
            } else {
                return null;
            }
        }
    }
}
