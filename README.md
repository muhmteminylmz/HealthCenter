# Health Center – Backend REST API

A **Spring Boot** REST API that powers a health center management system. It handles authentication, user management for multiple staff roles, appointment scheduling, examinations, prescriptions, lab tests, medical reports, disease tracking, and allergy management.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [User Roles & Permissions](#user-roles--permissions)
- [Core Modules](#core-modules)
  - [Authentication](#authentication)
  - [User Management](#user-management)
  - [Appointments](#appointments)
  - [Examinations](#examinations)
  - [Prescriptions](#prescriptions)
  - [Lab Tests](#lab-tests)
  - [Medical Reports](#medical-reports)
  - [Diseases & Patient-Disease Tracking](#diseases--patient-disease-tracking)
  - [Allergies](#allergies)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)

---

## Overview

The Health Center application is a RESTful backend designed to digitise and centralise the core workflows of an outpatient health centre. Staff members (admins, chief doctors, doctors, family doctors, nurses) and patients each have their own set of secured endpoints that match their real-world responsibilities.

Key highlights:

- **Role-based access control** enforced at every endpoint with Spring Security.
- **JWT authentication** – stateless, token-based login.
- **Pre-loaded reference data** – allergy and disease catalogues are seeded automatically from bundled CSV files on application startup.
- **OpenAPI / Swagger UI** – interactive documentation available at runtime.
- **Excel / CSV support** via Apache POI and Commons CSV for data import/export utilities.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 2.7.12 |
| Web | Spring MVC (REST) |
| Security | Spring Security + JJWT (JWT) |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Utilities | Lombok, Apache POI, Apache Commons CSV |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven (Maven Wrapper included) |

---

## User Roles & Permissions

| Role | Description |
|---|---|
| `ADMIN` | Full system access: create/update/delete all entities, view all data |
| `CHIEF_DOCTOR` | View patients, doctors, examinations, prescriptions, lab tests |
| `DOCTOR` | Manage own appointments, examinations, prescriptions, lab tests, medical reports |
| `FAMILY_DOCTOR` | View own assigned patients |
| `NURSE` | View examinations assigned to their patients |
| `PATIENT` | Book appointments, view own records (examinations, prescriptions, lab tests, reports, allergies) |
| `GUESTUSER` | Unauthenticated / minimal access |

---

## Core Modules

### Authentication

**Endpoint prefix:** `/auth`

Patients and staff log in with a username and password. On successful login the server returns a signed JWT that must be sent in the `Authorization: Bearer <token>` header for all subsequent requests.

---

### User Management

The system manages six concrete user types that all share a common `User` base (id, username, Turkish national ID `tc`, name, surname, birth date, password, phone number, gender, role).

| Entity | Endpoint prefix | Created by |
|---|---|---|
| Admin | `/admin` | ADMIN |
| Doctor | `/doctor` | ADMIN |
| Chief Doctor | `/chiefDoctor` | ADMIN |
| Family Doctor | `/familyDoctor` | ADMIN |
| Nurse | `/nurse` | ADMIN |
| Patient | `/patient` | ADMIN |

**Patient extras:** blood type, assigned family doctor, allergy list.  
Patients can change their own assigned family doctor and add allergies to their own profile.

---

### Appointments

**Endpoint prefix:** `/appointment`

- **Patients** book appointments by choosing a doctor and a date/time.
- Each appointment has a **status** (e.g. pending, completed, cancelled) and an optional **cancellation reason**.
- **Doctors** can view their own upcoming/past appointments.
- **Admins** can view all appointments and delete them.
- A completed appointment can be linked to an **Examination** record.

---

### Examinations

**Endpoint prefix:** `/examination`

After an appointment, a doctor records the examination result. Each examination is linked one-to-one with an appointment.

- **Doctors** create and view their own examinations.
- **Patients** view their own examination history.
- **Nurses** view examinations relevant to their patients.
- **Admins / Chief Doctors** have full read and delete access.

---

### Prescriptions

**Endpoint prefix:** `/prescription`

Doctors issue prescriptions to patients during or after an examination.

- **Doctors** create and view their own prescriptions.
- **Patients** view their own prescriptions.
- **Admins / Chief Doctors** can read, update, and delete any prescription.

---

### Lab Tests

**Endpoint prefix:** `/labTest`

Doctors order laboratory tests for their patients.

Supported test types: `BLOOD_TEST`, `URINE_TEST`, `IMAGING_TEST`, `BIOPSY_TEST`, `GENETIC_TEST`, `ALLERGY_TEST`.

Each test has a **status** (`PENDING`, `COMPLETED`, etc.) that can be updated separately.

- **Doctors** create and view their own lab test orders.
- **Patients** view their own lab test results.
- **Admins / Chief Doctors** have full management access.

---

### Medical Reports

**Endpoint prefix:** `/medical-report`

Doctors write formal medical reports for patients (e.g. after an examination or a series of visits).

- **Doctors** create and view their own reports.
- **Patients** view reports written for them.
- **Admins / Doctors** can delete reports.

---

### Diseases & Patient-Disease Tracking

**Endpoint prefixes:** `/disease`, `/patientDisease`

A disease catalogue is pre-loaded from `diseases.csv` at startup. Doctors can link one or more diseases to a patient, creating a medical history record.

---

### Allergies

**Endpoint prefix:** `/allergy`

An allergy catalogue is pre-loaded from `allergies.csv` at startup. Patients can add known allergies to their own profile (identified by patient ID).

---

## Project Structure

```
src/main/java/com/example/health_center/
├── config/             # Spring beans and OpenAPI configuration
├── controller/         # REST controllers (one per domain entity)
├── entity/
│   ├── abstracts/      # Base User class
│   ├── concretes/      # JPA entities (Patient, Doctor, Appointment, …)
│   ├── enums/          # Enumerations (RoleType, BloodType, Gender, …)
│   └── relations/      # Join-table entities (PatientDisease)
├── exception/          # Custom exception classes
├── mapper/             # Entity ↔ DTO mappers
├── payload/
│   ├── request/        # Inbound DTOs
│   └── response/       # Outbound DTOs
├── repository/         # Spring Data JPA repositories
├── security/           # JWT utilities, filters, UserDetails implementation
├── service/
│   ├── domain/         # Business logic services
│   └── loader/         # CSV data-loader services (allergies, diseases)
└── utils/              # Shared helpers (field validation, time checks, messages)
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+ (or use the included `mvnw` wrapper)
- PostgreSQL database

### Configuration

Edit `src/main/resources/application.properties` and set your database connection details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/health_center_db
spring.datasource.username=<your_db_user>
spring.datasource.password=<your_db_password>
```

You can also change the JWT secret and expiry:

```properties
backendapi.app.jwtSecret=<your_secret>
backendapi.app.jwtExpirationMs=86400000
```

### Running the Application

```bash
# Using the Maven wrapper
./mvnw spring-boot:run

# Or build and run the JAR
./mvnw clean package
java -jar target/health_center-0.0.1-SNAPSHOT.jar
```

The server starts on **port 8080** by default.

---

## API Documentation

Once the application is running, the interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

The raw OpenAPI JSON specification is served at:

```
http://localhost:8080/v3/api-docs
```
