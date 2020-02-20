package fr.dndizeye.esdatavizu.repository;

import fr.dndizeye.esdatavizu.model.GithubEventsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GithubEventsRepo extends ElasticsearchRepository<GithubEventsIndex, Long> {
}
