package ru.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Configuration
public class WebClientConfig {

    private final DiscoveryClient discoveryClient;
    private final String nameService;

    @Autowired
    public WebClientConfig(DiscoveryClient discoveryClient, @Value("${stats-server.name}") String nameService) {
        this.discoveryClient = discoveryClient;
        this.nameService = nameService;
    }

    @Bean
    @Scope("prototype")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(String.valueOf(makeUri(nameService)))
                .build();
    }

    private URI makeUri(String nameService) {
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println(discoveryClient.getServices()); // подтягивает с докер переменных 'stats-server'. одинарные кавычки мешают
        // с проперти все корректно
        ServiceInstance instance = getRetryTemplate().execute(cxt -> getInstance("stats-server"));
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort());
    }

    private ServiceInstance getInstance(String service) {
        try {
            return discoveryClient
                    .getInstances(service)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + service
            );
        }
    }

    private RetryTemplate getRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        template.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        template.setRetryPolicy(retryPolicy);
        return template;
    }
}