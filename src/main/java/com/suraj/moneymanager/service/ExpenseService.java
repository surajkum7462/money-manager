package com.suraj.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.suraj.moneymanager.dto.ExpenseDTO;
import com.suraj.moneymanager.entity.CategoryEntity;
import com.suraj.moneymanager.entity.ExpenseEntity;
import com.suraj.moneymanager.entity.ProfileEntity;
import com.suraj.moneymanager.repository.CategoryRepo;
import com.suraj.moneymanager.repository.ExpenseRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final CategoryRepo categoryRepo;
  private final ExpenseRepo expenseRepo;

  private final ProfileService profileService;



  // Retrieve all expenses for the current month / based on the start date and end date

  public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser()
  {
    ProfileEntity profile=profileService.getCurrentProfile();
    LocalDate now=LocalDate.now();
    LocalDate startDate=now.withDayOfMonth(1);
    LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
    List<ExpenseEntity> expenses=expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
    return expenses.stream().map(this::toDTO).toList();


  }

  private ExpenseEntity toEntity(ExpenseDTO dto,ProfileEntity profile,CategoryEntity category)
  {
    return ExpenseEntity.builder()
        .id(dto.getId())
        .name(dto.getName())
        .icon(dto.getIcon())
        .amount(dto.getAmount())
        .date(dto.getDate())
        .category(category)
        .profile(profile)
        .build();
  }

  private ExpenseDTO toDTO(ExpenseEntity entity)
  {
    return ExpenseDTO.builder()
        .id(entity.getId())
        .name(entity.getName())
        .icon(entity.getIcon())
        .categoryName(entity.getCategory()!=null?entity.getCategory().getName():null)
        .categoryId(entity.getCategory()!=null?entity.getCategory().getId():null)
        .amount(entity.getAmount())
        .date(entity.getDate())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }


  public ExpenseDTO addExpense(ExpenseDTO dto)
  {
      ProfileEntity profile=profileService.getCurrentProfile();
      CategoryEntity category=categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
      ExpenseEntity expense=toEntity(dto,profile,category);
      ExpenseEntity savedExpense=expenseRepo.save(expense);
      return toDTO(savedExpense);

  }

 // delete expense by id for current user
 public void deleteExpense(Long id)
 {
   ProfileEntity  profile =profileService.getCurrentProfile();
    ExpenseEntity expense=expenseRepo.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
    if(!expense.getProfile().getId().equals(profile.getId()))
    {
      throw new RuntimeException("Unauthorized to delete expense");
    }
    expenseRepo.deleteById(id);
 }

 // Get Latest 5 expenses for current user


   public List<ExpenseDTO> getLatest5ExpensesForCurrentUser()
   {
     ProfileEntity profile=profileService.getCurrentProfile();
     List<ExpenseEntity> expenses=expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
     return expenses.stream().map(this::toDTO).toList();
   }


   // Get total expense for currenr user
   public BigDecimal getTotalExpenseForCurrentUser()
   {
     ProfileEntity profile=profileService.getCurrentProfile();
     BigDecimal totalExpense=expenseRepo.findTotalExpenseByProfileId(profile.getId());
     return totalExpense!=null?totalExpense:BigDecimal.ZERO;
   }

   // Filter expenses
   public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate,String keyword,Sort sort)
   {
     ProfileEntity profile=profileService.getCurrentProfile();
     List<ExpenseEntity> expenses=expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword,sort);
     return expenses.stream().map(this::toDTO).toList();
   }

   // Notification

   public List<ExpenseDTO> getExpenseForUserOnDate(Long profileId,LocalDate date)
   {
      List<ExpenseEntity> expenses=expenseRepo.findByProfileIdAndDate(profileId,date);
      return expenses.stream().map(this::toDTO).toList();
   }
   public List<ExpenseDTO> getAllExpenseForCurrentUser() {
	    ProfileEntity profile = profileService.getCurrentProfile();
	    List<ExpenseEntity> expense = expenseRepo.findByProfileId(profile.getId());
	    return expense.stream().map(this::toDTO).toList();
	}
  
  
}
