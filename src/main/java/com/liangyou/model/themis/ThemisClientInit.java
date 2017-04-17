package com.liangyou.model.themis;

import com.liangyou.context.Global;
import org.mapu.themis.ThemisClient;

/**
 * 保全客户端初始化，这里只作演示，实际项目中可将ThemisClient类配置成spring bean方式
 *
 * @author luopeng Created on 2014/5/5.
 */
public class ThemisClientInit {

    private static ThemisClient themisClient;

    static {
        if ("1".equals(Global.getValue("config_online"))) {
            themisClient = new ThemisClient(Global.getValue("themis_services_url"), Global.getValue("themis_app_key"), Global.getValue("themis_app_secret"));
        } else {
            themisClient = new ThemisClient(Global.getValue("themis_test_services_url"), Global.getValue("themis_test_app_key"), Global.getValue("themis_test_app_secret"));
        }
    }

    public static ThemisClient getClient() {
        return themisClient;
    }

}
