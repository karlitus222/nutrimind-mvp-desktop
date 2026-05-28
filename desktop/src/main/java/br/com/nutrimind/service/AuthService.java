package br.com.nutrimind.service;

import br.com.nutrimind.dao.AuditLogDao;
import br.com.nutrimind.dao.UserDao;
import br.com.nutrimind.exception.AppException;
import br.com.nutrimind.model.User;
import br.com.nutrimind.util.PasswordUtil;

public class AuthService {
    private final UserDao userDao;
    private final AuditLogDao auditLogDao;

    public AuthService(UserDao userDao, AuditLogDao auditLogDao) {
        this.userDao = userDao;
        this.auditLogDao = auditLogDao;
    }

    public User login(String email, char[] password) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new AppException("Usuário ou senha inválidos."));
        if (!user.isActive() || !PasswordUtil.verify(password, user.getPasswordHash())) {
            auditLogDao.log(user.getId(), "LOGIN_NEGADO", "Falha de autenticação para " + email);
            throw new AppException("Usuário ou senha inválidos.");
        }
        auditLogDao.log(user.getId(), "LOGIN_OK", "Usuário autenticado.");
        return user;
    }
}

