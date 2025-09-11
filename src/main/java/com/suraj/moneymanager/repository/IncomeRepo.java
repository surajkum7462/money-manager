package com.suraj.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.suraj.moneymanager.entity.IncomeEntity;

public interface  IncomeRepo extends JpaRepository<IncomeEntity, Long>{

  
  // select * from income where profile_id = ? 1
  List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

  // select top 5 from income where profile_id = ? 1 order by date desc 
  List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);


  @Query("select sum(e.amount) from ExpenseEntity e where e.profile.id = :profileId")
  BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

 
 // select * from income where profile_id = ? 1 and date between ? 2 and ? 3
  List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword,Sort sort);

// select * from income where profile_id = ? 1 and date between ? 2 and ? 3
  List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
  
  
}
