package cz.cernobilao.exception.demo.repository;

import cz.cernobilao.exception.demo.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
