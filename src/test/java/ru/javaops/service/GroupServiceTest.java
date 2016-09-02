package ru.javaops.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.ApplicationAbstractTest;
import ru.javaops.UserTestData;

/**
 * GKislin
 * 16.02.2016
 */
public class GroupServiceTest extends ApplicationAbstractTest {

    @Autowired
    private GroupService groupService;

    @Test
    public void testFindByUser() throws Exception {
        System.out.println(groupService.findByUserId(UserTestData.USER_ID));
    }
}