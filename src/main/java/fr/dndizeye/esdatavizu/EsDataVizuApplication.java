package fr.dndizeye.esdatavizu;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventPayload;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@Slf4j
public class EsDataVizuApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsDataVizuApplication.class, args);


		EventService eService = new EventService();
		eService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");
		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");
		/*Repository repo = null;
		try {
			repo = repoService.getRepository("Fisheep1207", "Fisheep1207.github.io");
		} catch (IOException e) {
			log.error("Unexpected", e);
		}
		System.out.println("repoLanguage : " + repo.getLanguage());*/

		for (Collection<Event> event : eService.pagePublicEvents()) {

			event.stream().forEach(ev -> {
				//String language = "";
				String repoOwner = ev.getActor().getLogin();
				String repoName = ev.getRepo().getName();
				repoName = repoName.substring(repoName.indexOf("/")+1);
				String eventType = ev.getType();
				Date createAt = ev.getCreatedAt();
				log.info("repoName : {}, Event : {}, created at : {} -> Owner : {}", repoName, eventType, createAt, repoOwner);
				//language = repoService.getRepository(repoOwner, repoName).getLanguage();
				//log.info("repoName : {}, language : {} -> Owner : {}", repoName, language, repoOwner);
			});

			//for (Event ev : event) {
				//String language = "";
				//String repoOwner = ev.getActor().getLogin();
				//String repoName = ev.getRepo().getName();
				//repoName = repoName.substring(repoName.indexOf("/") + 1);
				//language = repoService.getRepository(repoOwner, repoName).getLanguage();
				//log.info("repoName : {}, language : {} -> Owner : {}", repoName, language, repoOwner);
			//}

		}


	}

}
