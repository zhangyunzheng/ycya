package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@Entity
@Table(name = "mon_car_apply_info",uniqueConstraints = {@UniqueConstraint(columnNames="yongCheNo")})
public class Order {


	  @Id
	  @GeneratedValue(strategy=GenerationType.AUTO)
	  private Integer id;
	  @Column(name = "yongCheNo")
	  private String yongCheNo;     //订单号
	  @Column(name = "applyDeptId")  //申请部门
	  private Integer applyDeptId; 
	  @Column(name = "applyNameId")  //申请账号
	  private Integer applyNameId;
	  @Column(name = "applyNamePhone")  //申请账号电话
	  private String applyNamePhone; 
	  @Column(name = "applyCarId")  //申请车辆id
	  private Integer applyCarId;
	  @Column(name = "applyDriverId")  //申请驾驶员id
	  private Integer applyDriverId; 
	  @Column(name = "applyManNum")  //申请用车人数
	  private Integer applyManNum; 
	  @Column(name = "applyTime")  //申请时间
	  private String applyTime; 
	  @Column(name = "applyFlowState")  //订单状态
	  private Integer applyFlowState; 
	  @Column(name = "applyUseAddress")  //出发地
	  private String applyUseAddress; 
	  @Column(name = "applyDestination")  //目的地
	  private String applyDestination; 
	  @Column(name = "yongCheRen")  //用车人
	  private String yongCheRen; 
	  @Column(name = "applyUsePhone")  //用车人电话
	  private String applyUsePhone;
	  @Column(name = "applyUseTime")  //用车人开始时间
	  private String applyUseTime;
	  @Column(name = "applyBackTime")  //用车人结束时间
	  private String applyBackTime;
	  @Column(name = "yongCheXz")  //用车性质
	  private String yongCheXz;
      @Column(name = "xsKm")  //行驶公里数
	  private Double xsKm;
      @Column(name = "applyRemark")  //用车事由
	  private String applyRemark;
  	  @Column(name = "applySxPople")  //用车信息
	  private String applySxPople;
	public String getYongCheNo() {
		return yongCheNo;
	}

	public void setYongCheNo(String yongCheNo) {
		this.yongCheNo = yongCheNo;
	}

	public Integer getApplyDeptId() {
		return applyDeptId;
	}

	public void setApplyDeptId(Integer applyDeptId) {
		this.applyDeptId = applyDeptId;
	}

	public Integer getApplyNameId() {
		return applyNameId;
	}

	public void setApplyNameId(Integer applyNameId) {
		this.applyNameId = applyNameId;
	}

	public String getApplyNamePhone() {
		return applyNamePhone;
	}

	public void setApplyNamePhone(String applyNamePhone) {
		this.applyNamePhone = applyNamePhone;
	}

	public Integer getApplyCarId() {
		return applyCarId;
	}

	public void setApplyCarId(Integer applyCarId) {
		this.applyCarId = applyCarId;
	}

	public Integer getApplyDriverId() {
		return applyDriverId;
	}

	public void setApplyDriverId(Integer applyDriverId) {
		this.applyDriverId = applyDriverId;
	}

	public Integer getApplyManNum() {
		return applyManNum;
	}

	public void setApplyManNum(Integer applyManNum) {
		this.applyManNum = applyManNum;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	/*public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}*/

	public Integer getApplyFlowState() {
		return applyFlowState;
	}

	public void setApplyFlowState(Integer applyFlowState) {
		this.applyFlowState = 8;
	}

	public String getApplyUseAddress() {
		return applyUseAddress;
	}

	public void setApplyUseAddress(String applyUseAddress) {
		this.applyUseAddress = applyUseAddress;
	}

	public String getApplyDestination() {
		return applyDestination;
	}

	public void setApplyDestination(String applyDestination) {
		this.applyDestination = applyDestination;
	}

	public String getYongCheRen() {
		return yongCheRen;
	}

	public void setYongCheRen(String yongCheRen) {
		this.yongCheRen = yongCheRen;
	}

	public String getApplyUsePhone() {
		return applyUsePhone;
	}

	public void setApplyUsePhone(String applyUsePhone) {
		this.applyUsePhone = applyUsePhone;
	}

	public String getApplyUseTime() {
		return applyUseTime;
	}

	public void setApplyUseTime(String applyUseTime) {
		this.applyUseTime = applyUseTime;
	}

	public String getApplyBackTime() {
		return applyBackTime;
	}

	public void setApplyBackTime(String applyBackTime) {
		this.applyBackTime = applyBackTime;
	}

	public String getYongCheXz() {
		return yongCheXz;
	}

	public void setYongCheXz(String yongCheXz) {
		this.yongCheXz = yongCheXz;
	}

	public Double getXsKm() {
		return xsKm;
	}

	public void setXsKm(Double xsKm) {
		this.xsKm = xsKm;
	}
	 public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}

	public String getApplySxPople() {
		return applySxPople;
	}

	public void setApplySxPople(String applySxPople) {
		this.applySxPople = applySxPople;
	} 

}
