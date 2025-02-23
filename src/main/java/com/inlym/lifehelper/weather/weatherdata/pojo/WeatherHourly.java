package com.inlym.lifehelper.weather.weatherdata.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 逐小时天气预报中的单小时数据详情
 *
 * @author inlym
 * @date 2022-02-19
 **/
@Data
public class WeatherHourly {
    // ====================     新增的字段     ====================

    /** 预报时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    /** 天气状况和图标 URL 地址 */
    private String iconUrl;

    // ====================  和风天气原有的字段  ====================

    /** 温度，默认单位：摄氏度 */
    private String temp;

    /** 天气状况的文字描述 */
    private String text;

    /** 风向360角度 */
    private String wind360;

    /** 风向 */
    private String windDir;

    /** 风力等级 */
    private String windScale;

    /** 风速，公里/小时 */
    private String windSpeed;

    /** 相对湿度，百分比数值 */
    private String humidity;

    /** 当前小时累计降水量，默认单位：毫米 */
    private String precip;

    /** 逐小时预报降水概率，百分比数值，可能为空 */
    private String pop;

    /** 大气压强，默认单位：百帕 */
    private String pressure;
}
