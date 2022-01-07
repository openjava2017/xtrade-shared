package com.diligrp.xtrade.shared.classloader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Arrays;

/**
 * 资源模型
 *
 * @author: brenthuang
 * @date: 2021/12/29
 */
public abstract class Resource {
    // The name of resource
    public abstract String getName();

    // The url of resource
    public abstract URL getURL();

    // The InputStream for reading the resource.
    public abstract InputStream getInputStream() throws IOException;

    // The length of the Resource data, or -1 if unknown.
    public abstract int getContentLength() throws IOException;

    // The source URL for the Resource.
    public abstract URL getSourceURL();

    public byte[] getBytes() throws IOException {
        byte[] b;
        // Get stream before content length so that a FileNotFoundException
        // can propagate upwards without being caught too early
        InputStream in = getInputStream();

        // This code has been uglified to protect against interrupts.
        // Even if a thread has been interrupted when loading resources,
        // the IO should not abort, so must carefully retry, failing only
        // if the retry leads to some other IO exception.
        boolean isInterrupted = Thread.interrupted();

        int len;
        for (;;) {
            try {
                len = getContentLength();
                break;
            } catch (InterruptedIOException iioe) {
                Thread.interrupted();
                isInterrupted = true;
            }
        }

        try {
            b = new byte[0];
            if (len == -1) {
                len = Integer.MAX_VALUE;
            }

            int pos = 0;
            while (pos < len) {
                int bytesToRead;
                if (pos >= b.length) { // Only expand when there's no room
                    bytesToRead = Math.min(len - pos, b.length + 1024);
                    if (b.length < pos + bytesToRead) {
                        b = Arrays.copyOf(b, pos + bytesToRead);
                    }
                } else {
                    bytesToRead = b.length - pos;
                }

                int cc = 0;
                try {
                    cc = in.read(b, pos, bytesToRead);
                } catch (InterruptedIOException iioe) {
                    Thread.interrupted();
                    isInterrupted = true;
                }

                if (cc < 0) {
                    if (len != Integer.MAX_VALUE) {
                        throw new EOFException("Detect premature EOF");
                    } else {
                        if (b.length != pos) {
                            b = Arrays.copyOf(b, pos);
                        }
                        break;
                    }
                }
                pos += cc;
            }
        } finally {
            try {
                in.close();
            } catch (InterruptedIOException iioe) {
                isInterrupted = true;
            } catch (IOException ignore) {}

            if (isInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return b;
    }
}
