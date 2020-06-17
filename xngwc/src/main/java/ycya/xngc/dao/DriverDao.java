package ycya.xngc.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.repository.JpaRepository;

import ycya.xngc.bean.Car;
import ycya.xngc.bean.Driver;

@Entity
@Table(name = "mon_user_info",uniqueConstraints = {@UniqueConstraint(columnNames="user_name")})
public interface DriverDao extends JpaRepository<Driver, Integer> {

}
