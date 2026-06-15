package org.bigdata.server.convert;

import org.bigdata.api.job.JobHandlerIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class JobParamConverterInvoker {
    private static Map<JobHandlerIdentifier, JobParamConverter> converterMap = new HashMap<>();

    static {
        ServiceLoader<JobParamConverter> serviceLoader = ServiceLoader.load(JobParamConverter.class);
        for (JobParamConverter converter : serviceLoader) {
            converterMap.put(
                    new JobHandlerIdentifier(
                            converter.getClusterType(),
                            converter.getEngineType(),
                            converter.getJobMode()),
                    converter
            );
        }
    }

    public static JobParamConverter convert(JobHandlerIdentifier identifier) {
        JobParamConverter converter = converterMap.get(identifier);
        if (converter == null) {
            throw new IllegalArgumentException("No converter found for identifier: " + identifier);
        }
        return converter;
    }
}
