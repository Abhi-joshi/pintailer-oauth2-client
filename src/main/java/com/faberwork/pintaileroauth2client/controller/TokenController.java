package com.faberwork.pintaileroauth2client.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {

    @GetMapping("/tokens")
    public Map<String, Object> tokens(
            @RegisteredOAuth2AuthorizedClient("messaging-client-authorization-code")
            OAuth2AuthorizedClient authorizedClient) {
        return Map.of(
                "access_token", authorizedClient.getAccessToken().getTokenValue(),
                "refresh_token", authorizedClient.getRefreshToken().getTokenValue()
        );
    }
}
