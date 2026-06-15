package org.bigdata.api.job;

import org.bigdata.api.enums.JobStatusEnum;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class JobHandlerInvoker {
    private static Map<JobHandlerIdentifier, JobHandler> handlerMap = new HashMap<>();

    static {
        ServiceLoader<JobHandler> serviceLoader = ServiceLoader.load(JobHandler.class);
        for (JobHandler handler : serviceLoader) {
            handlerMap.put(
                    new JobHandlerIdentifier(
                            handler.getClusterType(),
                            handler.getEngineType(),
                            handler.getJobMode()),
                    handler
            );
        }
    }


    public static <T> String JobSubmit(JobHandlerIdentifier identifier, T param) {
        JobHandler jobHandler = handlerMap.get(identifier);
        if (jobHandler == null) {
            throw new IllegalArgumentException("job handler not found");
        }

        return jobHandler.submit(param);
    }

    public static <T> boolean cancel(JobHandlerIdentifier identifier, T param){
        JobHandler jobHandler = handlerMap.get(identifier);
        if (jobHandler == null) {
            throw new IllegalArgumentException("job handler not found");
        }

        return jobHandler.cancel(param);
    }

    public static <T> JobStatusEnum status(JobHandlerIdentifier identifier, T param){
        JobHandler jobHandler = handlerMap.get(identifier);
        if (jobHandler == null) {
            throw new IllegalArgumentException("job handler not found");
        }

        return jobHandler.status(param);
    }

    public static <T> String getTrackingUrl(JobHandlerIdentifier identifier, T param) {
        JobHandler jobHandler = handlerMap.get(identifier);
        if (jobHandler == null) {
            return "";
        }
        return jobHandler.getTrackingUrl(param);
    }

    public static List<String> listSupportedJobModes(String clusterType, String engineType) {
        return handlerMap.keySet().stream()
                .filter(identifier -> identifier.getClusterType().equalsIgnoreCase(clusterType))
                .filter(identifier -> identifier.getEngineType().equalsIgnoreCase(engineType))
                .map(JobHandlerIdentifier::getJobMode)
                .map(String::toLowerCase)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

}
