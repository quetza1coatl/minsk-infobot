package com.quetzalcoatl.telebot.util;

import com.quetzalcoatl.telebot.response.Response;
import com.quetzalcoatl.telebot.response.StartResponseImpl;
import com.quetzalcoatl.telebot.response.mock.MockResponseImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MainUtil {

    //TODO: public or package?
    public static Properties getProperties() {
        Properties properties = new Properties();
        InputStream is = MainUtil.class.getClassLoader().getResourceAsStream("main.properties");
        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return properties;

    }

    public static List<Response> getResponseList(){
        return Arrays.asList(new StartResponseImpl(), new MockResponseImpl());
    }

}
