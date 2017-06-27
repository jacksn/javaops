package ru.javaops.web.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javaops.to.UserToExt;

@Controller
@RequestMapping("/login/google")
public class GoogleOAuth2Controller extends AbstractOAuth2Controller {

    @Autowired
    public GoogleOAuth2Controller(@Qualifier("googleOAuth2Provider") OAuth2Provider provider) {
        super(provider);
    }

    @Override
    @GetMapping
    public String authorize() {
        return super.authorize() +
                "&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile" +
                "&response_type=code";
    }

    @Override
    protected UserToExt getUserToExt(String accessToken) {
        JsonNode jsonResponse = getRequest(provider.getUserInfoUrl(), accessToken);
        String name = jsonResponse.get("name").asText();
        String email = jsonResponse.get("email").asText();
        return new UserToExt(email, name);
    }
}
