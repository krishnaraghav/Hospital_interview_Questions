package com.aethercare.hms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class Patient {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullName;

  private Integer age;
  private String gender;
  private String email;
  private String phone;
  private String bloodGroup;

  @Column(length = 500)
  private String address;

  private String room;

  @Column(nullable = false)
  private String status;

  private LocalDate registeredOn;
}
