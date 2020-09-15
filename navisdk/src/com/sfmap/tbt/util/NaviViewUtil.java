package com.sfmap.tbt.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 对导航视图进行自定义修改类
 */

public class NaviViewUtil {

public static Map<String,String> config_style;

    private static void readConfigFile(String filePath) {
        if(filePath==null||"".equals(filePath))return ;
        File file = new File(filePath);
        BufferedReader reader = null;
        try {

            if(config_style==null)
            config_style=new HashMap();
            config_style.clear();
            if(!file.exists())return;
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if(tempString.startsWith("#"))continue;
                if(tempString.contains("//"))tempString=tempString.substring(0,tempString.indexOf("//"));
                if(tempString.contains("=")){
                String[] keyAndValue=tempString.split("=");
                    config_style.put(keyAndValue[0].trim(),keyAndValue[1].trim());
            }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

public static void loadStyleConfigByAsync(final String filePath){
    new Thread(new Runnable() {
        @Override
        public void run() {
            readConfigFile(filePath);
        }
    }){}.start();
}
}
