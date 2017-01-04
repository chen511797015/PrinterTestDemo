package cn.pax.db;

/**
 * Created by hr on 2015/9/19.
 * 打印设置
 */
public class PrintSetting {
    private Long id;
    private String goodsName;  // 商品名称：  1-使用 0-未使用
    private String goodsCode;  // 商品编码： 1-使用 0-未使用
    private String standard;   // 规格： 1-使用 0-未使用
    private String integral;  // 积分： 1-使用 0-未使用
    private String sericeStaff; // 店铺服务人员
    private String vipCode; //会员号
    private Integer printNum; // 打印次数



    public PrintSetting(){}

    public PrintSetting(Long id){
        this.id = id;
    }

    public PrintSetting(Long id, String goodsName, String goodsCode, String standard, String integral, String serviceStaff, String vipCode, Integer printNum){
        this.id = id;
        this.goodsName = goodsName;
        this.goodsCode = goodsCode;
        this.standard = standard;
        this.integral = integral;
        this.sericeStaff = serviceStaff;
        this.printNum = printNum;
        this.vipCode = vipCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getPrintNum() {
        return printNum;
    }

    public void setPrintNum(Integer printNum) {
        this.printNum = printNum;
    }

    public String getSericeStaff() {
        return sericeStaff;
    }

    public void setSericeStaff(String sericeStaff) {
        this.sericeStaff = sericeStaff;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }
}
