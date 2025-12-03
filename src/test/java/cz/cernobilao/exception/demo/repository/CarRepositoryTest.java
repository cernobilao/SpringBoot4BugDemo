package cz.cernobilao.exception.demo.repository;

import cz.cernobilao.exception.demo.entity.Car;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.testutil.TestForIssue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CarRepositoryTest {


    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Autowired
    private CarRepository carRepository;

    @Test
    @TestForIssue(jiraKey = "HHH-19970")
    void testSQLExceptionInvalidArgumentsInCall() {
        Car car = new Car();
        car.setSpecs("{}");

        carRepository.save(car);
        carRepository.save(car);

        validator.validate(car);
    }

}