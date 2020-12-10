package org.apache.pulsar.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hezhangjian
 */
@Slf4j
public class DebugTopicUtil {

    static Config config;

    static final ConcurrentHashMap<String, String> fakeSet = new ConcurrentHashMap<>();

    static {
        config = ConfigService.getConfig("Pulsar.Debug");
        // do nothing
        final String configProperty = config.getProperty("DebugTopicList", "");
        if (configProperty != null && configProperty.length() > 0) {
            final String[] split = configProperty.split("\\.");
            for (String s : split) {
                fakeSet.put(s, s);
            }
        }
        config.addChangeListener(changeEvent -> {
            log.info("Changes for namespace " + changeEvent.getNamespace());
            fakeSet.clear();
            for (String key : changeEvent.changedKeys()) {
                ConfigChange change = changeEvent.getChange(key);
                final String value = change.getNewValue();
                final String[] split = value.split("\\.");
                for (String s : split) {
                    fakeSet.put(s, s);
                }
            }
        });
    }

    public static boolean contains(String topic) {
        return fakeSet.contains(topic);
    }

}
