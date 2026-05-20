package controller.Common;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.AccountMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Account;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import service.AccountService;

import java.io.IOException;
import java.io.Reader;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        AccountService accountService = new AccountService();
        Account account = new Account();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Nếu đã login rồi thì redirect về home
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Validate input rỗng
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
            return;
        }
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            Account account = mapper.selectByUsername(username);

            // Kiểm tra tài khoản tồn tại, mật khẩu đúng và đang active
            if (account == null || !account.getPassword().equals(password)) {
                req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
                req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
                return;
            }

            if (Boolean.FALSE.equals(account.getIsActive())) {
                req.setAttribute("error", "Tài khoản đã bị vô hiệu hóa.");
                req.getRequestDispatcher("/view/common/login.jsp").forward(req, resp);
                return;
            }

            // Lưu account vào session
            HttpSession ses = req.getSession();
            ses.setAttribute("account", account);
            ses.setMaxInactiveInterval(30 * 60); // 30 phút

            resp.sendRedirect(req.getContextPath() + "/home");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi hệ thống. Vui lòng thử lại.");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        }
    }
}
