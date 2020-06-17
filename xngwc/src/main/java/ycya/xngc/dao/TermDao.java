package ycya.xngc.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.repository.JpaRepository;

import ycya.xngc.bean.Car;
import ycya.xngc.bean.Term;

@Entity
@Table(name = "mon_term_info",uniqueConstraints = {@UniqueConstraint(columnNames="term_id")})
public interface TermDao extends JpaRepository<Term, Integer> {

}
