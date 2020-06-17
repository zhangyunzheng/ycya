package ycya.xngc.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@Entity
@Table(name = "mon_car_info",uniqueConstraints = {@UniqueConstraint(columnNames="carNum")})
public class Car {

	  @Id
	  private Integer id;          //部门的id
	  @Column(name = "carNum")
	  private String carNum;       //车辆名称
	  @Column(name = "carType")
	  private Integer carType;      //车辆类型 
	  @Column(name = "carModel")
	  private Integer carModel;     // 车辆品牌
	  @Column(name = "carState")
	  private Integer carState;      // 车辆种类
	  @Column(name = "departmentId")
	  private Integer departmentId;   //部门的id
	  @Column(name = "carColor")
	  private Integer carColor;      //车辆的颜色
//	  @Column(name = "addTime")
//	  private Integer addTime;      //添加的时间
	  @Column(name = "carPrice")
	  private double carPrice;      //车辆价格
	  @Column(name = "carSeating")
	  private Integer carSeating;      //上级部门的id
	  @Column(name = "carPl")
	  private String carPl;      //上级部门的id
	  
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getCarNum() {
			return carNum;
		}
		public void setCarNum(String carNum) {
			this.carNum = carNum;
		}
		public Integer getCarType() {
			return carType;
		}
		public void setCarType(Integer carType) {
			this.carType = carType;
		}
		public Integer getCarModel() {
			return carModel;
		}
		public void setCarModel(Integer carModel) {
			this.carModel = carModel;
		}
		public Integer getCarState() {
			return carState;
		}
		public void setCarState(Integer carState) {
			this.carState = carState;
		}
		public Integer getDepartmentId() {
			return departmentId;
		}
		public void setDepartmentId(Integer departmentId) {
			this.departmentId = departmentId;
		}
		public Integer getCarColor() {
			return carColor;
		}
		public void setCarColor(Integer carColor) {
			this.carColor = carColor;
		}
//		public Integer getAddTime() {
//			return addTime;
//		}
//		public void setAddTime(Integer addTime) {
//			this.addTime = addTime;
//		}
		public double getCarPrice() {
			return carPrice;
		}
		public void setCarPrice(double carPrice) {
			this.carPrice = carPrice;
		}
		public Integer getCarSeating() {
			return carSeating;
		}
		public void setCarSeating(Integer carSeating) {
			this.carSeating = carSeating;
		}
		public String getCarPl() {
			return carPl;
		}
		public void setCarPl(String carPl) {
			this.carPl = carPl;
		}
	  
}
