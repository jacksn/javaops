package ru.javaops;

import ru.javaops.model.User;


/**
 * GKislin
 * 24.09.2015.
 */
public class UserTestData {

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;

    public static final User USER = new User(USER_ID, "gkislin@yandex.ru", "Григорий Кислин", "Санкт-Петербург", "vk", null);
    public static final User ADMIN = new User(ADMIN_ID, "admin@javaops.ru", "Java_Online_Projects", "Москва", "habr", null);
}
