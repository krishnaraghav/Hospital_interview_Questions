package com.aethercare.hms.service;

import com.aethercare.hms.dto.ApiModels.AppointmentRequest;
import com.aethercare.hms.dto.ApiModels.AppointmentResponse;
import com.aethercare.hms.dto.ApiModels.DashboardResponse;
import com.aethercare.hms.dto.ApiModels.DepartmentLoad;
import com.aethercare.hms.dto.ApiModels.DepartmentRequest;
import com.aethercare.hms.dto.ApiModels.DepartmentResponse;
import com.aethercare.hms.dto.ApiModels.DoctorRequest;
import com.aethercare.hms.dto.ApiModels.DoctorResponse;
import com.aethercare.hms.dto.ApiModels.InvoiceRequest;
import com.aethercare.hms.dto.ApiModels.InvoiceResponse;
import com.aethercare.hms.dto.ApiModels.PatientRequest;
import com.aethercare.hms.dto.ApiModels.PatientResponse;
import com.aethercare.hms.exception.ConflictException;
import com.aethercare.hms.exception.NotFoundException;
import com.aethercare.hms.model.Appointment;
import com.aethercare.hms.model.Department;
import com.aethercare.hms.model.Doctor;
import com.aethercare.hms.model.Invoice;
import com.aethercare.hms.model.Patient;
import com.aethercare.hms.repository.AppointmentRepository;
import com.aethercare.hms.repository.DepartmentRepository;
import com.aethercare.hms.repository.DoctorRepository;
import com.aethercare.hms.repository.InvoiceRepository;
import com.aethercare.hms.repository.PatientRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HospitalService {
  private static final Set<String> DOCTOR_STATUS = Set.of("AVAILABLE", "IN_ROUNDS", "ON_LEAVE");
  private static final Set<String> PATIENT_STATUS = Set.of("OUTPATIENT", "ADMITTED", "DISCHARGED");
  private static final Set<String> VISIT_STATUS = Set.of("SCHEDULED", "COMPLETED", "CANCELLED");
  private static final Set<String> BILL_STATUS = Set.of("PENDING", "PAID");

  private final DepartmentRepository departments;
  private final DoctorRepository doctors;
  private final PatientRepository patients;
  private final AppointmentRepository appointments;
  private final InvoiceRepository invoices;

  public HospitalService(
      DepartmentRepository departments,
      DoctorRepository doctors,
      PatientRepository patients,
      AppointmentRepository appointments,
      InvoiceRepository invoices
  ) {
    this.departments = departments;
    this.doctors = doctors;
    this.patients = patients;
    this.appointments = appointments;
    this.invoices = invoices;
  }

  @Transactional(readOnly = true)
  public List<DepartmentResponse> departments() {
    List<Doctor> allDoctors = doctors.findAll();
    return departments.findAll().stream()
        .sorted(Comparator.comparing(Department::getName))
        .map(department -> toDepartment(department, allDoctors))
        .toList();
  }

  public DepartmentResponse createDepartment(DepartmentRequest request) {
    Department department = new Department();
    applyDepartment(department, request);
    return toDepartment(departments.save(department), doctors.findAll());
  }

  public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
    Department department = department(id);
    applyDepartment(department, request);
    return toDepartment(departments.save(department), doctors.findAll());
  }

  public void deleteDepartment(Long id) {
    if (!departments.existsById(id)) {
      throw new NotFoundException("Department not found");
    }
    remove(() -> {
      departments.deleteById(id);
      departments.flush();
    }, "Move or remove doctors in this department before deleting it.");
  }

  @Transactional(readOnly = true)
  public List<DoctorResponse> doctors() {
    return doctors.findAll().stream()
        .sorted(Comparator.comparing(Doctor::getFullName))
        .map(this::toDoctor)
        .toList();
  }

  public DoctorResponse createDoctor(DoctorRequest request) {
    Doctor doctor = new Doctor();
    applyDoctor(doctor, request);
    return toDoctor(doctors.save(doctor));
  }

  public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
    Doctor doctor = doctor(id);
    applyDoctor(doctor, request);
    return toDoctor(doctors.save(doctor));
  }

  public void deleteDoctor(Long id) {
    if (!doctors.existsById(id)) {
      throw new NotFoundException("Doctor not found");
    }
    remove(() -> {
      doctors.deleteById(id);
      doctors.flush();
    }, "This doctor still has appointments and cannot be removed.");
  }

  @Transactional(readOnly = true)
  public List<PatientResponse> patients() {
    return patients.findAll().stream()
        .sorted(Comparator.comparing(Patient::getFullName))
        .map(this::toPatient)
        .toList();
  }

  public PatientResponse createPatient(PatientRequest request) {
    Patient patient = new Patient();
    patient.setRegisteredOn(LocalDate.now());
    applyPatient(patient, request);
    return toPatient(patients.save(patient));
  }

  public PatientResponse updatePatient(Long id, PatientRequest request) {
    Patient patient = patient(id);
    applyPatient(patient, request);
    return toPatient(patients.save(patient));
  }

  public void deletePatient(Long id) {
    if (!patients.existsById(id)) {
      throw new NotFoundException("Patient not found");
    }
    remove(() -> {
      patients.deleteById(id);
      patients.flush();
    }, "This patient still has appointments or invoices and cannot be removed.");
  }

  @Transactional(readOnly = true)
  public List<AppointmentResponse> appointments() {
    return appointments.findAll().stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentDate).thenComparing(Appointment::getAppointmentTime))
        .map(this::toAppointment)
        .toList();
  }

  public AppointmentResponse createAppointment(AppointmentRequest request) {
    Appointment appointment = new Appointment();
    applyAppointment(appointment, request);
    return toAppointment(appointments.save(appointment));
  }

  public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
    Appointment appointment = appointment(id);
    applyAppointment(appointment, request);
    return toAppointment(appointments.save(appointment));
  }

  public void deleteAppointment(Long id) {
    if (!appointments.existsById(id)) {
      throw new NotFoundException("Appointment not found");
    }
    appointments.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<InvoiceResponse> invoices() {
    return invoices.findAll().stream()
        .sorted(Comparator.comparing(Invoice::getIssuedOn, Comparator.nullsLast(Comparator.reverseOrder())))
        .map(this::toInvoice)
        .toList();
  }

  public InvoiceResponse createInvoice(InvoiceRequest request) {
    Invoice invoice = new Invoice();
    invoice.setInvoiceNo(nextInvoiceNo());
    invoice.setIssuedOn(LocalDate.now());
    applyInvoice(invoice, request);
    return toInvoice(invoices.save(invoice));
  }

  public InvoiceResponse updateInvoice(Long id, InvoiceRequest request) {
    Invoice invoice = invoice(id);
    applyInvoice(invoice, request);
    return toInvoice(invoices.save(invoice));
  }

  public InvoiceResponse payInvoice(Long id) {
    Invoice invoice = invoice(id);
    invoice.setStatus("PAID");
    invoice.setPaidOn(LocalDate.now());
    return toInvoice(invoices.save(invoice));
  }

  public void deleteInvoice(Long id) {
    if (!invoices.existsById(id)) {
      throw new NotFoundException("Invoice not found");
    }
    invoices.deleteById(id);
  }

  @Transactional(readOnly = true)
  public DashboardResponse dashboard() {
    LocalDate today = LocalDate.now();
    List<Patient> allPatients = patients.findAll();
    List<Doctor> allDoctors = doctors.findAll();
    List<Appointment> allVisits = appointments.findAll();
    List<Invoice> allInvoices = invoices.findAll();
    List<Department> allDepartments = departments.findAll();

    List<DepartmentLoad> loads = allDepartments.stream()
        .sorted(Comparator.comparing(Department::getName))
        .map(department -> new DepartmentLoad(
            department.getName(),
            allDoctors.stream().filter(doctor -> doctor.getDepartment().getId().equals(department.getId())).count(),
            allVisits.stream()
                .filter(visit -> "SCHEDULED".equals(visit.getStatus()))
                .filter(visit -> visit.getDoctor().getDepartment().getId().equals(department.getId()))
                .count()
        ))
        .toList();

    List<AppointmentResponse> upcoming = allVisits.stream()
        .filter(visit -> "SCHEDULED".equals(visit.getStatus()))
        .filter(visit -> !visit.getAppointmentDate().isBefore(today))
        .sorted(Comparator.comparing(Appointment::getAppointmentDate).thenComparing(Appointment::getAppointmentTime))
        .limit(6)
        .map(this::toAppointment)
        .toList();

    List<PatientResponse> recent = allPatients.stream()
        .sorted(Comparator.comparing(Patient::getRegisteredOn, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Patient::getId, Comparator.reverseOrder()))
        .limit(5)
        .map(this::toPatient)
        .toList();

    BigDecimal revenue = allInvoices.stream()
        .filter(invoice -> "PAID".equals(invoice.getStatus()))
        .map(Invoice::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new DashboardResponse(
        allPatients.size(),
        allPatients.stream().filter(patient -> "ADMITTED".equals(patient.getStatus())).count(),
        allDoctors.size(),
        allDoctors.stream().filter(doctor -> "AVAILABLE".equals(doctor.getStatus()) || "IN_ROUNDS".equals(doctor.getStatus())).count(),
        allVisits.stream().filter(visit -> today.equals(visit.getAppointmentDate()) && !"CANCELLED".equals(visit.getStatus())).count(),
        allInvoices.stream().filter(invoice -> "PENDING".equals(invoice.getStatus())).count(),
        revenue,
        loads,
        upcoming,
        recent
    );
  }

  private void applyDepartment(Department department, DepartmentRequest request) {
    department.setName(request.name().trim());
    department.setDescription(clean(request.description()));
    department.setFloor(clean(request.floor()));
    department.setPhone(clean(request.phone()));
  }

  private void applyDoctor(Doctor doctor, DoctorRequest request) {
    requireStatus(request.status(), DOCTOR_STATUS);
    doctor.setFullName(request.fullName().trim());
    doctor.setSpecialization(request.specialization().trim());
    doctor.setEmail(clean(request.email()));
    doctor.setPhone(request.phone().trim());
    doctor.setDepartment(department(request.departmentId()));
    doctor.setExperienceYears(request.experienceYears());
    doctor.setConsultationFee(request.consultationFee());
    doctor.setStatus(request.status());
  }

  private void applyPatient(Patient patient, PatientRequest request) {
    requireStatus(request.status(), PATIENT_STATUS);
    patient.setFullName(request.fullName().trim());
    patient.setAge(request.age());
    patient.setGender(request.gender().trim());
    patient.setEmail(clean(request.email()));
    patient.setPhone(request.phone().trim());
    patient.setBloodGroup(clean(request.bloodGroup()));
    patient.setAddress(clean(request.address()));
    patient.setRoom(clean(request.room()));
    patient.setStatus(request.status());
  }

  private void applyAppointment(Appointment appointment, AppointmentRequest request) {
    requireStatus(request.status(), VISIT_STATUS);
    appointment.setPatient(patient(request.patientId()));
    appointment.setDoctor(doctor(request.doctorId()));
    appointment.setAppointmentDate(request.appointmentDate());
    appointment.setAppointmentTime(request.appointmentTime());
    appointment.setReason(clean(request.reason()));
    appointment.setStatus(request.status());
  }

  private void applyInvoice(Invoice invoice, InvoiceRequest request) {
    requireStatus(request.status(), BILL_STATUS);
    invoice.setPatient(patient(request.patientId()));
    invoice.setDescription(request.description().trim());
    invoice.setAmount(request.amount());
    invoice.setStatus(request.status());
    if ("PAID".equals(request.status()) && invoice.getPaidOn() == null) {
      invoice.setPaidOn(LocalDate.now());
    }
    if ("PENDING".equals(request.status())) {
      invoice.setPaidOn(null);
    }
  }

  private DepartmentResponse toDepartment(Department department, List<Doctor> allDoctors) {
    long count = allDoctors.stream()
        .filter(doctor -> doctor.getDepartment().getId().equals(department.getId()))
        .count();
    return new DepartmentResponse(
        department.getId(),
        department.getName(),
        department.getDescription(),
        department.getFloor(),
        department.getPhone(),
        count
    );
  }

  private DoctorResponse toDoctor(Doctor doctor) {
    return new DoctorResponse(
        doctor.getId(),
        doctor.getFullName(),
        doctor.getSpecialization(),
        doctor.getEmail(),
        doctor.getPhone(),
        doctor.getDepartment().getId(),
        doctor.getDepartment().getName(),
        doctor.getExperienceYears(),
        doctor.getConsultationFee(),
        doctor.getStatus()
    );
  }

  private PatientResponse toPatient(Patient patient) {
    return new PatientResponse(
        patient.getId(),
        patient.getFullName(),
        patient.getAge(),
        patient.getGender(),
        patient.getEmail(),
        patient.getPhone(),
        patient.getBloodGroup(),
        patient.getAddress(),
        patient.getRoom(),
        patient.getStatus(),
        patient.getRegisteredOn()
    );
  }

  private AppointmentResponse toAppointment(Appointment appointment) {
    return new AppointmentResponse(
        appointment.getId(),
        appointment.getPatient().getId(),
        appointment.getPatient().getFullName(),
        appointment.getDoctor().getId(),
        appointment.getDoctor().getFullName(),
        appointment.getDoctor().getSpecialization(),
        appointment.getAppointmentDate(),
        appointment.getAppointmentTime(),
        appointment.getReason(),
        appointment.getStatus()
    );
  }

  private InvoiceResponse toInvoice(Invoice invoice) {
    return new InvoiceResponse(
        invoice.getId(),
        invoice.getInvoiceNo(),
        invoice.getPatient().getId(),
        invoice.getPatient().getFullName(),
        invoice.getDescription(),
        invoice.getAmount(),
        invoice.getStatus(),
        invoice.getIssuedOn(),
        invoice.getPaidOn()
    );
  }

  private Department department(Long id) {
    return departments.findById(id).orElseThrow(() -> new NotFoundException("Department not found"));
  }

  private Doctor doctor(Long id) {
    return doctors.findById(id).orElseThrow(() -> new NotFoundException("Doctor not found"));
  }

  private Patient patient(Long id) {
    return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
  }

  private Appointment appointment(Long id) {
    return appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
  }

  private Invoice invoice(Long id) {
    return invoices.findById(id).orElseThrow(() -> new NotFoundException("Invoice not found"));
  }

  private String nextInvoiceNo() {
    return "INV-" + (1000 + invoices.count() + 1);
  }

  private void requireStatus(String status, Set<String> allowed) {
    if (!allowed.contains(status)) {
      throw new IllegalArgumentException("Status must be one of " + allowed);
    }
  }

  private String clean(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private void remove(Runnable action, String message) {
    try {
      action.run();
    } catch (DataIntegrityViolationException ex) {
      throw new ConflictException(message);
    }
  }
}
