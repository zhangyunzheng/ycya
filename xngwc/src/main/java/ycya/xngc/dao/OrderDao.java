package ycya.xngc.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.repository.JpaRepository;

import ycya.xngc.bean.Order;

@Entity
@Table(name = "mon_car_apply_info",uniqueConstraints = {@UniqueConstraint(columnNames="yong_che_no")})
public interface OrderDao extends JpaRepository<Order, Integer> {

}
