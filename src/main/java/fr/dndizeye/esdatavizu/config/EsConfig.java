package fr.dndizeye.esdatavizu.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

	@Value("${elasticsearch.host}")
	private String esHost;

	@Value("${elasticsearch.port}")
	private int esPort;

	@Bean(destroyMethod = "close")
	public RestHighLevelClient esClient() {
		return new RestHighLevelClient(
						RestClient.builder(
										new HttpHost(esHost, esPort, "http")));
	}

}
