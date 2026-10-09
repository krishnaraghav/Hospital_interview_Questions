package com.aethercare.hms.config;

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
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
  private final DepartmentRepository departments;
  private final DoctorRepository doctors;
  private final PatientRepository patients;
  private final AppointmentRepository appointments;
  private final InvoiceRepository invoices;

  public DataSeeder(
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

  @Override
  public void run(String... args) {
    if (departments.count() > 0) {
      return;
    }

    LocalDate today = LocalDate.now();

    Department cardio = dept("Cardiology", "Heart rhythm, imaging, and recovery wards.", "Level 3", "044-4101");
    Department neuro = dept("Neurology", "Stroke response and outpatient neurology.", "Level 4", "044-4102");
    Department ortho = dept("Orthopedics", "Joint care, trauma, and physiotherapy.", "Level 2", "044-4103");
    Department peds = dept("Pediatrics", "Child wellness, wards, and neonatal support.", "Level 5", "044-4104");
    Department emergency = dept("Emergency", "24-hour triage and acute care.", "Ground", "044-4100");
    Department oncology = dept("Oncology", "Day-care chemotherapy and consults.", "Level 6", "044-4105");
    Department medicine = dept("General Medicine", "Adult inpatient and clinic care.", "Level 1", "044-4106");
    dept("Radiology", "Imaging, ultrasound, and reporting.", "Lower 1", "044-4107");

    Doctor meera = doctor("Dr. Meera Iyer", "Interventional Cardiology", "meera.iyer@aethercare.example", "9800001101", cardio, 14, 1800, "AVAILABLE");
    Doctor sofia = doctor("Dr. Sofia Alvarez", "Cardiac Imaging", "sofia.alvarez@aethercare.example", "9800001102", cardio, 9, 1500, "IN_ROUNDS");
    Doctor arjun = doctor("Dr. Arjun Shah", "Neurology", "arjun.shah@aethercare.example", "9800001103", neuro, 11, 1600, "AVAILABLE");
    Doctor lena = doctor("Dr. Lena Ortiz", "Orthopedics", "lena.ortiz@aethercare.example", "9800001104", ortho, 16, 1700, "AVAILABLE");
    Doctor noah = doctor("Dr. Noah Rahman", "Pediatrics", "noah.rahman@aethercare.example", "9800001105", peds, 8, 1200, "AVAILABLE");
    Doctor priya = doctor("Dr. Priya Nair", "Emergency Medicine", "priya.nair@aethercare.example", "9800001106", emergency, 12, 1000, "IN_ROUNDS");
    Doctor elias = doctor("Dr. Elias Berg", "Medical Oncology", "elias.berg@aethercare.example", "9800001107", oncology, 18, 2200, "ON_LEAVE");
    Doctor hannah = doctor("Dr. Hannah Cole", "Internal Medicine", "hannah.cole@aethercare.example", "9800001108", medicine, 10, 900, "AVAILABLE");
    Doctor vikram = doctor("Dr. Vikram Desai", "General Medicine", "vikram.desai@aethercare.example", "9800001109", medicine, 7, 800, "AVAILABLE");

    Patient anika = patient("Anika Bose", 34, "Female", "anika.bose@email.example", "9900002201", "B+", "12 Lake View, Chennai", null, "OUTPATIENT", today.minusDays(1));
    Patient ravi = patient("Ravi Menon", 58, "Male", "ravi.menon@email.example", "9900002202", "O+", "44 Harbour Road, Kochi", "A-214", "ADMITTED", today.minusDays(3));
    Patient leila = patient("Leila Farooq", 41, "Female", "leila.farooq@email.example", "9900002203", "A+", "9 Palm Court, Hyderabad", "B-118", "ADMITTED", today.minusDays(2));
    Patient owen = patient("Owen Clarke", 7, "Male", "owen.clarke@email.example", "9900002204", "O-", "18 School Lane, Pune", null, "OUTPATIENT", today);
    Patient mira = patient("Mira Kapoor", 67, "Female", "mira.kapoor@email.example", "9900002205", "AB+", "2 Temple Street, Jaipur", "ICU-03", "ADMITTED", today.minusDays(6));
    Patient samir = patient("Samir Qureshi", 29, "Male", "samir.qureshi@email.example", "9900002206", "B-", "77 Market Road, Lucknow", null, "DISCHARGED", today.minusDays(12));
    Patient helen = patient("Helen Park", 52, "Female", "helen.park@email.example", "9900002207", "A-", "5 Cedar Block, Bengaluru", "C-306", "ADMITTED", today.minusDays(1));
    Patient dev = patient("Dev Sharma", 45, "Male", "dev.sharma@email.example", "9900002208", "O+", "31 Ring Road, Delhi", null, "OUTPATIENT", today.minusDays(4));
    Patient nora = patient("Nora Ibrahim", 23, "Female", "nora.ibrahim@email.example", "9900002209", "B+", "8 Garden Street, Mysuru", null, "OUTPATIENT", today);
    Patient joseph = patient("Joseph D'Souza", 73, "Male", "joseph.dsouza@email.example", "9900002210", "A+", "16 Church Road, Goa", null, "DISCHARGED", today.minusDays(20));

    visit(anika, meera, today, LocalTime.of(9, 30), "Chest pain review and ECG", "SCHEDULED");
    visit(ravi, sofia, today, LocalTime.of(11, 0), "Post-procedure echo", "SCHEDULED");
    visit(leila, arjun, today, LocalTime.of(13, 15), "Migraine follow-up", "SCHEDULED");
    visit(owen, noah, today, LocalTime.of(15, 0), "Fever and rash check", "SCHEDULED");
    visit(helen, lena, today.plusDays(1), LocalTime.of(10, 0), "Knee replacement consult", "SCHEDULED");
    visit(dev, hannah, today.plusDays(1), LocalTime.of(16, 30), "Diabetes review", "SCHEDULED");
    visit(mira, priya, today.minusDays(1), LocalTime.of(8, 10), "Emergency admission review", "COMPLETED");
    visit(samir, vikram, today.minusDays(10), LocalTime.of(12, 0), "Discharge summary", "COMPLETED");
    visit(nora, meera, today.plusDays(2), LocalTime.of(9, 0), "Palpitation screening", "SCHEDULED");
    visit(joseph, elias, today.plusDays(3), LocalTime.of(14, 0), "Oncology opinion", "CANCELLED");

    bill(anika, "INV-1001", "Cardiology consultation", "1800", "PENDING", today, null);
    bill(ravi, "INV-1002", "Ward stay and cardiac imaging", "24600", "PENDING", today.minusDays(1), null);
    bill(leila, "INV-1003", "Neurology admission day 1", "9800", "PENDING", today.minusDays(2), null);
    bill(owen, "INV-1004", "Pediatric clinic visit", "1200", "PAID", today, today);
    bill(mira, "INV-1005", "Emergency stabilization", "15400", "PAID", today.minusDays(6), today.minusDays(4));
    bill(samir, "INV-1006", "General medicine discharge", "6400", "PAID", today.minusDays(12), today.minusDays(11));
    bill(helen, "INV-1007", "Orthopedic pre-op panel", "8700", "PENDING", today, null);
  }

  private Department dept(String name, String description, String floor, String phone) {
    Department department = new Department();
    department.setName(name);
    department.setDescription(description);
    department.setFloor(floor);
    department.setPhone(phone);
    return departments.save(department);
  }

  private Doctor doctor(
      String name,
      String specialization,
      String email,
      String phone,
      Department department,
      int years,
      int fee,
      String status
  ) {
    Doctor doctor = new Doctor();
    doctor.setFullName(name);
    doctor.setSpecialization(specialization);
    doctor.setEmail(email);
    doctor.setPhone(phone);
    doctor.setDepartment(department);
    doctor.setExperienceYears(years);
    doctor.setConsultationFee(fee);
    doctor.setStatus(status);
    return doctors.save(doctor);
  }

  private Patient patient(
      String name,
      int age,
      String gender,
      String email,
      String phone,
      String bloodGroup,
      String address,
      String room,
      String status,
      LocalDate registeredOn
  ) {
    Patient patient = new Patient();
    patient.setFullName(name);
    patient.setAge(age);
    patient.setGender(gender);
    patient.setEmail(email);
    patient.setPhone(phone);
    patient.setBloodGroup(bloodGroup);
    patient.setAddress(address);
    patient.setRoom(room);
    patient.setStatus(status);
    patient.setRegisteredOn(registeredOn);
    return patients.save(patient);
  }

  private void visit(Patient patient, Doctor doctor, LocalDate date, LocalTime time, String reason, String status) {
    Appointment appointment = new Appointment();
    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setAppointmentDate(date);
    appointment.setAppointmentTime(time);
    appointment.setReason(reason);
    appointment.setStatus(status);
    appointments.save(appointment);
  }

  private void bill(Patient patient, String number, String description, String amount, String status, LocalDate issuedOn, LocalDate paidOn) {
    Invoice invoice = new Invoice();
    invoice.setPatient(patient);
    invoice.setInvoiceNo(number);
    invoice.setDescription(description);
    invoice.setAmount(new BigDecimal(amount));
    invoice.setStatus(status);
    invoice.setIssuedOn(issuedOn);
    invoice.setPaidOn(paidOn);
    invoices.save(invoice);
  }
}
