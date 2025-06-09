package com.example.health_center.utils;

public class Messages {

    public static final String APPOINTMENT_TIME_CONFLICT =
            "Error : Your already have a appointment in same time between 5 minutes";

    public static final String ALREADY_REGISTER_MESSAGE_USERNAME =
            "Error : User with username %s already registered";

    public static final String ALREADY_REGISTER_MESSAGE_TC =
            "Error : User with tc %s already registered";

    public static final String ALREADY_REGISTER_MESSAGE_PHONE_NUMBER =
            "Error : User with phone number %s already registered";

    public static final String NOT_PERMITTED_METHOD_MESSAGE =
            "You don't have any permission to change this value";

    public static final String NOT_FOUND_USER_MESSAGE =
            "Error : User not found";

    public static final String NOT_FOUND_USER2_MESSAGE =
            "Error : User not found with id %s";

    public static final String ALREADY_REGISTER_LESSON_MESSAGE =
            "Error : Lesson with lesson name %s already registered";

    public static final String NOT_FOUND_APPOINTMENT_MESSAGE =
            "Error : Appointment with this field %s not found";

    public static final String NOT_FOUND_FAMILYDOCTOR_FOR_ASSIGN =
            "Error : There is no family doctor assigned to the patient";

    public static final String NOT_FOUND_ANOTHER_FAMILYDOCTOR_TO_ASSIGN =
            "Error : There is no another family doctor assigned to the patient";

    public static final String TIME_NOT_VALID_MESSAGE =
            "Error : incorrect time";

    public static final String NOT_FOUND_EXAMINATION_MESSAGE =
            "Error : Examination not found";

    public static final String NOT_FOUND_PRESCRIPTION_MESSAGE =
            "Error : Prescription with id %s not found";

    public static final String APPOINTMENT_NOT_HAS_MESSAGE =
            "Error : %s not have appointment for create examination";

    public static final String EXAMINATION_NOT_FOUND_MESSAGE =
            "Error : Examination with id %s not found";

    public static final String USERNAME_EXIST_MESSAGE =
            "Error : This username '%s' has already been taken";

    public static final String EXAMINATION_NOT_EXIST_MESSAGE =
            "Error : This examination with id '%s' not exists";

    public static final String NOT_ENOUGH_NURSE_MESSAGE =
            "Error : Not enough nurses could be found";

    public static final String NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME =
            "Error : Advisor Teacher with username %s not found";

    public static final String DOCTOR_NOT_FOUND_MESSAGE =
            "Error : Doctor not found";

    public static final String STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID =
            "Error : Student Info with student id %d not found";

    public static final String MEET_EXIST_MESSAGE =
            "Error: Meet not found";

    public static final String  LABTEST_NOT_FOUND_MESSAGE =
            "Error: Lab Test with id %d not found";

    public static final String INCORRECT_APPOINTMENT_MESSAGE =
            "Error: %s does not have such an appointment";
    public static final String DOCTOR_HAS_NO_EXAMINATIONS_MESSAGE =
            "Error: %s has no examination";

    public static final String INCORRECT_EXAMINATION_MESSAGE =
            "Error: %s does not have such an examination";
    public static final String ALLERGY_NOT_FOUND_MESSAGE =
            "Error: %s id allergy not found";

    public static final String DISEASE_NOT_FOUND_MESSAGE =
            "Error: %s id disease not found";
    public static final String ALLERGY_DISEASE_DUPLICATE_MESSAGE =
            "Error: Allergy and disease can not be duplicated";

    public static final String ALLERGY_DUPLICATE_MESSAGE =
            "Error: Allergy can not be duplicated";
    public static final String DISEASE_ALREADY_ADDED =
            "Error: Disease already added for this patient";
    public static final String PATIENT_ALREADY_HAS_THIS_ALLERGIES =
            "Error: Patient already has these allergies: ";
    public static final String NOT_FOUND_MEDICAL_REPORT =
            "Error: Medical Report with id %s not found";
    public static final String APPOINTMENT_DUPLICATE_MESSAGE =
            "Error: Same appointment cant be use for 2 examinations";
}