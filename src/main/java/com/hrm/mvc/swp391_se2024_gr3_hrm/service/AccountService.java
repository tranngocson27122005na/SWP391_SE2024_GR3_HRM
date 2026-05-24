package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;

public class AccountService {

    public Account login(LoginForm form) {
        return SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account account = mapper.selectByUsername(form.getUsername());
            if (account == null) return null;
            if (!account.getPassword().equals(form.getPassword())) return null;
            if (Boolean.FALSE.equals(account.getIsActive())) return null;
            return account;
        });
    }

    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        return SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account account = mapper.selectByPrimaryKey(accountId);
            if (account == null) return false;
            if (!account.getPassword().equals(oldPassword)) return false;
            account.setPassword(newPassword);
            int rows = mapper.updateByPrimaryKey(account);
            return rows > 0;
        });
    }
}





