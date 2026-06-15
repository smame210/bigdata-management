package org.bigdata.flink.yarn;

import com.google.common.hash.Hashing;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class YarnClientManager {
    private static final Map<String, YarnClientProvider> yarnClientProviderMap = new ConcurrentHashMap<>();

    private static final long timeout = Duration.ofMinutes(10).toMillis();

    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            yarnClientProviderMap.forEach((id, provider) -> {
                if (System.currentTimeMillis() - provider.getLastUsedTime() > timeout) {
                    provider.close();
                    yarnClientProviderMap.remove(id);
                }
            });
        }, 10, 10, java.util.concurrent.TimeUnit.SECONDS);

    }

    public static YarnClient getYarnClient(String coreSitePath, String yarnSitePath, String hdfsSitePath) {
        String id = generateId(coreSitePath, yarnSitePath, hdfsSitePath);
        YarnClientProvider yarnClientProvider = yarnClientProviderMap.get(id);
        if (yarnClientProvider == null) {
            yarnClientProvider = new YarnClientProvider(coreSitePath, yarnSitePath, hdfsSitePath);
            yarnClientProviderMap.put(id, yarnClientProvider);
        }
        return yarnClientProvider.getYarnClient();
    }

    public static String generateId(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part).append("|");
        }
        return Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString();
    }
}
