package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.AccountWithProfile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.dto.form.LoginForm;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.ProfileMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RoleMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Profile;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlExecutor;
import com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor.SqlSessionTransaction;

import java.security.SecureRandom;
import java.util.List;

public class AccountService {

    public Account login(LoginForm form) {
        Account account = SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account found = mapper.selectByUsername(form.getUsername());
            if (found == null) return null;
            if (!found.getPassword().equals(form.getPassword())) return null;
            if (Boolean.FALSE.equals(found.getIsActive())) return null;
            return found;
        });
        if (account == null) {
            return null;
        }
        Role role = SqlExecutor.execute(RoleMapper.class, mapper -> mapper.selectByPrimaryKey(account.getRoleId()));
        if (role == null || Boolean.FALSE.equals(role.getIsActive())) {
            return null;
        }
        return account;
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

    public List<AccountWithProfile> getAllAccountsWithProfile(int offset, int limit, String keyword) {
        String search = keyword == null ? "" : keyword.trim();
        return SqlExecutor.execute(AccountMapper.class,
                mapper -> mapper.selectAccountsWithProfile(offset, limit, search));
    }

    public int countAccountsWithProfile(String keyword) {
        String search = keyword == null ? "" : keyword.trim();
        return SqlExecutor.execute(AccountMapper.class, mapper -> mapper.countAccountsWithProfile(search));
    }

    public AccountWithProfile getAccountWithProfileById(Integer accountId) {
        if (accountId == null) {
            return null;
        }
        return SqlExecutor.execute(AccountMapper.class, mapper -> mapper.selectAccountWithProfileById(accountId));
    }

    public boolean createAccount(Account account, Profile profile) {
        if (account == null || profile == null || profile.getCitizenId() == null) {
            return false;
        }
        return SqlSessionTransaction.executeTransaction(session -> {
            ProfileMapper profileMapper = session.getMapper(ProfileMapper.class);
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            RoleMapper roleMapper = session.getMapper(RoleMapper.class);

            if (accountMapper.selectByUsername(account.getUsername()) != null) {
                return false;
            }
            if (profileMapper.selectByPrimaryKey(profile.getCitizenId()) != null) {
                return false;
            }
            if (accountMapper.selectByCitizenId(profile.getCitizenId()) != null) {
                return false;
            }
            Role role = roleMapper.selectByPrimaryKey(account.getRoleId());
            if (role == null || Boolean.FALSE.equals(role.getIsActive())) {
                return false;
            }
            if (profile.getEmail() != null && profileMapper.countByEmailExceptCitizen(profile.getEmail(), null) > 0) {
                return false;
            }

            profileMapper.insert(profile);
            account.setCitizenId(profile.getCitizenId());
            if (account.getIsActive() == null) {
                account.setIsActive(true);
            }
            accountMapper.insertGeneratedKey(account);
            return true;
        });
    }

    public boolean updateAccountAndProfile(Account account, Profile profile) {
        if (account == null || account.getAccountId() == null || profile == null) {
            return false;
        }
        return SqlSessionTransaction.executeTransaction(session -> {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            ProfileMapper profileMapper = session.getMapper(ProfileMapper.class);
            RoleMapper roleMapper = session.getMapper(RoleMapper.class);

            Account existing = accountMapper.selectByPrimaryKey(account.getAccountId());
            if (existing == null) {
                return false;
            }
            Role role = roleMapper.selectByPrimaryKey(account.getRoleId());
            if (role == null || Boolean.FALSE.equals(role.getIsActive())) {
                return false;
            }
            if (profile.getEmail() != null
                    && profileMapper.countByEmailExceptCitizen(profile.getEmail(), profile.getCitizenId()) > 0) {
                return false;
            }

            account.setPassword(existing.getPassword());
            account.setCitizenId(existing.getCitizenId());
            accountMapper.updateByPrimaryKey(account);
            profile.setCitizenId(existing.getCitizenId());
            profileMapper.updateByPrimaryKey(profile);
            return true;
        });
    }

    public boolean toggleAccountActive(Integer accountId, boolean active) {
        if (accountId == null) {
            return false;
        }
        return SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account account = mapper.selectByPrimaryKey(accountId);
            if (account == null) {
                return false;
            }
            account.setIsActive(active);
            return mapper.updateByPrimaryKey(account) > 0;
        });
    }

    public String resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String newPassword = generateRandomPassword();
        boolean updated = SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account account = mapper.selectByEmail(email.trim());
            if (account == null) {
                return false;
            }
            return mapper.updatePassword(account.getAccountId(), newPassword) > 0;
        });
        return updated ? newPassword : null;
    }

    public boolean isUsernameTaken(String username, Integer exceptAccountId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return SqlExecutor.execute(AccountMapper.class, mapper -> {
            Account account = mapper.selectByUsername(username.trim());
            if (account == null) {
                return false;
            }
            return exceptAccountId == null || !exceptAccountId.equals(account.getAccountId());
        });
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
