package com.faberwork.pintaileroauth2client.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;


@Controller
public class AuthorizationController {
    private final WebClient webClient;
    private final String messagesBaseUri;

    public AuthorizationController(WebClient webClient,
                                   @Value("${messages.base-uri}") String messagesBaseUri) {
        this.webClient = webClient;
        this.messagesBaseUri = messagesBaseUri;
    }

    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public Mono<String> authorizationCodeGrant(Model model,
                                               @RegisteredOAuth2AuthorizedClient("messaging-client-authorization-code")
                                         OAuth2AuthorizedClient authorizedClient) {

        return this.webClient
                .get()
                .uri(this.messagesBaseUri + "/messages")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String[].class)
                .map(res -> {
                    model.addAttribute("messages", res);
                    return "index";
                });

    }

    // '/authorized' is the registered 'redirect_uri' for authorization_code
    @GetMapping(value = "/authorized")
    public String authorizationFailed(Model model, ServerHttpRequest request) {
        MultiValueMap<String, String> parameters = request.getQueryParams();
        if (StringUtils.hasText(parameters.getFirst(OAuth2ParameterNames.ERROR))) {
            model.addAttribute("error",
                    new OAuth2Error(
                            parameters.getFirst(OAuth2ParameterNames.ERROR),
                            parameters.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION),
                            parameters.getFirst(OAuth2ParameterNames.ERROR_URI))
            );
        }

        return "index";
    }

    @GetMapping(value = "/authorize", params = "grant_type=client_credentials")
    public Mono<String> clientCredentialsGrant(Model model) {

        return this.webClient
                .get()
                .uri(this.messagesBaseUri + "/messages")
                .attributes(clientRegistrationId("messaging-client-client-credentials"))
                .retrieve()
                .bodyToMono(String[].class)
                .map(res -> {
                    model.addAttribute("messages", res);
                    return "index";
                });
    }
}
