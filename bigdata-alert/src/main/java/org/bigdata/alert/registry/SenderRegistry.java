package org.bigdata.alert.registry;

import org.bigdata.alert.Sender;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SenderRegistry {

    private final Map<String, Sender> senderMap = new HashMap<>();

    public SenderRegistry() {
        ServiceLoader<Sender> serviceLoader = ServiceLoader.load(Sender.class);
        for (Sender sender : serviceLoader) {
            senderMap.put(sender.getSenderName(), sender);
        }
    }

    public Sender getSender(String type) {
        return senderMap.get(type);
    }
}
