package fr.dndizeye.esdatavizu.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Service;

@Configuration
@EnableElasticsearchRepositories(basePackages = "fr.dndizeye.esdatavizu.repository")
public class EsConfig {

	@Value("${elasticsearch.host}")
	private String esHost;

	@Value("${elasticsearch.port}")
	private int esPort;

	@Value("${elasticsearch.index.storage.name}")
	private String esGithubEventsStorageIndex;

	@Bean(destroyMethod = "close")
	public RestHighLevelClient esClient() {
		RestHighLevelClient client = new RestHighLevelClient(
						RestClient.builder(
										new HttpHost(esHost, esPort, "http")));
		return client;
	}

	/**
	 * @return the esGithubEventsStorageIndex
	 */
	@Bean
	public String esGithubEventsStorageIndex() {
		return esGithubEventsStorageIndex;
	}
}
