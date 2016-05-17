package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.MailCase;

import java.util.List;

@Transactional(readOnly = true)
public interface MailCaseRepository extends JpaRepository<MailCase, Integer> {
    @Query("SELECT mc FROM MailCase mc WHERE mc.datetime > TODAY AND mc.result <> 'OK'")
    List<MailCase> getTodayFailed();
}