package fr.dndizeye.esdatavizu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dndizeye.esdatavizu.config.EsConfig;
import fr.dndizeye.esdatavizu.config.GithubApiServiceOAuth2Config;
import fr.dndizeye.esdatavizu.model.GithubEventsDocument;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class GithubEventsService {

	private EsConfig esConfig;
	private GithubApiOAuth2Config githubApiOAuth2Config;
	private ObjectMapper objectMapper;

	@Autowired
	private GithubEventsService(EsConfig esConfig, GithubApiOAuth2Config githubApiOAuth2Config, ObjectMapper objectMapper) {
		this.esConfig = esConfig;
		this.githubApiOAuth2Config = githubApiOAuth2Config;
		this.objectMapper = objectMapper;
	}

	public void githubEventsDocumentFromGithubApi() throws IOException {

		ClusterHealthRequest request = new ClusterHealthRequest();
		ClusterHealthResponse response = esConfig.esClient().cluster().health(request, RequestOptions.DEFAULT);

		String clusterName = response.getClusterName();
		int numberOfNodes = response.getNumberOfNodes();
		ClusterHealthStatus status = response.getStatus();

		log.info("clusterName >>> {}", clusterName);
		log.info("NumberOfNodes {}", numberOfNodes);
		log.info("status >>> {}", status);

		log.info("-------------------------------------------");

		RepositoryService repoService = (RepositoryService) githubApiOAuth2Config.serviceOAuth(new RepositoryService());
		Repository repo = new Repository();
		try {
			repo = repoService.getRepository("Fisheep1207", "Fisheep1207.github.io");
		} catch (IOException e) {
			log.error("Unexpected", e);
		}
		log.info("repoLanguage : {}", repo.getLanguage());


	}

	public void saveGithubEventsDocument() throws IOException {
		createGithubEventsIndex();

		EventService eventService = (EventService) githubApiOAuth2Config.serviceOAuth(new EventService());
		PageIterator<Event> eventCollection = eventService.pagePublicEvents();

		for (Collection<Event> event : eventCollection) {
			GithubEventsDocument githubEventsDocument = createGithubEventsDocumentFromGithubApi((Event) event);
			Map<String, Object> githubEventsDocumentMapper = objectMapper.convertValue(githubEventsDocument, Map.class);
		}
	}

	private GithubEventsDocument createGithubEventsDocumentFromGithubApi (Event event) {
		UUID uuid = UUID.randomUUID();
		return new GithubEventsDocument(uuid.toString(), event.getType(), event.getCreatedAt());
	}

	private void createGithubEventsIndex() throws IOException {
		GetIndexRequest getIndexrequest = new GetIndexRequest("githubEvents");
		boolean exists = esConfig.esClient().indices().exists(getIndexrequest, RequestOptions.DEFAULT);
		if(!exists) {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest("githubEvents");

			createIndexRequest.settings(Settings.builder()
							.put("index.number_of_shards", 1)
							.put("index.number_of_replicas", 0)
			);

			Map<String, Object> id = new HashMap<>();
			id.put("type", "text");
			Map<String, Object> eventType = new HashMap<>();
			eventType.put("type", "text");
			Map<String, Object> createdAt = new HashMap<>();
			createdAt.put("type", "date");
			Map<String, Object> properties = new HashMap<>();
			properties.put("id", id);
			properties.put("eventType", eventType);
			properties.put("createdAt", createdAt);
			Map<String, Object> mapping = new HashMap<>();
			mapping.put("properties", properties);
			createIndexRequest.mapping(mapping);

			CreateIndexResponse createIndexResponse = esConfig.esClient().indices().create(createIndexRequest, RequestOptions.DEFAULT);
			boolean acknowledged = createIndexResponse.isAcknowledged();
			log.info("Index creation {}", acknowledged);
		}
	}

}
