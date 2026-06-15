package org.bigdata.flink.standalone;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bigdata.api.enums.ClusterTypeEnum;
import org.bigdata.api.enums.EngineTypeEnum;
import org.bigdata.api.enums.JobStatusEnum;
import org.bigdata.api.exception.JobHandleException;
import org.bigdata.api.job.JobHandler;
import org.bigdata.flink.bean.ApplicationJobParam;
import org.bigdata.flink.enums.FlinkDeployMode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AutoService(JobHandler.class)
public class StandaloneJobHandler implements JobHandler<ApplicationJobParam> {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String submit(ApplicationJobParam job) {
        String clusterUrl = job.getClusterUrl();
        String jarPath = job.getJarPath();
        String mainClass = job.getMainClass();
        Map<String, String> args = job.getArgs();
        int parallelism = job.getParallelism() == null ? 1 : job.getParallelism();

        List<String> argList = new ArrayList<>();
        if (args != null) {
            for (Map.Entry<String, String> entry : args.entrySet()) {
                argList.add("--" + entry.getKey());
                argList.add(entry.getValue());
            }
        }

        log.info("Submitting Flink job to Standalone cluster: {}", clusterUrl);
        log.info("JAR: {}, MainClass: {}, Args: {}, Parallelism: {}", jarPath, mainClass, argList, parallelism);

        // TODO: Implement actual Flink REST API call to upload jar and submit job
        // For now, return a placeholder job ID
        return "standalone-" + System.currentTimeMillis();
    }

    @Override
    public boolean cancel(ApplicationJobParam params) {
        String clusterUrl = params.getClusterUrl();
        String jobId = params.getJobId();

        String cancelUrl = clusterUrl + "/jobs/" + jobId + "/yarn-cancel";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(cancelUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Cancel job response: status={}, body={}", response.statusCode(), response.body());
            return response.statusCode() == 200;
        } catch (Exception e) {
            log.error("Failed to cancel job, jobId: {}", jobId, e);
            return false;
        }
    }

    @Override
    public JobStatusEnum status(ApplicationJobParam params) {
        String clusterUrl = params.getClusterUrl();
        String jobId = params.getJobId();

        String statusUrl = clusterUrl + "/jobs/" + jobId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(statusUrl))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Parse JSON response to get state
                // For now, return RUNNING as placeholder
                return JobStatusEnum.RUNNING;
            }
            return JobStatusEnum.UNKNOWN;
        } catch (Exception e) {
            log.error("Failed to get job status, jobId: {}", jobId, e);
            return JobStatusEnum.UNKNOWN;
        }
    }

    @Override
    public String getTrackingUrl(ApplicationJobParam params) {
        return params.getClusterUrl();
    }

    @Override
    public String getEngineType() {
        return EngineTypeEnum.FLINK.name();
    }

    @Override
    public String getJobMode() {
        return FlinkDeployMode.SESSION.name();
    }

    @Override
    public String getClusterType() {
        return ClusterTypeEnum.STANDALONE.name();
    }
}
