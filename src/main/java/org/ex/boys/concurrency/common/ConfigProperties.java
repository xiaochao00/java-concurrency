package org.ex.boys.concurrency.common;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * config properties
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:23
 */
public class ConfigProperties {

    private String propertiesName = "";
    private final Properties properties = new Properties();


    public ConfigProperties() {

    }

    public ConfigProperties(String fileName) {
        this.propertiesName = fileName;
    }


    protected void loadFromFile() {
        InputStream in;
        InputStreamReader ireader = null;
        try {
//            String filePath = IOUtil.getResourcePath(properiesName);
            if (propertiesName.startsWith("/")) {
                propertiesName = propertiesName.substring(1);
            }
            in = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
//            in = new FileInputStream(filePath);
            //解决读非UTF-8编码的配置文件时，出现的中文乱码问题
            assert in != null;
            ireader = new InputStreamReader(in, StandardCharsets.UTF_8);
            properties.load(ireader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(ireader);
        }
    }


    /**
     * 按key获取值
     *
     * @param key 属性值的键
     * @return 读取到的属性值
     */
    public String readProperty(String key) {
        String value;

        value = properties.getProperty(key);

        return value;
    }


    public String getValue(String key) {

        return readProperty(key);

    }

    public int getIntValue(String key) {

        return Integer.parseInt((readProperty(key)));

    }

    public static ConfigProperties loadFromFile(Class aClass)
            throws IllegalAccessException {


        return null;
    }

    public static void loadAnnotations(Class aClass) {

        ConfigProperties configProperties;
        try {
            configProperties = loadFromFile(aClass);


            if (null == configProperties) return;

            Field[] fields = aClass.getDeclaredFields();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
