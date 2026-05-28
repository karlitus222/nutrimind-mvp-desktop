package br.com.nutrimind.controller;

import br.com.nutrimind.dao.PatientDao;
import br.com.nutrimind.model.Patient;

import java.util.List;

public class PatientController {
    private final PatientDao patientDao;

    public PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    public List<Patient> listForNutritionist(long nutritionistId) {
        return patientDao.findByNutritionist(nutritionistId);
    }

    public List<Patient> listAll() {
        return patientDao.findAll();
    }

    public Patient save(Patient patient) {
        return patientDao.save(patient);
    }

    public void delete(long id) {
        patientDao.delete(id);
    }
}

