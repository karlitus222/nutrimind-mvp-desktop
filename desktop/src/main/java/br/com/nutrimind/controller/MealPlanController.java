package br.com.nutrimind.controller;

import br.com.nutrimind.dao.MealPlanDao;
import br.com.nutrimind.model.MealPlan;

import java.util.List;

public class MealPlanController {
    private final MealPlanDao mealPlanDao;

    public MealPlanController(MealPlanDao mealPlanDao) {
        this.mealPlanDao = mealPlanDao;
    }

    public List<MealPlan> listForPatient(long patientId) {
        return mealPlanDao.findByPatient(patientId);
    }

    public void approve(long planId, long nutritionistId) {
        mealPlanDao.approve(planId, nutritionistId);
    }
}
