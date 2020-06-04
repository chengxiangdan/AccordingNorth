package com.website.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties properties;
    //获取配置文件流
    private static Properties initProperties() throws IOException {
        if (properties==null){
            properties=new Properties();
        }
        //返回一个读取指定资源的输入流
        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties");
        //读取流
        InputStreamReader reader=null;
        try {
            reader=new InputStreamReader(inputStream);
            properties.load(reader);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
            reader.close();
        }
        return properties;
    }

    //读取配置文件信息
    public static String readByKey(String key) throws IOException {
        if(properties == null) {
            initProperties();
        }
        return properties.getProperty(key);
    }

}
