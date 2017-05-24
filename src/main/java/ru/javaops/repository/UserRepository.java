package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.User;
import ru.javaops.to.UserMail;
import ru.javaops.to.UserStat;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
    @Override
    User getOne(Integer integer);

    @Query("SELECT u FROM User u " +
            "  LEFT JOIN FETCH u.roles WHERE u.email=:email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "  LEFT JOIN FETCH u.roles WHERE u.email=:email OR u.gmail=:email")
    User findByEmailOrGmail(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "  LEFT JOIN FETCH u.userGroups WHERE u.email=:email")
    User findByEmailWithGroup(@Param("email") String email);

    @Query("SELECT new ru.javaops.to.UserMail(ug.user) FROM UserGroup ug " +
            " WHERE ug.group.name=:groupName AND ug.user.active=TRUE")
    Set<UserMail> findByGroupName(@Param("groupName") String groupName);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.user.email=:email AND ug.group.id=:groupId")
    User findByEmailAndGroupId(@Param("email") String email, @Param("groupId") int groupId);

    @Query("SELECT new ru.javaops.to.UserStat(u.fullName, u.email, u.location, u.aboutMe, u.skype) FROM User u " +
            "WHERE u.statsAgree=TRUE " +
            "AND u.fullName IS NOT NULL " +
            "AND u.location IS NOT NULL " +
            "ORDER BY LOWER(u.location)")
    List<UserStat> findAllForStats();

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.user.email=:email AND ug.group.name=:groupName")
    User findByEmailAndGroupName(@Param("email") String email, @Param("groupName") String groupName);

    @Override
    User save(User entity);

    //    https://jira.spring.io/browse/DATAJPA-1103
//    @Query("UPDATE User u SET u.comment = :#{#uaInfo.comment}, u.mark=:#{#uaInfo.mark}, u.bonus=:#{#uaInfo.bonus} WHERE u.email=:email")
//    void saveAdminInfo(@Param("email") String email, @Param("uaInfo") UserAdminsInfo uaInfo);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.comment = :comment, u.mark=:mark, u.bonus=:bonus WHERE u.email=:email")
    void saveAdminInfo(@Param("email") String email, @Param("comment") String comment, @Param("mark") String mark, @Param("bonus") Integer bonus);
}