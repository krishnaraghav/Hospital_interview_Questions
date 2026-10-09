package com.aethercare.hms.web;

import com.aethercare.hms.dto.ApiModels.AppointmentRequest;
import com.aethercare.hms.dto.ApiModels.AppointmentResponse;
import com.aethercare.hms.dto.ApiModels.DashboardResponse;
import com.aethercare.hms.dto.ApiModels.DepartmentRequest;
import com.aethercare.hms.dto.ApiModels.DepartmentResponse;
import com.aethercare.hms.dto.ApiModels.DoctorRequest;
import com.aethercare.hms.dto.ApiModels.DoctorResponse;
import com.aethercare.hms.dto.ApiModels.InvoiceRequest;
import com.aethercare.hms.dto.ApiModels.InvoiceResponse;
import com.aethercare.hms.dto.ApiModels.PatientRequest;
import com.aethercare.hms.dto.ApiModels.PatientResponse;
import com.aethercare.hms.service.HospitalService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HospitalController {
  private final HospitalService hospital;

  public HospitalController(HospitalService hospital) {
    this.hospital = hospital;
  }

  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok", "service", "AetherCare HMS");
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard() {
    return hospital.dashboard();
  }

  @GetMapping("/departments")
  public List<DepartmentResponse> departments() {
    return hospital.departments();
  }

  @PostMapping("/departments")
  @ResponseStatus(HttpStatus.CREATED)
  public DepartmentResponse createDepartment(@Valid @RequestBody DepartmentRequest request) {
    return hospital.createDepartment(request);
  }

  @PutMapping("/departments/{id}")
  public DepartmentResponse updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
    return hospital.updateDepartment(id, request);
  }

  @DeleteMapping("/departments/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDepartment(@PathVariable Long id) {
    hospital.deleteDepartment(id);
  }

  @GetMapping("/doctors")
  public List<DoctorResponse> doctors() {
    return hospital.doctors();
  }

  @PostMapping("/doctors")
  @ResponseStatus(HttpStatus.CREATED)
  public DoctorResponse createDoctor(@Valid @RequestBody DoctorRequest request) {
    return hospital.createDoctor(request);
  }

  @PutMapping("/doctors/{id}")
  public DoctorResponse updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
    return hospital.updateDoctor(id, request);
  }

  @DeleteMapping("/doctors/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDoctor(@PathVariable Long id) {
    hospital.deleteDoctor(id);
  }

  @GetMapping("/patients")
  public List<PatientResponse> patients() {
    return hospital.patients();
  }

  @PostMapping("/patients")
  @ResponseStatus(HttpStatus.CREATED)
  public PatientResponse createPatient(@Valid @RequestBody PatientRequest request) {
    return hospital.createPatient(request);
  }

  @PutMapping("/patients/{id}")
  public PatientResponse updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
    return hospital.updatePatient(id, request);
  }

  @DeleteMapping("/patients/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePatient(@PathVariable Long id) {
    hospital.deletePatient(id);
  }

  @GetMapping("/appointments")
  public List<AppointmentResponse> appointments() {
    return hospital.appointments();
  }

  @PostMapping("/appointments")
  @ResponseStatus(HttpStatus.CREATED)
  public AppointmentResponse createAppointment(@Valid @RequestBody AppointmentRequest request) {
    return hospital.createAppointment(request);
  }

  @PutMapping("/appointments/{id}")
  public AppointmentResponse updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
    return hospital.updateAppointment(id, request);
  }

  @DeleteMapping("/appointments/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAppointment(@PathVariable Long id) {
    hospital.deleteAppointment(id);
  }

  @GetMapping("/invoices")
  public List<InvoiceResponse> invoices() {
    return hospital.invoices();
  }

  @PostMapping("/invoices")
  @ResponseStatus(HttpStatus.CREATED)
  public InvoiceResponse createInvoice(@Valid @RequestBody InvoiceRequest request) {
    return hospital.createInvoice(request);
  }

  @PutMapping("/invoices/{id}")
  public InvoiceResponse updateInvoice(@PathVariable Long id, @Valid @RequestBody InvoiceRequest request) {
    return hospital.updateInvoice(id, request);
  }

  @PostMapping("/invoices/{id}/pay")
  public InvoiceResponse payInvoice(@PathVariable Long id) {
    return hospital.payInvoice(id);
  }

  @DeleteMapping("/invoices/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInvoice(@PathVariable Long id) {
    hospital.deleteInvoice(id);
  }
}
