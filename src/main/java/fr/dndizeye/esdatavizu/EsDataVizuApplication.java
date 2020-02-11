package fr.dndizeye.esdatavizu;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventPayload;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Collection;

@SpringBootApplication
public class EsDataVizuApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsDataVizuApplication.class, args);

		/*RepositoryService service = new RepositoryService();
		service.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");
		try {
			for (Repository repo : service.getRepositories("dndizeye"))
				System.out.println(repo.getName() + " Watchers: " + repo.getLanguage());
		} catch (IOException e) {
			e.printStackTrace();
		}*/


		EventService eService = new EventService();
		eService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");

		for (Collection<Event> event : eService.pagePublicEvents()) {
			event.stream().forEach(e -> System.out.println("Event " + e.getType() + ", created at " + e.getCreatedAt()));
		}


	}

}
