package com.suraj.moneymanager.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="tbl_incomes")
public class IncomeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String icon;
  private BigDecimal amount;

  private LocalDate date;

  @CreationTimestamp
  @Column(updatable=false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="category_id", nullable=false)
  private CategoryEntity category;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="profile_id", nullable=false)
  private ProfileEntity profile;


  @PrePersist
  public void prePersist()
  {
    if(this.date==null)
    {
      this.date=LocalDate.now();
    }
  }
  
}
