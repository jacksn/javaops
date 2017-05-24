package ru.javaops.web.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.User;
import ru.javaops.service.UserService;
import ru.javaops.to.UserTo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

public abstract class AbstractOAuth2Controller {

    @Autowired
    protected RestTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @RequestMapping("/callback")
    public String authenticate(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        if (state.equals("csrf_token_auth")) {
            String accessToken = getAccessToken(code);
            UserTo userTo = getUserDetails(accessToken);
            User user = userService.findByEmailOrGmail(userTo.getEmail());
            if (user == null) {
                throw new UsernameNotFoundException("Пользователь с адресом " + userTo.getEmail() + " не зарегистрирован.");
            }
            UserDetails userDetails = new AuthorizedUser(user);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );
            // Create a new session and add the security context.
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            return "redirect:/profile";
        }
        return "/";
    }

    protected abstract UserTo getUserDetails(String accessToken);

    protected abstract String getAccessToken(String code);

    protected String getAccessTokenFromOAuth2Provider(String code, OAuth2Provider provider) {
        UriComponentsBuilder builder = fromHttpUrl(provider.getAccessTokenUrl())
                .queryParam("client_id", provider.getClientId())
                .queryParam("client_secret", provider.getClientSecret())
                .queryParam("code", code)
                .queryParam("redirect_uri", provider.getRedirectUri())
                .queryParam("grant_type", "authorization_code");

        ResponseEntity<JsonNode> tokenEntity = template.postForEntity(builder.build().encode().toUri(), null, JsonNode.class);
        return tokenEntity.getBody().get("access_token").asText();
    }
}
