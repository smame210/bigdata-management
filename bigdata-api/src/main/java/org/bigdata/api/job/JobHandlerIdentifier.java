package org.bigdata.api.job;

import java.util.Objects;

public class JobHandlerIdentifier implements ClusterType, EngineType, JobDeployMode {
    private final String clusterType;
    private final String engineType;
    private final String jobMode;

    public JobHandlerIdentifier(String clusterType, String engineType, String jobMode) {
        this.clusterType = clusterType;
        this.engineType = engineType;
        this.jobMode = jobMode;
    }

    @Override
    public String getClusterType() {
        return this.clusterType;
    }

    @Override
    public String getEngineType() {
        return this.engineType;
    }

    @Override
    public String getJobMode() {
        return this.jobMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobHandlerIdentifier)) return false;
        JobHandlerIdentifier that = (JobHandlerIdentifier) o;
        return equalsIgnoreCase(clusterType, that.clusterType) &&
                equalsIgnoreCase(engineType, that.engineType) &&
                equalsIgnoreCase(jobMode, that.jobMode);
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equalsIgnoreCase(b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterType.toLowerCase(), engineType.toLowerCase(), jobMode.toLowerCase());
    }
}
