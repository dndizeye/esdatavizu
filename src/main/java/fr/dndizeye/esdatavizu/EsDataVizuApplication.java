package fr.dndizeye.esdatavizu;

import fr.dndizeye.esdatavizu.config.EsConfig;
import fr.dndizeye.esdatavizu.config.GithubApiServiceOAuth2Config;
import fr.dndizeye.esdatavizu.service.GithubEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class EsDataVizuApplication {
	private static GithubEventsService githubEventsService;

	@Autowired
	public EsDataVizuApplication(GithubEventsService githubEventsService) {
		this.githubEventsService = githubEventsService;
	}

	public static void main(String[] args) throws IOException {

		SpringApplication.run(EsDataVizuApplication.class, args);
		githubEventsService.githubEventsDocumentFromGithubApi();

	/*
		EventService eventService = new EventService();
		eventService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");
		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");

		for (Collection<Event> event : eventService.pagePublicEvents()) {

			event.stream().forEach(ev -> {
				String repoOwner = ev.getActor().getLogin();
				String repoName = ev.getRepo().getName();
				repoName = repoName.substring(repoName.indexOf("/")+1);
				String eventType = ev.getType();
				Date createAt = ev.getCreatedAt();
				log.info("repoName : {}, Event : {}, created at : {} -> Owner : {}", repoName, eventType, createAt, repoOwner);
			});

		}
	*/

	}

}
