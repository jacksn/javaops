package ru.javaops.web.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.User;
import ru.javaops.service.UserService;
import ru.javaops.to.UserToExt;
import ru.javaops.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
public abstract class AbstractOAuth2Controller {

    @Autowired
    protected RestTemplate template;

    @Autowired
    private UserService userService;

    final OAuth2Provider provider;

    public AbstractOAuth2Controller(OAuth2Provider provider) {
        this.provider = provider;
    }

    @RequestMapping("/callback")
    public String authenticate(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        if (state.equals("csrf_token_auth")) {
            String accessToken = getAccessToken(code);

            UserToExt userToExt = getUserToExt(accessToken);
            log.info(provider.getName() + " authorization from user {}", userToExt.getEmail());
            User user = userService.findByEmailOrGmail(userToExt.getEmail());
            if (user == null) {
                AuthorizedUser.setPreAuthorized(userToExt, request);
                return "redirect:/view/profileChoice?email=" + userToExt.getEmail();
            }
            if (UserUtil.updateFromAuth(user, userToExt)) {
                userService.save(user);
            }
            AuthorizedUser.setAuthorized(user, request);
            return "redirect:/auth/profile";
        }
        return "/";
    }

    protected abstract UserToExt getUserToExt(String accessToken);

    public String authorize() {
        return "redirect:" + provider.getAuthorizeUrl()
                + "?client_id=" + provider.getClientId()
                + "&redirect_uri=" + provider.getRedirectUri()
                + "&state=csrf_token_auth";
    }

    protected String getAccessToken(String code) {
        URI uri = fromHttpUrl(provider.getAccessTokenUrl())
                .queryParam("client_id", provider.getClientId())
                .queryParam("client_secret", provider.getClientSecret())
                .queryParam("code", code)
                .queryParam("redirect_uri", provider.getRedirectUri())
                .queryParam("grant_type", "authorization_code").build().encode().toUri();

        ResponseEntity<JsonNode> tokenEntity = template.postForEntity(uri, null, JsonNode.class);
        return tokenEntity.getBody().get("access_token").asText();
    }

    protected JsonNode getRequest(String url, String accessToken) {
        UriComponentsBuilder builder = fromHttpUrl(url).queryParam("access_token", accessToken);
        ResponseEntity<JsonNode> entity = template.getForEntity(builder.build().encode().toUri(), JsonNode.class);
        return entity.getBody();
    }
}
