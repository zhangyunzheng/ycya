package ycya.xngc.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.repository.JpaRepository;

import ycya.xngc.bean.Car;
import ycya.xngc.bean.Dept;

@Entity
@Table(name = "mon_department_info",uniqueConstraints = {@UniqueConstraint(columnNames="dept_name")})
public interface DeptDao extends JpaRepository<Dept, Integer> {

}
