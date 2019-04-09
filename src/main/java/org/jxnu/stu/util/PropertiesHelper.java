package org.jxnu.stu.util;

import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class PropertiesHelper {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

    public static String getProperties(String key){
        return resourceBundle.getString(key);
    }
}
