package  service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import org.apache.ibatis.session.SqlSession;

public class AccountService {

    public Account authenticate(String username, String password) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            Account account = mapper.selectByUsername(username);

            if (account == null) return null;                          // username không tồn tại
            if (!account.getPassword().equals(password)) return null;  // sai mật khẩu
            if (Boolean.FALSE.equals(account.getIsActive())) return null; // bị khóa

            return account;
        }
    }

    public static void main(String[] args) {
        AccountService service = new AccountService();

        testLogin(service, "admin1",  "adminpass1");  // đúng
        testLogin(service, "admin",  "wrongpw"); // sai mật khẩu
        testLogin(service, "ghost",  "123456");  // username không tồn tại
    }

    static void testLogin(AccountService service, String username, String password) {
        System.out.println("--- Test: username=" + username + " | password=" + password);
        Account account = service.authenticate(username, password);
        if (account == null) {
            System.out.println("    => FAIL (sai thông tin hoặc tài khoản bị khóa)");
        } else {
            System.out.println("    => OK | accountId=" + account.getAccountId()
                    + " | roleId=" + account.getRoleId()
                    + " | isActive=" + account.getIsActive());
        }
    }
}
