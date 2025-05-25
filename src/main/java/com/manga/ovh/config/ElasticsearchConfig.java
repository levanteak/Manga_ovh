package com.manga.ovh.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class ElasticsearchConfig {

    private RestClient restClient;

    @Value("${spring.data.elasticsearch.uris}")
    private String elasticUri;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        URI uri = URI.create(elasticUri);
        String host = uri.getHost();
        int port = uri.getPort();

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
        } catch (Exception ignored) {}
    }
}
