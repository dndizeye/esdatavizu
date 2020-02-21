package fr.dndizeye.esdatavizu.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@ToString
public class GithubEventsDocument {
	@Id
	private String id;
	private String eventType;
	private Date createdAt;

	public GithubEventsDocument() {

    }

	public GithubEventsDocument(String id, String eventType, Date createdAt) {
		this.id = id;
		this.eventType = eventType;
		this.createdAt = createdAt;
	}
}
