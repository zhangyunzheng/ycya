package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mon_term_info",uniqueConstraints = {@UniqueConstraint(columnNames="termId")})
public class Term {
	  @Id
	  private Integer id;
	  @Column(name = "termId")
      private String termId;  // 终端号
	  @Column(name = "simNum")
	  private String simNum;  // 终端卡号
	  @Column(name = "termDeptId")
	  private Integer termDeptId;  //终端所属单位
	  @Column(name = "manufacturerInt")
	  private String manufacturerInt;   // 厂商
	  public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTermId() {
		return termId;
	}
	public void setTermId(String termId) {
		this.termId = termId;
	}
	public String getSimNum() {
		return simNum;
	}
	public void setSimNum(String simNum) {
		this.simNum = simNum;
	}
	public Integer getTermDeptId() {
		return termDeptId;
	}
	public void setTermDeptId(Integer termDeptId) {
		this.termDeptId = termDeptId;
	}
	public String getManufacturerInt() {
		return manufacturerInt;
	}
	public void setManufacturerInt(String manufacturerInt) {
		this.manufacturerInt = manufacturerInt;
	}

}
