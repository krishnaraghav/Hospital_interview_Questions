package com.aethercare.hms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String specialization;

  private String email;
  private String phone;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "department_id")
  private Department department;

  private Integer experienceYears;
  private Integer consultationFee;

  @Column(nullable = false)
  private String status;
}
