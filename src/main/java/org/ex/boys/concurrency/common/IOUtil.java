package org.ex.boys.concurrency.common;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * io utils
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:18
 */
public class IOUtil {
    public static String getUserHomeResourcePath(String resName) {

        return System.getProperty("user.dir") + File.separator + resName;
    }

    /**
     * 取得当前类路径下的 resName资源的完整路径
     * url.getPath()获取到的路径被utf-8编码了
     * 需要用URLDecoder.decode(path, "UTF-8")解码
     *
     * @param resName 需要获取完整路径的资源,需要以/打头
     * @return 完整路径
     */
    public static String getResourcePath(String resName) {
        URL url = IOUtil.class.getResource(resName);
        String path = url.getPath();
        String decodePath = null;
        try {
            decodePath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodePath;
    }

    public static void closeQuietly(java.io.Closeable o) {
        if (null == o) return;
        try {
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
