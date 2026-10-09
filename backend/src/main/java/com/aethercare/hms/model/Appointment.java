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
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "patient_id")
  private Patient patient;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "doctor_id")
  private Doctor doctor;

  @Column(nullable = false)
  private LocalDate appointmentDate;

  @Column(nullable = false)
  private LocalTime appointmentTime;

  @Column(length = 500)
  private String reason;

  @Column(nullable = false)
  private String status;
}
