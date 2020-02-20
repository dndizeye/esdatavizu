package fr.dndizeye.esdatavizu;

import fr.dndizeye.esdatavizu.config.EsConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@Slf4j
public class EsDataVizuApplication {

	@Value("${githubApi.oAuth2Token}")
	private static String oAuth2Token;

	private static EsConfig esConfig;

	@Autowired
	public EsDataVizuApplication(EsConfig esConfig) {
		this.esConfig = esConfig;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(EsDataVizuApplication.class, args);

		ClusterHealthRequest request = new ClusterHealthRequest();
		ClusterHealthResponse response = esConfig.esClient().cluster().health(request, RequestOptions.DEFAULT);

		String clusterName = response.getClusterName();
		int numberOfNodes = response.getNumberOfNodes();
		ClusterHealthStatus status = response.getStatus();

		log.info("clusterName >>> {}", clusterName);
		log.info("NumberOfNodes {}", numberOfNodes);
		log.info("status >>> {}", status);

		log.info("-------------------------------------------");

		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token(oAuth2Token);

		Repository repo = new Repository();
		try {
			repo = repoService.getRepository("Fisheep1207", "Fisheep1207.github.io");
		} catch (IOException e) {
			log.error("Unexpected", e);
		}
		log.info("repoLanguage : {}", repo.getLanguage());

	/*
		EventService eventService = new EventService();
		eventService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");
		RepositoryService repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token("4efeb3edba9ec63e379a5327876166fc872647af");

		//Repository repo = null;
		//try {
		//	repo = repoService.getRepository("Fisheep1207", "Fisheep1207.github.io");
		//} catch (IOException e) {
		//	log.error("Unexpected", e);
		//}
		//System.out.println("repoLanguage : " + repo.getLanguage());

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
