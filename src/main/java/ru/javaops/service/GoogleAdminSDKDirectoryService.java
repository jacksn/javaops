package ru.javaops.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.Users;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;

import static com.google.api.client.googleapis.util.Utils.getDefaultJsonFactory;
import static com.google.api.client.googleapis.util.Utils.getDefaultTransport;

@Service
@Slf4j
public class GoogleAdminSDKDirectoryService {
    private static final String APPLICATION_NAME = "JavaOPs";

    private final List<String> SCOPES = ImmutableList.of(
            DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER, DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_GROUP);

    private Directory service;

    @PostConstruct
    void init() throws GeneralSecurityException, IOException {
        GoogleCredential credential;
        try (InputStream is = new FileInputStream("./config/client_secret.json")) {
            credential = GoogleCredential.fromStream(is);
        }
        GoogleCredential credentialWithUser = new GoogleCredential.Builder()
                .setTransport(getDefaultTransport())
                .setJsonFactory(getDefaultJsonFactory())
                .setServiceAccountUser("admin@javaops.ru")
                .setServiceAccountId(credential.getServiceAccountId())
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKey(credential.getServiceAccountPrivateKey())
                .setServiceAccountPrivateKeyId(credential.getServiceAccountPrivateKeyId())
                .setTokenServerEncodedUrl(credential.getTokenServerEncodedUrl()).build();

        service = new Directory.Builder(getDefaultTransport(), getDefaultJsonFactory(), credentialWithUser).setApplicationName(APPLICATION_NAME).build();
    }

    public Members members(String googleGroupName) throws IOException {
        return service.members().list(googleGroupName).execute();
    }

    public String insertMember(String googleGroupMail, String email) {
        log.info("++++ insert {} to {} google groups ++++", email, googleGroupMail);
        Member member = new Member();
        member.setEmail(email);
        try {
            service.members().insert(googleGroupMail, member).execute();
            return "ok";
        } catch (GoogleJsonResponseException ex) {
            log.warn(ex.getDetails().toString());
            if (ex.getDetails().getCode() == 409) {
                //Member already exists.
                log.warn(ex.getDetails().getMessage());
                return "ok";
            }
            return ex.getDetails().getMessage();
        } catch (Exception e) {
            log.error("insertMember failed", e);
            return e.toString();
        }
    }

    public Users users(String googleDomain) throws IOException {
        return service.users().list().setDomain(googleDomain).execute();
    }
}
