package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RoleMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class RoleService {
    public List<Role> loadRoles(){
        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {
                RoleMapper roleMapper = session.getMapper(RoleMapper.class);
                List<Role> roles = roleMapper.selectAll();
                return roles;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        RoleService roleService = new RoleService();
        List<Role> roles = roleService.loadRoles();

        System.out.println("Danh sách Role:");
        for (Role role : roles) {
            System.out.println("ID: " + role.getRoleId()
                    + ", Name: " + role.getRoleName()
                    + ", Active: " + role.getIsActive());
        }
    }
}
