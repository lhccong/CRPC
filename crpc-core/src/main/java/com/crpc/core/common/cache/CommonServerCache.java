package com.crpc.core.common.cache;
import com.crpc.core.common.config.ServerConfig;
import com.crpc.core.filter.server.ServerFilterChain;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.core.serialize.SerializeFactory;
import com.crpc.core.server.ServiceWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 服务器缓存
 *
 * @author liuhuaicong
 * @date 2023/08/08
 */
public class CommonServerCache {
    private CommonServerCache() {
        throw new IllegalStateException("Utility class");
    }
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();

    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();

    public static RegistryService REGISTRY_SERVICE;
    public static ServerConfig SERVER_CONFIG;
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;

    public static ServerFilterChain SERVER_FILTER_CHAIN;

    public static final Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();

    public static Boolean IS_STARTED = false;
}
