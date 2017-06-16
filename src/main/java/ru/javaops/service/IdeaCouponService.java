package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.model.IdeaCoupon;
import ru.javaops.model.Project;
import ru.javaops.model.User;
import ru.javaops.repository.IdeaCouponRepository;

import java.time.LocalDate;

/**
 * GKislin
 * 15.02.2016
 */
@Service
public class IdeaCouponService {
    private final Logger log = LoggerFactory.getLogger(IdeaCouponService.class);

    @Autowired
    private IdeaCouponRepository ideaCouponRepository;

    public IdeaCoupon assignToUser(User user, Project project) {
        IdeaCoupon ideaCoupon = ideaCouponRepository.findByUserId(user.getId());
        if (ideaCoupon != null) {
            throw new IllegalStateException("Согласно политике JetBrains персональный купон IDEA выдается только один раз.");
        }
        ideaCoupon = ideaCouponRepository.getUnused();
        if (ideaCoupon == null) {
            throw new IllegalStateException("Закончились купоны IDEA, напиши пожалуйста в skype: grigory.kislin");
        }
        ideaCoupon.setUser(user);
        ideaCoupon.setProject(project);
        ideaCoupon.setDate(LocalDate.now());
        ideaCouponRepository.save(ideaCoupon);
        log.info("Assign IDEA coupon to " + user.getEmail());
        return ideaCoupon;
    }
}