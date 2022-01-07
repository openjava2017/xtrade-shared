package com.diligrp.xtrade.shared.classloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

/**
 * 自定义类加载器实现
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
public class JavaClassloader extends ClassLoader {

    static {
        if (!ClassLoader.registerAsParallelCapable())
            throw new InternalError();
    }

    private final IResourceFactory resourceFactory;

    /**
     * 自定义类加载器，自动设置AppClassLoader为父类加载器
     */
    public JavaClassloader(String classPath) {
        super(); //设置AppClassLoader为父类加载器
        resourceFactory = initResourceFactory(classPath);
    }

    public JavaClassloader(String classPath, ClassLoader parent) {
        super(parent); //设置AppClassLoader为父类加载器
        resourceFactory = initResourceFactory(classPath);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = findClassOrNull(name);
        if (c == null) throw new ClassNotFoundException(name);
        return c;
    }

    private Class<?> findClassOrNull(String name) {
        if (name == null) return null;
        String path = name.replace('.', '/').concat(".class");
        Resource resource = resourceFactory.getResource(path);
        if (resource == null) return null;
        System.out.println(String.format("%s load %s", resource.getSourceURL(), name));
        try {
            byte[] bytes = resource.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException iex) {
            return null;
        }
    }

    private IResourceFactory initResourceFactory(String classPath) {
        ArrayList<URL> path = new ArrayList<>();
        if (classPath != null) {
            // map each element of class path to a file URL
            int off = 0, next;
            do {
                next = classPath.indexOf(File.pathSeparator, off);
                String element = (next == -1) ? classPath.substring(off) : classPath.substring(off, next);
                if (!element.isEmpty()) {
                    try {
                        File file = new File(element).getCanonicalFile();
                        URL url = file.toURI().toURL();
                        if (url != null) path.add(url);
                    } catch (IOException ioe) {
                        continue;
                    }
                }
                off = next + 1;
            } while (next != -1);
        }
        URLClassPath urlClassPath = new URLClassPath(path.toArray(new URL[0]));
        return new URLResourceFactory(urlClassPath);
    }

    public static void main(String[] args) {
        ClassLoader loader = new JavaClassloader("/Users/brenthuang/Work/projects/Openjava/build/classes/java/main:" +
                "/Users/brenthuang/Work/projects/Openjava/build/libs/Openjava-1.0-SNAPSHOT.jar");
        try {
            Class<?> c = loader.loadClass("org.openjava.service.impl.HelloService");
            System.out.println(c.getClassLoader());
            System.out.println(c.getClassLoader().getParent());

            Constructor<?> constructor = c.getDeclaredConstructor();
            Object service = constructor.newInstance();
            Method method = c.getDeclaredMethod("sayHello");
            method.invoke(service);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
