package cn.univyz.plugin.soap;

import cn.univyz.framework.helper.ConfigHelper;

/**
 * 从配置文件中获取相关属性
 *
 * @author bfy
 * @version 1.0.0
 */
public class SoapConfig {

    public static boolean isLog() {
        return ConfigHelper.getBoolean(SoapConstant.LOG);
    }
}
