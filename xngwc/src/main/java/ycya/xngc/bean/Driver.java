package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mon_user_info",uniqueConstraints = {@UniqueConstraint(columnNames="userName")})
public class Driver {
	  @Id
	  private Integer id;
	  @Column(name = "userName")
      private String driverName;  // 驾驶员姓名 
	  @Column(name = "userSex")
      private Integer driverSex;  // 驾驶员性别
	  @Column(name = "userDeptId")
      private Integer driverDeptId;  //驾驶员部门
	  @Column(name = "userPhone")
      private String driverPhone;   //驾驶员号码
	  @Column(name = "sfNo")
      private String sfNo;        // 身份证号
	  @Column(name = "isType")
      private Integer isType;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	  public String getDriverName() {
			return driverName;
		}
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}
		public Integer getDriverSex() {
			return driverSex;
		}
		public void setDriverSex(Integer driverSex) {
			this.driverSex = driverSex;
		}
		public Integer getDriverDeptId() {
			return driverDeptId;
		}
		public void setDriverDeptId(Integer driverDeptId) {
			this.driverDeptId = driverDeptId;
		}
		public String getDriverPhone() {
			return driverPhone;
		}
		public void setDriverPhone(String driverPhone) {
			this.driverPhone = driverPhone;
		}
	public String getSfNo() {
		return sfNo;
	}
	public void setSfNo(String sfNo) {
		this.sfNo = sfNo;
	}
	public Integer getIsType() {
		return isType;
	}
	public void setIsType(Integer isType) {
		this.isType = 1;
	}
      
}
