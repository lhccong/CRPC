package com.crpc.core.common.config;

import com.crpc.core.common.utils.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 配置加载器
 *
 * @author liuhuaicong
 * @date 2023/08/18
 */
public class PropertiesLoader {

    private PropertiesLoader() {
        throw new IllegalStateException("Utility class");
    }
    private static Properties properties;

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();


    public static void loadConfiguration() throws IOException {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        String defaultPropertiesFile = "crpc.properties";
        InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(defaultPropertiesFile);
        properties.load(in);
    }

    /**
     * 根据键值获取配置属性
     *
     * @param key 关键
     * @return {@link String}
     */
    public static String getPropertiesStr(String key) {
        if (properties == null) {
            return null;
        }
        if (CommonUtils.isEmpty(key)) {
            return null;
        }

        PROPERTIES_MAP.computeIfAbsent(key, k -> properties.getProperty(k));
        return String.valueOf(PROPERTIES_MAP.get(key));
    }

    public static String getPropertiesNotBlank(String key) {
        String val = getPropertiesStr(key);
        if (val == null || "".equals(val)) {
            throw new IllegalArgumentException(key + " 配置为空异常");
        }
        return val;
    }
    public static String getPropertiesStrDefault(String key, String defaultVal) {
        String val = getPropertiesStr(key);
        return val == null || "".equals(val) ? defaultVal : val;
    }
    /**
     * 根据键值获取配置属性
     *
     * @param key 关键
     * @return {@link Integer}
     */
    public static Integer getPropertiesInteger(String key) {
        if (properties == null) {
            return null;
        }
        if (CommonUtils.isEmpty(key)) {
            return null;
        }
        PROPERTIES_MAP.computeIfAbsent(key, k -> properties.getProperty(k));
        return Integer.valueOf(PROPERTIES_MAP.get(key));
    }
    /**
     * 根据键值获取配置属性
     *
     * @param key
     * @return
     */
    public static Integer getPropertiesIntegerDefault(String key,Integer defaultVal) {
        if (properties == null) {
            return defaultVal;
        }
        if (CommonUtils.isEmpty(key)) {
            return defaultVal;
        }
        String value = properties.getProperty(key);
        if(value==null){
            PROPERTIES_MAP.put(key, String.valueOf(defaultVal));
            return defaultVal;
        }
        if (!PROPERTIES_MAP.containsKey(key)) {
            PROPERTIES_MAP.put(key, value);
        }
        return Integer.valueOf(PROPERTIES_MAP.get(key));
    }
}
