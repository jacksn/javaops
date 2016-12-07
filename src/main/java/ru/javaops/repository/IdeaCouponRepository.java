package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.IdeaCoupon;

@Transactional(readOnly = true)
public interface IdeaCouponRepository extends JpaRepository<IdeaCoupon, Integer> {
    @Query("SELECT c FROM IdeaCoupon c WHERE c.user.id=:userId AND c.project.id=:projectId")
    IdeaCoupon findByUserIdAndProjectId(@Param("userId") int userId, @Param("projectId") int projectId);

    @Query(value = "SELECT * FROM idea_coupon WHERE user_id IS NULL LIMIT 1", nativeQuery = true)
    IdeaCoupon getUnused();
}