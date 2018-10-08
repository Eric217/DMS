package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import src.base.Result;
import src.base.ResultCache;
import src.dao.AdminDAO;
import src.model.Admin;

@Service
public class AdminService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminDAO adminDAO;

    public Boolean passwordRight(Long id, String input_password) {

        String encoded = adminDAO.getEncodedPassword(id);
        if (encoded == null || encoded.isEmpty())
            return false;
        return passwordEncoder.matches(input_password, encoded);
    }

    public Result addAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        try {
            adminDAO.addAdministrator(admin);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
        return ResultCache.OK;
    }

}
