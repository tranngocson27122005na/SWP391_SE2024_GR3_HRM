package com.hrm.mvc.swp391_se2024_gr3_hrm.mapper;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.AccountWithProfile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AccountMapper {
    int deleteByPrimaryKey(Integer accountId);

    int insert(Account row);

    Account selectByPrimaryKey(Integer accountId);

    List<Account> selectAll();

    int updateByPrimaryKey(Account row);

    Account selectByUsername(String username);

    Account selectByEmail(String email);

    List<AccountWithProfile> selectAccountsWithProfile(@Param("offset") int offset,
                                                     @Param("limit") int limit,
                                                     @Param("keyword") String keyword);

    int countAccountsWithProfile(@Param("keyword") String keyword);

    AccountWithProfile selectAccountWithProfileById(@Param("accountId") Integer accountId);

    Account selectByCitizenId(@Param("citizenId") Integer citizenId);

    int insertGeneratedKey(Account row);

    int updatePassword(@Param("accountId") Integer accountId, @Param("password") String password);
}