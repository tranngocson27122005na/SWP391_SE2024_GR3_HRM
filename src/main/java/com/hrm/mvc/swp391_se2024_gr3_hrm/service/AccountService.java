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
}





