package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public class SqlSessionTransaction {
    private static final SqlSessionFactory sqlSessionFactory;

    static {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing SqlSessionFactory", e);
        }
    }

    public static <T> T executeTransaction(Function<SqlSession, T> action) {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession(false);
            T result = action.apply(session);
            session.commit();
            return result;
        } catch (Exception e) {
            if (session != null) {
                session.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
