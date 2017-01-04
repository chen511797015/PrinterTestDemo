package cn.pax.bean;


import java.util.ArrayList;

import cn.pax.db.Goods;

/**
 * @author cheng
 * @date 2015/4/14 12:16
 */
public class OrderInfo {

    private long id;
    private long bookId;
    private String pName;
    private String mobile;
    private String address;
    private int status;
    private String createTime = "";
    private String deliveryTime = "";
    private String settingTime = ""; // 结算时间
    private double maling; // 抹零价，若为0表示没有抹零操作
    private int isMaling; // 抹零标志：0-无抹零；1-抹元；2-抹角 add by ljp 20151117
    private double realPay; // 实收
    private double change; // 找零
    private String serviceStaff; // 导购员
    private boolean wmFlag; // 外卖标志
    private double total;
    private double discount;
    private double paidAmount;
    private double advance;
    private boolean dispatch;
    private boolean ispay;
    private String payMode;
    private int flag;
    private double orderDiscount = 1.0d;//整单折扣

    public Double getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(Double orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    private ArrayList<Goods> goodsInfoList;

    public boolean isWmFlag() {
        return wmFlag;
    }

    public void setWmFlag(boolean wmFlag) {
        this.wmFlag = wmFlag;
    }

    public String getServiceStaff() {
        return serviceStaff;
    }

    public void setServiceStaff(String serviceStaff) {
        this.serviceStaff = serviceStaff;
    }

    public double getMaling() {
        return maling;
    }

    public double getRealPay() {
        return realPay;
    }

    public void setRealPay(double realPay) {
        this.realPay = realPay;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public void setMaling(double maling) {
        this.maling = maling;
    }

    public String getSettingTime() {
        return settingTime;
    }

    public void setSettingTime(String settingTime) {
        this.settingTime = settingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isDispatch() {
        return dispatch;
    }

    public void setDispatch(boolean dispatch) {
        this.dispatch = dispatch;
    }

    public boolean ispay() {
        return ispay;
    }

    public void setIspay(boolean ispay) {
        this.ispay = ispay;
    }


    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getAdvance() {
        return advance;
    }

    public void setAdvance(double advance) {
        this.advance = advance;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int state) {
        this.status = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public ArrayList<Goods> getGoodsInfoList() {
        return goodsInfoList;
    }

    public void setGoodsInfoList(ArrayList<Goods> goodsInfoList) {
        this.goodsInfoList = goodsInfoList;
    }

    public int getIsMaling() {
        return isMaling;
    }

    public void setIsMaling(int isMaling) {
        this.isMaling = isMaling;
    }

    @Override
    public String toString() {
        return "-----OrderInfo-----" + pName + "  id  ==" + id + "   isPay==" + ispay + "  mobile==" + mobile + "  address== " + address;
    }
}
