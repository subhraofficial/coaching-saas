package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByCoaching_IdAndStudent_IdOrderByPaidAtDesc(Long coachingId, Long studentId);

    List<Payment> findAllByCoaching_IdOrderByPaidAtDesc(Long coachingId);

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
           """)
    long totalCollection();

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.coaching.id = :coachingId
              and p.student.id = :studentId
              and p.paidForMonth = :month
           """)
    long sumPaidForMonth(
            @Param("coachingId") Long coachingId,
            @Param("studentId") Long studentId,
            @Param("month") String month
    );

    @Query("""
            select p.paidForMonth, coalesce(sum(p.amount), 0)
            from Payment p
            where p.coaching.id = :coachingId
              and p.student.id = :studentId
              and p.paidForMonth >= :fromMonth
              and p.paidForMonth <= :toMonth
            group by p.paidForMonth
           """)
    List<Object[]> sumByMonthRange(
            @Param("coachingId") Long coachingId,
            @Param("studentId") Long studentId,
            @Param("fromMonth") String fromMonth,
            @Param("toMonth") String toMonth
    );
}