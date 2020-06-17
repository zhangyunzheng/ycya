package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mon_department_info",uniqueConstraints = {@UniqueConstraint(columnNames="deptName")})
public class Dept {
	@Id
//    @GeneratedValue
    private Integer id;          //部门的id
    @Column(name = "deptName")
    private String deptName;     //部门名称
    @Column(name = "deptType")
    private Integer deptType;         //部门的类型 
	@Column(name = "qyId")
    private Integer qyId;             // 区域id
	@Column(name = "deptSuperId")
    private Integer deptSuperId;      //上级部门的id
	@Column(name = "fwType")
    private Integer fwType;      //fwtype
	public Integer getFwType() {
		return fwType;
	}
	public void setFwType(Integer fwType) {
		this.fwType = fwType;
	}
	public Integer getDeptSuperId() {
		return deptSuperId;
	}
	public void setDeptSuperId(Integer deptSuperId) {
		this.deptSuperId = deptSuperId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Integer getDeptType() {
		return deptType;
	}
	public void setDeptType(Integer deptType) {
		this.deptType = deptType;
	}
	public Integer getQyId() {
		return qyId;
	}
	public void setQyId(Integer qyId) {
		this.qyId = qyId;
	}
}
