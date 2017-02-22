package com.getbetter.formsegment.Forms;

/**
 * Created by Hannah on 2/13/2017.
 */

public class FormPatientInfo {

    // Basic Details
    int healthCenter;
    int personnelId;
    int date;

    // Patient Details
    String patientName;
    int philhealthNo;
    int birthDate;
    char gender;
    char maritalStatus;
    String bloodType;
    String address;
    int cellphoneNo;
    String guardianName;

    // Surgeries & Hospitalizations
    String surgeries;
    String hospitalizations;

    // Childhood Illnesses
    boolean[] childhoodIllnesses;
    String otherchildhoodIllnesses;

    // Present Illnesses
    boolean[] presentIllnesses;
    String otherPresentIllnesses;
    String maintenanceMed;

}
