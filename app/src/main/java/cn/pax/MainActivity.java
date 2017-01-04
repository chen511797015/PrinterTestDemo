package cn.pax;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cn.pax.tool.Mustache;
import cn.pax.tool.PublicMethod;
import cn.pax.tool.Template;
import cn.pax.util.UsbUtil;

import static android.R.attr.x;
import static cn.pax.R.id.btn_show_printer_power;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPrint, btnOpen;//打印+打开钱箱
    Button btn_show_printer_info;//打印机信息按钮
    Button btn_show_printer_cut_all;//全切

    private UsbUtil mUsbUtil;

    private String msg = "打印机（Printer) 是计算机的输出设备之一，用于将计算机处理结果打印在相关介质上。衡量打印机好坏的指标有三项：打印分辨率，打印速度和噪声。 打印机的种类很多，按打印元件对纸是否有击打动作，分击打式打印机与非击打式打印机。按打印字符结构，分全形字打印机和点阵字符打印机。按一行字在纸上形成的方式，分串式打印机与行式打印机。按所采用的技术，分柱形、球形、喷墨式、热敏式、激光式、静电式、磁式、发光二极管式等打印机。!";

//    private byte SendCut[] = {0x0a, 0x0a, 0x1d, 0x56, 0x01};//切纸距离设置

    private byte SendCut[] = {0x0a, 0x0a, 0x1d, 0x56, 0x01};//切纸距离设置


    private byte SendCash[] = {0x1b, 0x70, 0x00, 0x1e, (byte) 0xff, 0x00};//打开钱箱

    private StringBuffer headDetail;
    private HashMap<String, Object> hashMap;

    private Template mTemplate;
    private String execute;
    private StringBuilder sb;

    Button btn_show_printer_power;//打印电量信息
    Button btn_show_printer_init;//初始化打印机


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        initData();

        mUsbUtil = new UsbUtil(this);

        initEvent();

        String toHexString = Integer.toHexString(38);
        Log.e(TAG, "onCreate: " + toHexString);
    }

    /**
     * 加载打印数据
     */
    private void initData() {
        hashMap = new HashMap<>();
        headDetail = new StringBuffer();
        headDetail.append("订单号:123456789");
        headDetail.append(PublicMethod.getSpaceByNum(12));
        headDetail.append("导购员:张三");
        headDetail.append("\r\n").append("收银员:李四");
        headDetail.append(PublicMethod.getSpaceByNum(6 - ("").length())).append("\r\n");
        headDetail.append("结账时间:2016-10-28-14:31");

        hashMap.put("headDetail", headDetail.toString());
        hashMap.put("items", "商品列表");

        try {
            AssetManager assetManager = this.getAssets();
            InputStream inputStream = null;
            inputStream = assetManager.open("ticket.ini");
            Mustache.Compiler compiler = Mustache.compiler();
            mTemplate = compiler.compile(new InputStreamReader(inputStream, "GBK"));
            execute = mTemplate.execute(hashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }


        sb = new StringBuilder();
        sb.append(msg);
//        sb.append(msg);
//        sb.append(msg);
//        sb.append(msg);
//        sb.append(msg);
//        sb.append(msg);
//        sb.append(msg);


//        sb.append(msg + "\r\n");
//        sb.append(msg + "\r\n");
//        sb.append(msg + "\r\n");
//        sb.append(msg + "\r\n");

    }

    private void findView() {
        btnPrint = (Button) findViewById(R.id.btn_print);
        btnOpen = (Button) findViewById(R.id.btn_open_money);
        btn_show_printer_info = (Button) findViewById(R.id.btn_show_printer_info);
        btn_show_printer_cut_all = (Button) findViewById(R.id.btn_show_printer_cut_all);
        btn_show_printer_power = (Button) findViewById(R.id.btn_show_printer_power);
        btn_show_printer_init = (Button) findViewById(R.id.btn_show_printer_init);

    }

    private void initEvent() {
        btnPrint.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btn_show_printer_info.setOnClickListener(this);
        btn_show_printer_cut_all.setOnClickListener(this);
        btn_show_printer_power.setOnClickListener(this);
        btn_show_printer_init.setOnClickListener(this);
    }


    /**
     * Button点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_show_printer_init:
                mUsbUtil.sendCommand(new byte[]{0x1b, 0x40});//初始化打印机
                mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x45, 0x4c, 0x46});//打印机信息
                mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x43, 0x54, 0x47, 0x48, 0x30});//全切
                break;
            case R.id.btn_show_printer_power:
                mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x4e});//打印电量信息
                mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x43, 0x54, 0x47, 0x48, 0x30});//全切
                break;
            case R.id.btn_show_printer_cut_all:
                try {
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x20});//打印机浓度  0-39
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x45, 0x46, 0x43, 0x54, 0x31});
                    mUsbUtil.sendCommand(sb.toString().getBytes("GBK"));
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x43, 0x54, 0x47, 0x48, 0x30});//全切
                    mUsbUtil.sendCommand(sb.toString().getBytes("GBK"));
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x43, 0x54, 0x47, 0x48, 0x31});//半切
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_print:

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                String toString = sb.toString();

                try {
                    String templeStr = "--------";
                    if (execute != null) {
                        templeStr = execute;
                    }
                    byte[] bytes = {29, 86, 0};//全切

                    byte[] b = {0x1D, 0X42, 0X21};
                    StringBuilder stringBuilder = new StringBuilder();
                    byteArrayOutputStream.write(b);
                    byteArrayOutputStream.write(toString.getBytes("GBK"));
                    byteArrayOutputStream.write(b);
                    byteArrayOutputStream.flush();
                    // 切纸
                    byte[] POS_CUT_MODE_FULL = new byte[]{0x1d, 'V', 0x00};

                    mUsbUtil.sendCommand(toString.getBytes("GBK"));
                    mUsbUtil.sendCommand(POS_CUT_MODE_FULL);
                    mUsbUtil.sendCommand(new byte[]{});

                } catch (IOException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.btn_open_money:
                //打开钱箱
                try {
                    mUsbUtil.sendCommand(SendCash);
                    mUsbUtil.sendCommand(new byte[]{});

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.btn_show_printer_info:
                try {

                    //mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x45, 0x4c, 0x46});//打印机信息

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x27});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========39\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x26});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========38".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x25});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========37\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x24});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========36".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x23});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========35\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x22});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========34".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x21});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========33\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x20});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========32".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1f});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========31\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1e});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========30".getBytes("GBK"));

                    //打印机浓度20-29
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1d});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========29\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1c});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========28".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1b});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========27\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1a});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========26".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x19});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========25\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x18});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========24".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x17});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========23\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x16});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========22".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x15});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========21\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x14});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========20".getBytes("GBK"));

                    //打印机浓度10-19
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x13});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========19\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x12});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========18".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x11});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========17\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x10});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========16".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xf});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========15\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xe});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========14".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xd});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========13\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xc});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========12".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xb});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========11\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0xa});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========10".getBytes("GBK"));

                    //打印机浓度0-9
                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x9});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========0\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x8});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========8".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x7});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========7\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x6});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========6".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x5});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========5\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x4});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========4".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x3});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========3\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x2});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========2".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x1});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========1\r\n".getBytes("GBK"));

                    mUsbUtil.sendCommand(new byte[]{0x1b, 0x23, 0x23, 0x53, 0x54, 0x44, 0x50, 0x0});//打印机浓度  0-39
                    mUsbUtil.sendCommand(mUsbUtil.getSb().toString().getBytes("GBK"));
                    mUsbUtil.sendCommand("浓度:========0".getBytes("GBK"));

                    mUsbUtil.sendCommand(SendCut);
                    mUsbUtil.sendCommand(new byte[]{});

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
