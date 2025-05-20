package com.manga.ovh.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    private RestClient restClient;

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        String[] parts = clusterNodes.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        restClient = RestClient.builder(new HttpHost(host, port)).build();

        RestClientTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }

    @PreDestroy
    public void cleanup() {
        try {
            restClient.close();
        } catch (Exception ignored) {
        }
    }
}
