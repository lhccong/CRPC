package com.crpc.core.common.cache;
import com.crpc.core.registry.RegistryService;
import com.crpc.core.registry.URL;
import com.crpc.core.serialize.SerializeFactory;

import java.util.*;



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

    public static SerializeFactory SERVER_SERIALIZE_FACTORY;

}
