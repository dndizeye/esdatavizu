package fr.dndizeye.esdatavizu.config;

import org.eclipse.egit.github.core.service.GitHubService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubApiServiceOAuth2Config {

	@Value("${github.pta}")
	private String oAuth2Token;

	public GitHubService serviceOAuth(GitHubService service) {
		service.getClient().setOAuth2Token(oAuth2Token);
		return service;
	}
}
