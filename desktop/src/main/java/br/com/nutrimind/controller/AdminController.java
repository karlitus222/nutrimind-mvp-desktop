package br.com.nutrimind.controller;

import br.com.nutrimind.config.AppConfig;
import br.com.nutrimind.dao.AlertDao;
import br.com.nutrimind.dao.StatsDao;
import br.com.nutrimind.dao.UserDao;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.User;
import br.com.nutrimind.service.JsonExportService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class AdminController {
    private final UserDao userDao;
    private final AlertDao alertDao;
    private final StatsDao statsDao;
    private final JsonExportService exportService;

    public AdminController(UserDao userDao, AlertDao alertDao, StatsDao statsDao, JsonExportService exportService) {
        this.userDao = userDao;
        this.alertDao = alertDao;
        this.statsDao = statsDao;
        this.exportService = exportService;
    }

    public boolean isAiConfigured() {
        return AppConfig.hasOpenAiKey();
    }

    public List<User> users() {
        return userDao.findAll();
    }

    public User createNutritionist(String name, String email, char[] password, String crn, String specialty) {
        return userDao.createNutritionist(name, email, password, crn, specialty);
    }

    public Map<String, Integer> dashboardCounts() {
        return statsDao.dashboardCounts();
    }

    public List<Alert> openAlerts() {
        return alertDao.findOpenAlerts();
    }

    public void decideAlert(long alertId, String action, String notes) {
        alertDao.decide(alertId, action, notes);
    }

    public Path exportJson() {
        return exportService.export();
    }
}

