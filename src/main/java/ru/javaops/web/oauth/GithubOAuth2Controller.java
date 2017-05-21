package ru.javaops.web.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;
import ru.javaops.to.UserTo;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Controller
@RequestMapping("/login/github")
public class GithubOAuth2Controller extends AbstractOAuth2Controller {

    @Autowired
    @Qualifier("githubOAuth2Provider")
    private OAuth2Provider provider;

    @GetMapping
    public String authorize() {
        return "redirect:" + provider.getAuthorizeUrl()
                + "?client_id=" + provider.getClientId()
                + "&redirect_uri=" + provider.getRedirectUri()
                + "&state=" + provider.getCode();
    }

    @Override
    protected UserTo getUserDetails(String accessToken) {
        UriComponentsBuilder builder = fromHttpUrl(provider.getUserInfoUrl()).queryParam("access_token", accessToken);
        ResponseEntity<JsonNode> responseEntity = template.getForEntity(builder.build().encode().toUri(), JsonNode.class);
        JsonNode user = responseEntity.getBody();
        String login = user.get("login").asText();
        String name = user.get("name").asText();
        if (name.equals("null")) {
            name = login;
        }
        String email = user.get("email").asText();
        if (email.equals("null")) {
            throw new UsernameNotFoundException("No email found in Github account");
        }
        UserTo userTo = new UserTo();
        userTo.setEmail(email);
        userTo.setNameSurname(name);
        return userTo;
    }

    protected String getAccessToken(String code) {
        return super.getAccessTokenFromOAuth2Provider(code, provider);
    }
}
