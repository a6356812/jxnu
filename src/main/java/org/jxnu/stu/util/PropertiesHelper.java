package org.jxnu.stu.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class PropertiesHelper {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("application-dev");

    public static String getProperties(String key){
        return resourceBundle.getString(key);
    }
}
