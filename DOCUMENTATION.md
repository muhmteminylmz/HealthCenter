# Health Center – Technical Documentation

> Deep-dive reference for developers working on the Health Center backend API.  
> For a quick-start guide see [README.md](README.md).

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Package Structure](#2-package-structure)
3. [Data Model](#3-data-model)
   - 3.1 [Abstract Base: `User`](#31-abstract-base-user)
   - 3.2 [Concrete User Entities](#32-concrete-user-entities)
   - 3.3 [Clinical Entities](#33-clinical-entities)
   - 3.4 [Reference / Catalogue Entities](#34-reference--catalogue-entities)
   - 3.5 [Relation Entity: `PatientDisease`](#35-relation-entity-patientdisease)
   - 3.6 [Enumerations](#36-enumerations)
   - 3.7 [Entity Relationship Summary](#37-entity-relationship-summary)
4. [Security Layer](#4-security-layer)
   - 4.1 [Authentication Flow](#41-authentication-flow)
   - 4.2 [JWT Internals](#42-jwt-internals)
   - 4.3 [`AuthTokenFilter`](#43-authtokenfilter)
   - 4.4 [`UserDetailsServiceImpl`](#44-userdetailsserviceimpl)
   - 4.5 [CORS & Whitelisted Endpoints](#45-cors--whitelisted-endpoints)
5. [Controller Layer](#5-controller-layer)
6. [Service Layer](#6-service-layer)
   - 6.1 [Domain Services](#61-domain-services)
   - 6.2 [Data-Loader Services](#62-data-loader-services)
7. [Repository Layer](#7-repository-layer)
8. [Payload (DTO) Layer](#8-payload-dto-layer)
   - 8.1 [Requests](#81-requests)
   - 8.2 [Responses](#82-responses)
9. [Utilities](#9-utilities)
10. [Exception Handling](#10-exception-handling)
11. [Configuration](#11-configuration)
12. [Static Front-End Assets](#12-static-front-end-assets)
13. [Design Decisions & Known Patterns](#13-design-decisions--known-patterns)
14. [Dependency Reference](#14-dependency-reference)

---

## 1. Architecture Overview

```
┌────────────────────────────────────────────────────────────────┐
│                        HTTP Clients                            │
│         (Browser / Swagger UI / Mobile / REST tools)           │
└──────────────────────────────┬─────────────────────────────────┘
                               │  HTTP / JSON
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Spring Security Filter Chain                   │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │  AuthTokenFilter  (OncePerRequestFilter)                 │    │
│  │  • Extracts JWT from Authorization: Bearer <token>       │    │
│  │  • Validates & decodes token → sets SecurityContext      │    │
│  └──────────────────────────────────────────────────────────┘    │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Controller Layer                            │
│  AuthController  │  AppointmentController  │  DoctorController   │
│  PatientController  │  ExaminationController  │  …               │
│  (@PreAuthorize role checks on each endpoint)                     │
└──────────────────────────────┬───────────────────────────────────┘
                               │  calls
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Service Layer                               │
│  AppointmentService │ PatientService │ DoctorService │ …         │
│  (Business logic, validation, DTO mapping)                        │
└──────────────────────────────┬───────────────────────────────────┘
                               │  uses
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      Repository Layer                             │
│  Spring Data JPA Repositories (one per entity)                    │
└──────────────────────────────┬───────────────────────────────────┘
                               │  SQL
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                          │
└──────────────────────────────────────────────────────────────────┘
```

The application follows a classic **layered architecture**:

| Layer | Responsibility |
|---|---|
| **Controller** | HTTP routing, request deserialisation, `@PreAuthorize` role guards |
| **Service** | Business rules, cross-entity validation, DTO ↔ entity mapping |
| **Repository** | Data access via Spring Data JPA (generated + custom JPQL queries) |
| **Entity** | JPA-managed domain objects persisted to PostgreSQL |
| **Security** | Stateless JWT authentication, BCrypt password hashing |

---

## 2. Package Structure

```
src/main/java/com/example/health_center/
│
├── HealthCenterApplication.java        # @SpringBootApplication entry point
│
├── config/
│   ├── CreateObjectBean.java           # @Configuration placeholder (extendable)
│   └── OpenAPIConfig.java              # Swagger/OpenAPI 3 meta-data
│
├── controller/                         # REST controllers (one per domain)
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── DoctorController.java
│   ├── ChiefDoctorController.java
│   ├── FamilyDoctorController.java
│   ├── NurseController.java
│   ├── PatientController.java
│   ├── AppointmentController.java
│   ├── ExaminationController.java
│   ├── PrescriptionController.java
│   ├── LabTestController.java
│   ├── MedicalReportController.java
│   ├── DiseaseController.java
│   ├── AllergyController.java
│   └── PatientDiseaseController.java
│
├── entity/
│   ├── abstracts/
│   │   └── User.java                   # @MappedSuperclass – shared user fields
│   ├── concretes/                      # JPA @Entity classes
│   │   ├── Admin.java
│   │   ├── Doctor.java
│   │   ├── ChiefDoctor.java
│   │   ├── FamilyDoctor.java
│   │   ├── Nurse.java
│   │   ├── Patient.java
│   │   ├── UserRole.java
│   │   ├── Appointment.java
│   │   ├── Examination.java
│   │   ├── Prescription.java
│   │   ├── LabTest.java
│   │   ├── MedicalReport.java
│   │   ├── Allergy.java
│   │   └── Disease.java
│   ├── enums/
│   │   ├── RoleType.java
│   │   ├── Gender.java
│   │   ├── BloodType.java
│   │   ├── AppointmentStatus.java
│   │   ├── CancellationReason.java
│   │   ├── TestType.java
│   │   └── TestStatus.java
│   └── relations/
│       └── PatientDisease.java         # Explicit join table with extra columns
│
├── exception/
│   ├── BadRequestException.java        # → HTTP 400
│   ├── ConflictException.java          # → HTTP 409
│   └── ResourceNotFoundException.java  # → HTTP 404
│
├── mapper/
│   └── MedicalReportMapper.java        # Manual entity ↔ DTO conversion
│
├── payload/
│   ├── request/
│   │   ├── abstracts/
│   │   │   └── BaseUserRequest.java    # Shared validation for user creation
│   │   ├── LoginRequest.java
│   │   ├── AdminRequest.java
│   │   ├── DoctorRequest.java
│   │   ├── PatientRequest.java
│   │   ├── NurseRequest.java
│   │   ├── ChiefDoctorRequest.java
│   │   ├── AppointmentRequest.java
│   │   ├── ExaminationRequest.java
│   │   ├── PrescriptionRequest.java
│   │   ├── LabTestRequest.java
│   │   ├── MedicalReportRequest.java
│   │   ├── PatientDiseaseRequest.java
│   │   └── AllergyIdsRequest.java
│   └── response/
│       ├── abstracts/
│       │   └── BaseUserResponse.java
│       ├── AuthResponse.java
│       ├── ResponseMessage.java        # Generic wrapper <E>
│       ├── AdminResponse.java
│       ├── DoctorResponse.java
│       ├── PatientResponse.java
│       ├── NurseResponse.java
│       ├── ChiefDoctorResponse.java
│       ├── FamilyDoctorResponse.java
│       ├── AppointmentResponse.java
│       ├── ExaminationResponse.java
│       ├── PrescriptionResponse.java
│       ├── LabTestResponse.java
│       ├── MedicalReportResponse.java
│       ├── AllergyResponse.java
│       ├── DiseaseResponse.java
│       └── PatientDiseaseResponse.java
│
├── repository/                         # Spring Data JPA interfaces
│
├── security/
│   ├── config/
│   │   └── WebSecurityConfig.java
│   ├── jwt/
│   │   ├── AuthEntryPointJwt.java      # 401 error handler
│   │   ├── AuthTokenFilter.java        # Per-request JWT validation filter
│   │   └── JwtUtils.java              # Token generation & validation
│   └── service/
│       ├── UserDetailsImpl.java        # Spring Security UserDetails adapter
│       └── UserDetailsServiceImpl.java # Multi-table user lookup
│
├── service/
│   ├── domain/                         # Business logic services
│   └── loader/                         # Startup CSV data loaders
│
└── utils/
    ├── CheckParameterUpdateMethod.java # Null-safe field update helper
    ├── FieldControl.java               # Cross-table uniqueness checks
    ├── Messages.java                   # Centralised error message strings
    └── TimeControl.java               # Time range validation helper
```

---

## 3. Data Model

### 3.1 Abstract Base: `User`

`User` is annotated `@MappedSuperclass` – it is never persisted directly but every concrete user type inherits its columns.

| Field | Type | Constraints |
|---|---|---|
| `id` | `Long` | PK, auto-generated |
| `username` | `String` | UNIQUE |
| `tc` | `String` | UNIQUE (Turkish national ID) |
| `name` | `String` | |
| `surname` | `String` | |
| `birthDate` | `LocalDate` | JSON format `yyyy-MM-dd` |
| `password` | `String` | Write-only (never serialised in responses) |
| `phoneNumber` | `String` | UNIQUE |
| `userRole` | `UserRole` | `@OneToOne`, write-only in JSON |
| `gender` | `Gender` (enum) | |

### 3.2 Concrete User Entities

#### `Admin`
Inherits all `User` fields. No additional domain fields.

#### `Doctor`
| Extra Field | Type | Notes |
|---|---|---|
| `isFamilyDoctor` | `boolean` | Flags the doctor as a family doctor |
| `dutyStartDate` | `LocalDate` | |
| `dutyEndDate` | `LocalDate` | |
| `startTime` | `LocalTime` | Shift start |
| `endTime` | `LocalTime` | Shift end |
| `appointment` | `List<Appointment>` | `@OneToMany`, cascade ALL |
| `labTest` | `List<LabTest>` | `@OneToMany`, cascade REMOVE |
| `familyDoctor` | `FamilyDoctor` | `@OneToOne` (inverse side) |
| `examination` | `List<Examination>` | `@OneToMany` |
| `prescriptions` | `List<Prescription>` | `@OneToMany` |
| `medicalReports` | `List<MedicalReport>` | `@OneToMany`, cascade ALL |

#### `ChiefDoctor`
Inherits `User`. No additional domain fields.

#### `FamilyDoctor`
A separate entity (not extending `User`) that acts as a **role wrapper** around a `Doctor`.

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `userRole` | `UserRole` | `@OneToOne` |
| `doctor` | `Doctor` | `@OneToOne` – the underlying Doctor |
| `patients` | `List<Patient>` | `@OneToMany` (mapped by `familyDoctor`) |

#### `Nurse`
| Extra Field | Type | Notes |
|---|---|---|
| `dutyStartDate` | `LocalDate` | |
| `dutyEndDate` | `LocalDate` | |
| `startTime` | `LocalTime` | |
| `endTime` | `LocalTime` | |
| `examinations` | `List<Examination>` | `@ManyToMany` via `nurse_examination_table` |

#### `Patient`
| Extra Field | Type | Notes |
|---|---|---|
| `bloodType` | `BloodType` (enum) | |
| `familyDoctor` | `FamilyDoctor` | `@ManyToOne`, JSON ignored |
| `allergies` | `List<Allergy>` | `@ManyToMany` via `patient_allergies` |
| `appointments` | `List<Appointment>` | `@OneToMany`, cascade REMOVE |
| `labTests` | `List<LabTest>` | `@OneToMany`, cascade REMOVE |
| `examination` | `List<Examination>` | `@OneToMany`, cascade REMOVE |
| `prescriptions` | `List<Prescription>` | `@OneToMany`, cascade REMOVE |
| `medicalReports` | `List<MedicalReport>` | `@OneToMany`, cascade ALL + orphanRemoval |

### 3.3 Clinical Entities

#### `Appointment`
| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `appointmentDate` | `LocalDateTime` | |
| `status` | `AppointmentStatus` | `SCHEDULED`, `CANCELLED`, `EXPIRED` |
| `cancellationReason` | `CancellationReason` | Optional |
| `patient` | `Patient` | `@ManyToOne`, JSON ignored |
| `doctor` | `Doctor` | `@ManyToOne`, JSON ignored |
| `examination` | `Examination` | `@OneToOne` (inverse) |

Conflict detection: the service blocks any booking within **±5 minutes** of an existing appointment for the same doctor **or** patient.

#### `Examination`
| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `examinationDate` | `LocalDateTime` | |
| `diagnosis` | `String` | Free-text diagnosis |
| `diseases` | `List<Disease>` | `@ManyToMany` via `examination_diseases` |
| `allergies` | `List<Allergy>` | `@ManyToMany` via `examination_allergies` |
| `patient` | `Patient` | `@ManyToOne` |
| `labTests` | `List<LabTest>` | `@OneToMany` (lazy) |
| `doctor` | `Doctor` | `@ManyToOne` |
| `nurses` | `List<Nurse>` | `@ManyToMany` (inverse, lazy) |
| `appointment` | `Appointment` | `@OneToOne` |

#### `Prescription`
| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `medicine` | `String` | |
| `dosage` | `String` | |
| `prescriptionNote` | `String` | |
| `followUpDate` | `LocalDate` | |
| `doctor` | `Doctor` | `@ManyToOne` |
| `patient` | `Patient` | `@ManyToOne` |

#### `LabTest`
| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `testType` | `TestType` | See enum |
| `testDate` | `LocalDateTime` | |
| `testResult` | `String` | |
| `status` | `TestStatus` | |
| `price` | `Integer` | |
| `examination` | `Examination` | `@ManyToOne` |
| `patient` | `Patient` | `@ManyToOne` |
| `doctor` | `Doctor` | `@ManyToOne` |

#### `MedicalReport`
| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `name` | `String` | Report title |
| `description` | `String` | |
| `reportDate` | `LocalDateTime` | Auto-set via `@PrePersist` |
| `startDate` | `LocalDate` | |
| `endDate` | `LocalDate` | |
| `diagnosis` | `Disease` | `@ManyToOne` – referenced disease |
| `patient` | `Patient` | `@ManyToOne` |
| `doctor` | `Doctor` | `@ManyToOne` |

### 3.4 Reference / Catalogue Entities

#### `Allergy`
Loaded from `allergies.csv` at startup.

| Field | Type |
|---|---|
| `id` | `Long` |
| `code` | `String` (unique) |
| `name` | `String` |

#### `Disease`
Loaded from `diseases.csv` at startup.

| Field | Type |
|---|---|
| `id` | `Long` |
| `code` | `String` (unique) |
| `name` | `String` |

### 3.5 Relation Entity: `PatientDisease`

An explicit join table (`patient_diseases`) that associates a patient with a disease and records when the diagnosis was made.

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | PK |
| `patient` | `Patient` | `@ManyToOne` (lazy) |
| `disease` | `Disease` | `@ManyToOne` (lazy) |
| `diagnosedAt` | `LocalDate` | |

### 3.6 Enumerations

| Enum | Values |
|---|---|
| `RoleType` | `ADMIN`, `PATIENT`, `DOCTOR`, `NURSE`, `CHIEF_DOCTOR`, `FAMILY_DOCTOR`, `GUESTUSER` |
| `Gender` | `MALE`, `FEMALE` |
| `BloodType` | `A_POSITIVE`, `A_NEGATIVE`, `B_POSITIVE`, `B_NEGATIVE`, `AB_POSITIVE`, `AB_NEGATIVE`, `O_POSITIVE`, `O_NEGATIVE` |
| `AppointmentStatus` | `SCHEDULED`, `CANCELLED`, `EXPIRED` |
| `CancellationReason` | (values defined in enum) |
| `TestType` | `BLOOD_TEST`, `URINE_TEST`, `IMAGING_TEST`, `BIOPSY_TEST`, `GENETIC_TEST`, `ALLERGY_TEST` |
| `TestStatus` | `PENDING`, `COMPLETED` (and others defined in enum) |

### 3.7 Entity Relationship Summary

```
User (abstract)
  ├── Admin
  ├── ChiefDoctor
  ├── Nurse ──────────────── examinations (M:N) ─── Examination
  ├── Doctor ──────────────── appointments  (1:N) ─── Appointment ─── Examination (1:1)
  │     └── FamilyDoctor (1:1 wrapper)           │
  └── Patient ─────────────── appointments  (1:N) ──┘
        ├── allergies     (M:N) ─── Allergy ◄── examination_allergies (M:N)
        ├── prescriptions (1:N) ─── Prescription
        ├── labTests      (1:N) ─── LabTest
        ├── examination   (1:N) ─── Examination ─── diseases (M:N) ─── Disease
        ├── medicalReports(1:N) ─── MedicalReport
        └── PatientDisease(1:N) ─── Disease
```

---

## 4. Security Layer

### 4.1 Authentication Flow

```
Client                     Server
  │                           │
  │  POST /auth/login          │
  │  { username, password }   │
  │ ──────────────────────────►│
  │                           │ AuthenticationManager.authenticate()
  │                           │   └─ DaoAuthenticationProvider
  │                           │         └─ UserDetailsServiceImpl.loadUserByUsername()
  │                           │               (queries Admin/Doctor/Nurse/Patient/ChiefDoctor tables)
  │                           │         └─ BCryptPasswordEncoder.matches()
  │                           │
  │                           │ JwtUtils.generateJwtToken()
  │                           │   (HS512, subject=username, claim: roles=[ROLE])
  │◄──────────────────────────│
  │  200 { token, username,   │
  │        name, role }       │
  │                           │
  │  GET /appointment/getAll  │
  │  Authorization: Bearer <token>
  │ ──────────────────────────►│
  │                           │ AuthTokenFilter.doFilterInternal()
  │                           │   ├─ parseJwt() → extract token
  │                           │   ├─ jwtUtils.validateJwtToken()
  │                           │   ├─ getUserNameFromJwtToken()
  │                           │   └─ UserDetailsServiceImpl.loadUserByUsername()
  │                           │        → SecurityContextHolder set
  │                           │
  │                           │ @PreAuthorize("hasAnyAuthority('ADMIN')")
  │◄──────────────────────────│
  │  200 [ ...appointments ]  │
```

### 4.2 JWT Internals

- **Library:** `io.jsonwebtoken:jjwt:0.9.1`
- **Algorithm:** `HS512` (HMAC-SHA-512)
- **Claims:**
  - `sub` → username
  - `roles` → `List<String>` (e.g. `["DOCTOR"]`)
  - `iat` → issued-at timestamp
  - `exp` → expiry (default 86 400 000 ms = 24 hours)
- **Secret:** configured via `backendapi.app.jwtSecret` in `application.properties`
- **Expiry:** configured via `backendapi.app.jwtExpirationMs`

`JwtUtils` validates tokens and catches all JJWT exceptions (`ExpiredJwtException`, `MalformedJwtException`, `SignatureException`, etc.), logging each case and returning `false`.

### 4.3 `AuthTokenFilter`

Extends `OncePerRequestFilter` – runs once per HTTP request.

Steps:
1. Reads the `Authorization` header and strips the `Bearer ` prefix.
2. If a token exists and is valid, decodes the username.
3. Loads `UserDetails` via `UserDetailsServiceImpl`.
4. Stores the username as a request attribute (`request.setAttribute("username", username)`) so controllers can read it without touching the `SecurityContext` directly.
5. Sets a `UsernamePasswordAuthenticationToken` in `SecurityContextHolder`.

### 4.4 `UserDetailsServiceImpl`

Since user data is split across five separate tables, `loadUserByUsername` queries **all five repositories** in sequence and returns the first non-null match:

```
ChiefDoctor → Doctor → Nurse → Patient → Admin
```

Each successful match constructs a `UserDetailsImpl` with the user's ID, username, display name, `isFamilyDoctor` flag, hashed password, and a single `GrantedAuthority` from the stored `RoleType`.

### 4.5 CORS & Whitelisted Endpoints

CORS is configured to allow all origins, headers, and methods (`*`) – suitable for development; tighten for production.

Endpoints that skip JWT authentication (`AUTH_WHITE_LIST`):
- `/auth/login`
- Swagger UI (`/swagger-ui/**`, `/v3/api-docs/**`)
- Static assets (`*.js`, `*.css`, `*.html`, `*.png`, etc.)

---

## 5. Controller Layer

All controllers are `@RestController` beans with constructor injection (Lombok `@RequiredArgsConstructor`). Role enforcement is done with `@PreAuthorize` at the method level (Spring Security method security, enabled via `@EnableGlobalMethodSecurity(prePostEnabled = true)`).

The currently-logged-in user's username is retrieved from the HTTP request attribute `"username"` (set by `AuthTokenFilter`) rather than from `SecurityContextHolder`, e.g.:

```java
String username = (String) httpServletRequest.getAttribute("username");
```

### Endpoint Summary

| Controller | Base Path | Key Operations |
|---|---|---|
| `AuthController` | `/auth` | `POST /login` |
| `AdminController` | `/admin` | CRUD for admin users |
| `DoctorController` | `/doctor` | CRUD; get by username; update |
| `ChiefDoctorController` | `/chiefDoctor` | CRUD |
| `FamilyDoctorController` | `/familyDoctor` | Get, delete; change patient's family doctor |
| `NurseController` | `/nurse` | CRUD; assign to examination |
| `PatientController` | `/patient` | CRUD; add allergies; change family doctor |
| `AppointmentController` | `/appointment` | Save (PATIENT); getAll (ADMIN); get own (PATIENT/DOCTOR); delete (ADMIN) |
| `ExaminationController` | `/examination` | Save (DOCTOR); getAll (ADMIN/CHIEF\_DOCTOR); get own by role; delete |
| `PrescriptionController` | `/prescription` | CRUD scoped by role |
| `LabTestController` | `/labTest` | CRUD; update status |
| `MedicalReportController` | `/medical-report` | CRUD scoped by role |
| `DiseaseController` | `/disease` | Get all / by id (reference data) |
| `AllergyController` | `/allergy` | Get all / by id (reference data) |
| `PatientDiseaseController` | `/patientDisease` | Add/view disease history for a patient |

---

## 6. Service Layer

### 6.1 Domain Services

Each service is a `@Service` bean with constructor injection. Responsibilities:

| Service | Key responsibilities |
|---|---|
| `AppointmentService` | Validates ±5-min booking conflicts; builds `AppointmentResponse` DTOs |
| `DoctorService` | Creates doctors; validates shift-time order via `TimeControl`; cross-table duplicate check via `FieldControl` |
| `PatientService` | Creates patients; manages allergy assignments; changes family doctor |
| `NurseService` | Creates nurses; assigns nurses to examinations |
| `ChiefDoctorService` | Creates chief doctors |
| `FamilyDoctorService` | Creates `FamilyDoctor` wrapper from a `Doctor`; manages patient-doctor assignment |
| `AdminService` | Creates admins |
| `ExaminationService` | Creates examinations linked to appointments; prevents duplicate appointment use |
| `PrescriptionService` | Creates and updates prescriptions |
| `LabTestService` | Creates lab tests; updates test status |
| `MedicalReportService` | Creates and deletes medical reports |
| `DiseaseService` | Read-only access to the disease catalogue |
| `AllergyService` | Read-only access to the allergy catalogue |
| `PatientDiseaseService` | Links a patient to a disease; prevents duplicates |
| `UserRoleService` | Finds or creates `UserRole` records by `RoleType` |

#### `AppointmentService` – conflict detection

```java
LocalDateTime fiveMinutesBefore = appointmentDate.minusMinutes(5);
LocalDateTime fiveMinutesAfter  = appointmentDate.plusMinutes(5);

boolean doctorHasConflict = appointmentRepository
    .existsByDoctorIdAndAppointmentDateBetween(doctorId, fiveMinutesBefore, fiveMinutesAfter);

boolean patientHasConflict = appointmentRepository
    .existsByPatientIdAndAppointmentDateBetween(patientId, fiveMinutesBefore, fiveMinutesAfter);
```

A `ConflictException` (HTTP 409) is thrown if either check is true.

### 6.2 Data-Loader Services

`AllergyLoaderService` and `DiseaseLoaderService` both implement `ApplicationRunner` so they execute automatically after the Spring context is fully started.

**Process:**
1. Open the bundled CSV file (`/allergies.csv` or `/diseases.csv`) from the classpath.
2. Fetch all existing codes from the database into a `Set` for O(1) deduplication.
3. Iterate CSV records; skip any entry whose code already exists.
4. Batch-save all new entries via `saveAll()`.

CSV format (no header row): `code,name`

---

## 7. Repository Layer

All repositories extend `JpaRepository<Entity, Long>` and gain standard CRUD operations for free. Custom query methods follow Spring Data naming conventions.

Notable custom methods:

| Repository | Method | Purpose |
|---|---|---|
| `AppointmentRepository` | `existsByDoctorIdAndAppointmentDateBetween` | Conflict detection |
| `AppointmentRepository` | `existsByPatientIdAndAppointmentDateBetween` | Conflict detection |
| `AppointmentRepository` | `findByDoctorUsername` | Doctor's own appointments |
| `AppointmentRepository` | `findByPatientUsername` | Patient's own appointments |
| `AllergyRepository` | `findAllCodes` | Returns all codes for deduplication on load |
| `AllergyRepository` | `existsByCode` | Per-record check during CSV import |
| `DiseaseRepository` | `findAllCodes` | Same pattern as allergy |
| `*Repository` | `existsByUsername` / `existsByTc` / `existsByPhoneNumber` | Used by `FieldControl` |
| `*Repository` | `findByUsernameEquals` | Used by `UserDetailsServiceImpl` |

---

## 8. Payload (DTO) Layer

DTOs decouple the HTTP API surface from the persistence model. All DTOs use Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`).

### 8.1 Requests

#### `BaseUserRequest` (abstract)
Common bean-validation constraints applied to every user-creation request:

| Field | Constraint |
|---|---|
| `username` | `@NotNull`, `@Size(4–16)`, non-blank pattern |
| `name` | `@NotNull`, `@Size(2–16)` |
| `surname` | `@NotNull`, `@Size(2–16)` |
| `birthDate` | `@NotNull`, `@Past` |
| `tc` | `@NotNull`, regex `^[1-9][0-9]{10}$` (11-digit Turkish ID) |
| `password` | `@NotNull`, `@Size(8–60)` |
| `phoneNumber` | `@NotNull`, `@Size(12)`, numeric format |
| `gender` | `@NotNull` |

#### `AppointmentRequest`

| Field | Constraint |
|---|---|
| `appointmentDate` | `@NotNull`, `@Future` |
| `patientId` | `@NotNull` |
| `doctorId` | `@NotNull` |

### 8.2 Responses

#### `ResponseMessage<E>` (generic wrapper)

```java
{
  "message": "Appointment Saved",
  "httpStatus": "OK",
  "object": { ...typed response... }
}
```

`@JsonInclude(NON_NULL)` ensures null fields are omitted from serialisation.

#### `AuthResponse`

Returned by `POST /auth/login`:

```json
{
  "username": "doctor01",
  "name": "Ali",
  "token": "<JWT>",
  "role": "DOCTOR",
  "isFamilyDoctor": "true"
}
```

`isFamilyDoctor` is only populated when `role == DOCTOR`.

---

## 9. Utilities

### `FieldControl`

Cross-table uniqueness check called before every user creation. Queries all five user-type repositories for matching `username`, `tc`, and `phoneNumber`. Throws `ConflictException` (HTTP 409) on the first match found.

### `TimeControl`

```java
public static boolean check(LocalTime start, LocalTime stop) {
    return start.isAfter(stop) || start.equals(stop);
}
```

Returns `true` if the shift time range is invalid (start ≥ stop). Used when creating/updating `Doctor` and `Nurse` records.

### `CheckParameterUpdateMethod`

Provides null-safe field assignment for update operations — if the incoming request value is null or empty the existing value is retained.

### `Messages`

A constants class centralising all error message strings used across exception throws, ensuring consistency:

```java
Messages.NOT_FOUND_APPOINTMENT_MESSAGE   // "Error : Appointment with this field %s not found"
Messages.APPOINTMENT_TIME_CONFLICT       // "Error : Your already have a appointment in same time between 5 minutes"
Messages.ALREADY_REGISTER_MESSAGE_USERNAME // "Error : User with username %s already registered"
// … etc.
```

---

## 10. Exception Handling

Custom exception classes are annotated with `@ResponseStatus` so Spring MVC automatically maps them to HTTP status codes without an explicit `@ExceptionHandler`.

| Exception | HTTP Status | Typical Trigger |
|---|---|---|
| `BadRequestException` | `400 Bad Request` | Invalid input not caught by bean validation |
| `ConflictException` | `409 Conflict` | Duplicate username / TC / phone; appointment time conflict; duplicate disease/allergy |
| `ResourceNotFoundException` | `404 Not Found` | Entity not found by ID or username |

Bean-validation errors (`@Valid` on controller parameters) produce `400` with field-level details, enabled by:

```properties
server.error.include-binding-errors=always
server.error.include-message=always
server.error.include-stacktrace=never
```

---

## 11. Configuration

### `application.properties`

| Property | Default / Example | Notes |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://…` | PostgreSQL JDBC URL |
| `spring.datasource.username` | `postgres` | DB user |
| `spring.datasource.password` | *(set securely)* | **Never commit production credentials** |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-creates/alters tables on startup |
| `backendapi.app.jwtSecret` | `healthcare` | HMAC-SHA-512 signing key |
| `backendapi.app.jwtExpirationMs` | `86400000` (24 h) | Token lifetime in milliseconds |

### `WebSecurityConfig`

Key beans registered:

| Bean | Purpose |
|---|---|
| `AuthTokenFilter` | JWT pre-processing filter |
| `DaoAuthenticationProvider` | Wires `UserDetailsServiceImpl` + `BCryptPasswordEncoder` |
| `PasswordEncoder` | `BCryptPasswordEncoder` instance |
| `AuthenticationManager` | Delegates to configured provider |
| `SecurityFilterChain` | Stateless session, disables CSRF, registers filter and entry point |
| `WebMvcConfigurer` | Wildcard CORS mappings |

### `OpenAPIConfig`

Configures SpringDoc / Swagger UI metadata (title, version, description). The interactive UI is available at `http://localhost:8080/swagger-ui/index.html` when the application is running.

---

## 12. Static Front-End Assets

The project bundles a lightweight HTML/JS front-end inside `src/main/resources/`:

```
static/
  index.html        – Landing page
  login.html        – Login form
  dashboard.html    – Main dashboard (current)
  dashboardeski.html – Archived older dashboard

templates/          – Role-scoped Thymeleaf-style HTML templates
  admin/            – Admin management views
  common/           – Shared views (nurses, patients, exams, lab tests…)
  doctor/           – Doctor workflow views
  nurse/            – Nurse views
  patient/          – Patient views

yedek/              – Archived/backup HTML pages (not in production routes)
```

These pages consume the REST API directly via JavaScript (`fetch` / `XMLHttpRequest`).

---

## 13. Design Decisions & Known Patterns

### Table-per-class without JPA inheritance
User types (`Admin`, `Doctor`, `Nurse`, `Patient`, `ChiefDoctor`) each map to their own database table via `@MappedSuperclass`. This avoids a single large table and keeps queries focused. The trade-off is that `UserDetailsServiceImpl` must query all five tables for every login.

### Username stored as request attribute
Rather than reading `SecurityContextHolder.getContext().getAuthentication()` in controllers, the filter stores the username in `request.setAttribute("username", username)`. This keeps controllers slightly simpler but means the attribute name `"username"` is an implicit contract between filter and controller.

### Manual DTO mapping
There is no MapStruct or ModelMapper dependency; mappings are done manually inside service methods or in `MedicalReportMapper`. This gives full control but requires more boilerplate.

### CSV seeding at startup
Reference data (allergies, diseases) is seeded via `ApplicationRunner` implementations. The idempotent design (code-based deduplication) means the application can be restarted safely without creating duplicate records.

### Generic `ResponseMessage<E>`
Most mutating endpoints return `ResponseMessage<SomeResponse>` which carries an HTTP status field in the body alongside the message and payload. This is in addition to the actual HTTP response code.

### Appointment conflict window
The 5-minute buffer on each side of an appointment (±5 min) means two appointments must be at least 10 minutes apart for the same doctor or patient.

---

## 14. Dependency Reference

| Dependency | Version | Purpose |
|---|---|---|
| `spring-boot-starter-web` | (managed) | Spring MVC REST |
| `spring-boot-starter-data-jpa` | (managed) | Hibernate / Spring Data |
| `spring-boot-starter-validation` | (managed) | Jakarta Bean Validation |
| `spring-boot-starter-security` | (managed) | Spring Security |
| `postgresql` | (managed) | JDBC driver |
| `io.jsonwebtoken:jjwt` | `0.9.1` | JWT generation & validation |
| `org.projectlombok:lombok` | `1.18.30` | Boilerplate code generation |
| `org.apache.poi:poi-ooxml` | `4.1.2` | Excel read/write support |
| `org.springdoc:springdoc-openapi-ui` | `1.6.9` | Swagger / OpenAPI 3 UI |
| `org.apache.commons:commons-csv` | `1.10.0` | CSV parsing for data loaders |
| `org.jetbrains:annotations` | `24.0.1` | IDE nullability annotations |
| `spring-boot-devtools` | (managed) | Live-reload in development |
| `spring-boot-starter-test` | (managed) | JUnit / Mockito testing |

---

*Generated for commit on branch `copilot/create-md-file-for-explanation`.*
