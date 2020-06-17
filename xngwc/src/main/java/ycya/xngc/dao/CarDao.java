package ycya.xngc.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.repository.JpaRepository;
import ycya.xngc.bean.Car;

@Entity
@Table(name = "mon_car_info",uniqueConstraints = {@UniqueConstraint(columnNames="car_num")})
public interface CarDao extends JpaRepository<Car, Integer> {
}