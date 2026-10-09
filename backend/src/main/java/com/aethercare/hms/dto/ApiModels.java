package com.aethercare.hms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class ApiModels {
  private ApiModels() {}

  public record DepartmentRequest(
      @NotBlank String name,
      String description,
      String floor,
      String phone
  ) {}

  public record DepartmentResponse(
      Long id,
      String name,
      String description,
      String floor,
      String phone,
      long doctorCount
  ) {}

  public record DoctorRequest(
      @NotBlank String fullName,
      @NotBlank String specialization,
      String email,
      @NotBlank String phone,
      @NotNull Long departmentId,
      @NotNull @Min(0) @Max(60) Integer experienceYears,
      @NotNull @Min(0) Integer consultationFee,
      @NotBlank String status
  ) {}

  public record DoctorResponse(
      Long id,
      String fullName,
      String specialization,
      String email,
      String phone,
      Long departmentId,
      String departmentName,
      Integer experienceYears,
      Integer consultationFee,
      String status
  ) {}

  public record PatientRequest(
      @NotBlank String fullName,
      @NotNull @Min(0) @Max(130) Integer age,
      @NotBlank String gender,
      String email,
      @NotBlank String phone,
      String bloodGroup,
      String address,
      String room,
      @NotBlank String status
  ) {}

  public record PatientResponse(
      Long id,
      String fullName,
      Integer age,
      String gender,
      String email,
      String phone,
      String bloodGroup,
      String address,
      String room,
      String status,
      LocalDate registeredOn
  ) {}

  public record AppointmentRequest(
      @NotNull Long patientId,
      @NotNull Long doctorId,
      @NotNull LocalDate appointmentDate,
      @NotNull LocalTime appointmentTime,
      String reason,
      @NotBlank String status
  ) {}

  public record AppointmentResponse(
      Long id,
      Long patientId,
      String patientName,
      Long doctorId,
      String doctorName,
      String specialization,
      LocalDate appointmentDate,
      LocalTime appointmentTime,
      String reason,
      String status
  ) {}

  public record InvoiceRequest(
      @NotNull Long patientId,
      @NotBlank String description,
      @NotNull @DecimalMin("0.0") BigDecimal amount,
      @NotBlank String status
  ) {}

  public record InvoiceResponse(
      Long id,
      String invoiceNo,
      Long patientId,
      String patientName,
      String description,
      BigDecimal amount,
      String status,
      LocalDate issuedOn,
      LocalDate paidOn
  ) {}

  public record DepartmentLoad(String name, long doctors, long appointments) {}

  public record DashboardResponse(
      long patients,
      long admitted,
      long doctors,
      long onDuty,
      long appointmentsToday,
      long pendingInvoices,
      BigDecimal revenueCollected,
      List<DepartmentLoad> departments,
      List<AppointmentResponse> upcoming,
      List<PatientResponse> recentPatients
  ) {}
}
