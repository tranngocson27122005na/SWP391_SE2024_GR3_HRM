package com.hrm.mvc.swp391_se2024_gr3_hrm.mapper;

import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import java.util.List;

public interface AccountMapper {
    int deleteByPrimaryKey(Integer accountId);

    int insert(Account row);

    Account selectByPrimaryKey(Integer accountId);

    List<Account> selectAll();

    int updateByPrimaryKey(Account row);

    Account selectByUsername(String username);


}