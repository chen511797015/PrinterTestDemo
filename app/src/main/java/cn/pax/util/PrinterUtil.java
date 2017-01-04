package cn.pax.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pax.tool.Mustache;
import cn.pax.tool.Template;

/**
 * Created by chendd on 2016/10/28.
 */

public class PrinterUtil {

    static Template mTemplate;

    public static String getTempleStr(HashMap<String, Object> hashMap, Context context) {

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = null;
            inputStream = assetManager.open("ticket.ini");
            Mustache.Compiler compiler = Mustache.compiler();
            mTemplate = compiler.compile(new InputStreamReader(inputStream, "GBK"));
            return mTemplate.execute(hashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "初始化失败";

    }


    /**
     * decode bitmap to bytes 解码Bitmap为位图字节流
     */
    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;

        // 每行字节数(除以8，不足补0)
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        // 每行需要补充的0
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        // 逐个读取像素颜色，将非白色改为黑色
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i); // 获得Bitmap 图片中每一个点的color颜色值
                //颜色值的R G B
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            // 每一行结束时，补充剩余的0
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        // binaryStr每8位调用一次转换方法，再拼合
        List<String> bmpHexList = ConvertUtil.binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        // 宽度指令
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", "宽度超出 width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        // 高度指令
        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", "高度超出 height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return ConvertUtil.hexList2Byte(commandList);
    }


}
