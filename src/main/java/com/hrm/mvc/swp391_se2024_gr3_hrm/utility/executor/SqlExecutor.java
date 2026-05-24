package com.hrm.mvc.swp391_se2024_gr3_hrm.utility.executor;

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
    /**
     * created by KienPT
     * Executes a database operation using a MyBatis mapper.
     *
     * @param mapperClass là lớp mapper.java cần gọi đến
     * autoCommit  ├── true: immediate commit after each operation
     *             ├── false: manual commit after the action
     * @param action a function defines the operation to perform with the mapper
     * @return of type R
     * @param <T>type of mapper interface
     * @param <R>type of result returned
     */
    public static <T, R> R executeTransaction(Class<T> mapperClass, Function<T, R> action) {
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
