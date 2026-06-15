package org.bigdata.scheduler.core;

import org.bigdata.scheduler.enums.ScheduledType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerProviderRegistry {
    private final Map<ScheduledType, AbstractSchedulerProvider> registry = new HashMap<>();

    @Autowired
    public SchedulerProviderRegistry(List<AbstractSchedulerProvider> providers) {
        for (AbstractSchedulerProvider provider : providers) {
            ScheduledType type = provider.getScheduleType();
            if (registry.containsKey(type)) {
                throw new IllegalStateException("Duplicate scheduler provider for type: " + type);
            }
            registry.put(type, provider);
        }
    }

    public AbstractSchedulerProvider getProvider(ScheduledType type) {
        return registry.get(type);
    }
}
