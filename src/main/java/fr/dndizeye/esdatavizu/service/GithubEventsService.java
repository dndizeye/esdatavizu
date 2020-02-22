package fr.dndizeye.esdatavizu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dndizeye.esdatavizu.config.EsConfig;
import fr.dndizeye.esdatavizu.config.GithubApiOAuth2Config;
import fr.dndizeye.esdatavizu.model.GithubEventsDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.EventService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class GithubEventsService {

	private EsConfig esConfig;
	private GithubApiOAuth2Config githubApiOAuth2Config;
	private ObjectMapper objectMapper;

	private static final String INDICES_NAME = "githubevents-index-storage";

	@Autowired
	private GithubEventsService(EsConfig esConfig, GithubApiOAuth2Config githubApiOAuth2Config, ObjectMapper objectMapper) {
		this.esConfig = esConfig;
		this.githubApiOAuth2Config = githubApiOAuth2Config;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedRate = 10000)
	public void saveGithubEventsDocument() throws IOException {
		createGithubEventsIndex();

		log.info(">> SAVE INDEX, OPERATION STARTS");
		EventService eventService = (EventService) githubApiOAuth2Config.serviceOAuth(new EventService());
		PageIterator<Event> eventCollection = eventService.pagePublicEvents();

		int counter = 0;
		for (Collection<Event> event : eventCollection) {
			log.info(">> EVENT COLLECTION SIZE = {}", event.size());
			log.info("EVENTS PAGE {}", counter + 1);
			event.stream().forEach(ev -> {
				GithubEventsDocument githubEventsDocument = createGithubEventsDocumentFromGithubApi(event);
				Map<String, Object> githubEventsDocumentMapper = objectMapper.convertValue(githubEventsDocument, Map.class);
				IndexRequest indexRequest = new IndexRequest(INDICES_NAME).id(githubEventsDocument.getId()).source(githubEventsDocumentMapper);
				try {
					IndexResponse indexResponse = esConfig.esClient().index(indexRequest, RequestOptions.DEFAULT);
				} catch (IOException e) {
					log.error("Index Response Exception", e);
				}
			});
			counter++;
		}
		log.info(">> SAVE INDEX, OPERATION  ENDS");
		log.info("-------------------------------------------------------------------");
	}

	private GithubEventsDocument createGithubEventsDocumentFromGithubApi(Collection<Event> events) {
		GithubEventsDocument githubEventsDocument = new GithubEventsDocument();
		UUID uuid = UUID.randomUUID();

		for (Event event : events) {
			githubEventsDocument = new GithubEventsDocument(uuid.toString(), event.getType(), event.getCreatedAt());
		}
		return githubEventsDocument;
	}

	private void createGithubEventsIndex() throws IOException {
		log.info(">> CHECK IF INDEX EXISTS, OPERATION STARTS");
		GetIndexRequest getIndexrequest = new GetIndexRequest(INDICES_NAME);
		boolean exists = esConfig.esClient().indices().exists(getIndexrequest, RequestOptions.DEFAULT);
		if (!exists) {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDICES_NAME);

			createIndexRequest.settings(Settings.builder()
							.put("index.number_of_shards", 1)
							.put("index.number_of_replicas", 0)
			);

			Map<String, Object> id = new HashMap<>();
			id.put("type", "text");
			Map<String, Object> eventType = new HashMap<>();
			eventType.put("type", "keyword");
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
			log.info(">> INDEX CREATED = {}", acknowledged);
		} else {
			GetIndexResponse getIndexResponse = esConfig.esClient().indices().get(getIndexrequest, RequestOptions.DEFAULT);
			log.info(">> INDEX EXISTS = {}", ArrayUtils.contains(getIndexResponse.getIndices(), INDICES_NAME));
		}
	}

}
