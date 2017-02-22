package com.getbetter.formsegment.Forms;

/**
 * Created by Hannah on 2/13/2017.
 */

public class FormChiefComp {

    // Basic Details
    int healthCenter;
    int personnelId;
    int date;

    // Patient Details
    String patientName;
    int age;
    char gender;

    // History of Present Illness
    String presentIllnessHistory;

    // Exploration of Symptoms
    boolean[] symptomExploration;
    String[] symptomExplorationRemarks;

    // Known Allergies
    String knownAllergies;

    // Others
    int height;
    int weight;
    int headCircumference;
    int temperature;
    int pulseRate;
    int resporatoryRate;
    int bloodPressureUpper, bloodPressureLower;

}
