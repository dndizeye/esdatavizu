package fr.dndizeye.esdatavizu.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


import java.util.Date;

@Data
@Document(indexName = "#{@esGithubEventsStorageIndex}")
public class GithubEventsIndex {
	@Id
	private Long id;
	private String eventType;
	private Date createdAt;

	private GithubEventsIndex() {
	}

	private GithubEventsIndex(String eventType, Date createdAt) {
		this.eventType = eventType;
		this.createdAt = createdAt;
	}
}
