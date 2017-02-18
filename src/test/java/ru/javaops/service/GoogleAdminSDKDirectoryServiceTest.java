package ru.javaops.service;

import com.google.api.services.admin.directory.model.Members;
import com.google.api.services.admin.directory.model.Users;
import org.junit.Test;

/**
 * gkislin
 * 17.02.2017
 */
public class GoogleAdminSDKDirectoryServiceTest {
    @Test
    public void testAPI() throws Exception {
        GoogleAdminSDKDirectoryService service = new GoogleAdminSDKDirectoryService();
        service.init();

        System.out.println("\nInsert:");
        String response = service.insertMember("masterjava@javaops.ru", "gkislin@yandex.ru");
        System.out.println(response);

        System.out.println("\nMembers:");
        Members members = service.members("masterjava@javaops.ru");
        members.getMembers().forEach(System.out::println);

        System.out.println("\nUsers:");
        Users users = service.users("javaops.ru");
        users.getUsers().forEach(System.out::println);
    }
}