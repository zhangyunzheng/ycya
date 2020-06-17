package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mon_user_info",uniqueConstraints = {@UniqueConstraint(columnNames="userName")})
public class User {
	  @Id
	//  @GeneratedValue
	  private Integer id;          //用户id
	  @Column(name = "userName")
	  private String userName;     //用户名称
	  
	  @Column(name = "uname")
	  private String uname;     //用户真实姓名
	  
	  @Column(name = "userType")
	  private Integer userType;         //用户类型 
	  
	  @Column(name = "userPhone")
	  private String userPhone;             // 区域id
			
	  @Column(name = "userDeptId")
	  private Integer userDeptId;      //上级部门的id
	  
	  public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public Integer getUserDeptId() {
		return userDeptId;
	}

	public void setUserDeptId(Integer userDeptId) {
		this.userDeptId = userDeptId;
	}

}
