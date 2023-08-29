package com.crpc.core.common.config;

import com.crpc.core.common.utils.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private static Properties properties;

    private static Map<String, String> propertiesMap = new HashMap<>();

    private static String DEFAULT_PROPERTIES_FILE = "G:\\owner-project\\CRPC\\crpc-core\\src\\main\\resources\\crpc.properties";


    public static void loadConfiguration() throws IOException {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(DEFAULT_PROPERTIES_FILE)) {
            properties.load(in);
        }
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

        propertiesMap.computeIfAbsent(key, k -> properties.getProperty(k));
        return String.valueOf(propertiesMap.get(key));
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
        propertiesMap.computeIfAbsent(key, k -> properties.getProperty(k));
        return Integer.valueOf(propertiesMap.get(key));
    }
}
