package com.hrm.infrastructure.persistence.executor;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public class SqlExecutor {
    private static final SqlSessionFactory sqlSessionFactory;
    static {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing SqlSessionFactory", e);
        }
    }
    
    public static <T, R> R execute(Class<T> mapperClass, Function<T, R> action) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            T mapper = session.getMapper(mapperClass);
            return action.apply(mapper);
        }
    }
    public static <T> T executeTransaction(Function<SqlSession, T> action) {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession(false);
            T result = action.apply(session);
            session.commit();
            return result;
        } catch (RuntimeException e) {
            if (session != null) {
                session.rollback();
            }
            throw e;
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
    public static <T, R> R executeMapper(Class<T> mapperClass, Function<T, R> action) {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession(false);
            T mapper = session.getMapper(mapperClass);

            R result = action.apply(mapper);

            session.commit();
            return result;

        } catch (Exception e) {
            session.rollback();
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static void main(String[] args) {

    }
}
