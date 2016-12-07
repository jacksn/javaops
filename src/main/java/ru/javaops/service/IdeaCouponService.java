package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.model.IdeaCoupon;
import ru.javaops.model.Project;
import ru.javaops.model.User;
import ru.javaops.repository.IdeaCouponRepository;

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
        IdeaCoupon ideaCoupon = ideaCouponRepository.findByUserIdAndProjectId(user.getId(), project.getId());
        if (ideaCoupon != null) {
            throw new IllegalStateException(
                    String.format("Вы уже получали купон IDEA, проверьте почту<br>На проекте %s можно получить купон только один раз", project.getName()));
        }
        ideaCoupon = ideaCouponRepository.getFirstByUserIdIsNull();
        ideaCoupon.setUser(user);
        ideaCoupon.setProject(project);
        ideaCouponRepository.save(ideaCoupon);
        log.info("Assign IDEA coupon to " + user.getEmail());
        return ideaCoupon;
    }
}
