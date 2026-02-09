package cz.cernobilao.exception.demo.repository;

import cz.cernobilao.exception.demo.entity.Car;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.validator.testutil.TestForIssue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRepositoryTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setUp() {
        Properties props = loadApplicationProperties();
        ensureCarTableExists(props);

        Map<String, Object> settings = Map.of(
                AvailableSettings.DRIVER, props.getProperty("spring.datasource.driver-class-name"),
                AvailableSettings.URL, props.getProperty("spring.datasource.url"),
                AvailableSettings.USER, props.getProperty("spring.datasource.username"),
                AvailableSettings.PASS, props.getProperty("spring.datasource.password"),
                AvailableSettings.DIALECT, "org.hibernate.dialect.OracleDialect",
                AvailableSettings.HBM2DDL_AUTO, "none",
                AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy",
                AvailableSettings.USE_SQL_COMMENTS, props.getProperty("spring.jpa.properties.hibernate.use_sql_comments")
        );

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(Car.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-19970")
    void testSQLExceptionInvalidArgumentsInCall() {
        Car car = new Car();
        car.setSpecs("{}");

        inTransaction(session -> session.persist(car));
        Long carId = readId(car);

        Throwable thrown = assertThrows(Throwable.class, () -> inTransaction(session -> session.merge(car)));
        assertTrue(hasMessage(thrown, "ORA-17068"), "Expected ORA-17068 in exception cause chain.");

        inTransaction(session -> session.createMutationQuery("delete from Car c where c.id = :id")
                .setParameter("id", carId)
                .executeUpdate());
    }

    private static Properties loadApplicationProperties() {
        try (InputStream input = CarRepositoryTest.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalStateException("application.properties not found on classpath");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void ensureCarTableExists(Properties props) {
        String ddl = """
                DECLARE
                    v_count NUMBER := 0;
                BEGIN
                    SELECT COUNT(*) INTO v_count FROM user_tables WHERE table_name = 'CAR';
                    IF v_count = 0 THEN
                        EXECUTE IMMEDIATE 'CREATE TABLE CAR (
                            ID NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL ENABLE,
                            SPECS VARCHAR2(36 CHAR),
                            MANUFACTURE_DATE DATE,
                            CONSTRAINT CAR_PK PRIMARY KEY (ID) ENABLE
                        )';
                    END IF;
                END;
                """;

        String driver = props.getProperty("spring.datasource.driver-class-name");
        String url = props.getProperty("spring.datasource.url");
        String user = props.getProperty("spring.datasource.username");
        String pass = props.getProperty("spring.datasource.password");

        try {
            Class.forName(driver);
            try (Connection connection = DriverManager.getConnection(url, user, pass);
                 Statement statement = connection.createStatement()) {
                statement.execute(ddl);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException("Unable to create CAR table for test setup", ex);
        }
    }

    private static void inTransaction(Consumer<Session> operation) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                operation.accept(session);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw ex;
            }
        }
    }

    private static boolean hasMessage(Throwable throwable, String expectedText) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(expectedText)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static Long readId(Car car) {
        try {
            Field idField = Car.class.getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(car);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException("Unable to read Car.id for cleanup", ex);
        }
    }

}
