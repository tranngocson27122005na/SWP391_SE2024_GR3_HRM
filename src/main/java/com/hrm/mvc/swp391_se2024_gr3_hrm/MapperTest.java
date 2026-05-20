package com.hrm.mvc.swp391_se2024_gr3_hrm;


import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RoleMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.Reader;
import java.util.List;

public class MapperTest {
    public static void main(String[] args) {
        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {
                // Lấy mapper runtime
                RoleMapper mapper = session.getMapper(RoleMapper.class);

                // Gọi hàm selectAll
                List<Role> roles = mapper.selectAll();

                // In kết quả
                for (Role role : roles) {
                    System.out.println(role.getRoleId() + " - " + role.getRoleName()+" - "+role.getIsActive());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

