package com.hrm.mvc.swp391_se2024_gr3_hrm.service;

import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.PermissionMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.mapper.RoleMapper;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Permission;
import com.hrm.mvc.swp391_se2024_gr3_hrm.model.Role;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class PermissionService {
    public List<Permission> loadPermissions(){
        try {
            // Đọc file cấu hình MyBatis
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // Mở session
            try (SqlSession session = sqlSessionFactory.openSession()) {
                PermissionMapper permissionMapper = session.getMapper(PermissionMapper.class);
                List<Permission> permissions = permissionMapper.selectAll();
                return permissions;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Permission> loadPermissionsByRole(Integer roleId){
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                PermissionMapper permissionMapper = session.getMapper(PermissionMapper.class);
                return permissionMapper.selectByRoleId(roleId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // Hàm lấy permission theo ID
    public Permission findById(Integer permissionId) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            try (SqlSession session = sqlSessionFactory.openSession()) {
                PermissionMapper mapper = session.getMapper(PermissionMapper.class);
                return mapper.selectByPrimaryKey(permissionId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hàm cập nhật permission
    public boolean updatePermission(Permission permission) {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            // mở session với auto-commit
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                PermissionMapper mapper = session.getMapper(PermissionMapper.class);
                int rows = mapper.updateByPrimaryKey(permission);
                return rows > 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
