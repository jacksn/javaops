package ru.javaops.web.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javaops.to.UserToExt;

@Controller
@RequestMapping("/login/github")
public class GithubOAuth2Controller extends AbstractOAuth2Controller {

    @Autowired
    public GithubOAuth2Controller(OAuth2Provider provider) {
        super(provider);
    }

    @GetMapping
    public String authorize() {
        return "redirect:" + provider.getAuthorizeUrl()
                + "?client_id=" + provider.getClientId()
                + "&redirect_uri=" + provider.getRedirectUri()
                + "&state=csrf_token_auth";
    }

    @Override
    protected UserToExt getUserToExt(String accessToken) {
        JsonNode jsonResponse = getRequest(provider.getUserInfoUrl(), accessToken);
        String name = jsonResponse.get("name").asText();
        if (name.equals("null")) {
            name = jsonResponse.get("login").asText();
        }
        String email = jsonResponse.get("email").asText();
        if (email.equals("null")) {
            // email is not public, one more request
            email = getEmail(accessToken);
        }
        UserToExt userToExt = new UserToExt(email, name);
        userToExt.setGithub(jsonResponse.get("html_url").asText());
        return userToExt;
    }

    private String getEmail(String accessToken) {
        return getRequest("https://api.github.com/user/emails", accessToken).get(0).get("email").asText();
    }
}
