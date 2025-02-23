package com.inlym.lifehelper.weather.weatherdata;

import com.inlym.lifehelper.weather.weatherdata.pojo.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 混合天气数据服务
 *
 * @author inlym
 * @date 2022-02-19
 **/
@Service
public class WeatherMixedDataService {
    private final WeatherDataServiceAsync weatherDataServiceAsync;

    public WeatherMixedDataService(WeatherDataServiceAsync weatherDataServiceAsync) {
        this.weatherDataServiceAsync = weatherDataServiceAsync;
    }

    /**
     * 获取整合后的天气数据
     *
     * @param longitude 经度
     * @param latitude  纬度
     */
    @SneakyThrows
    public Map<String, Object> getMixedWeatherData(double longitude, double latitude) {
        CompletableFuture<WeatherNow> now = weatherDataServiceAsync.getWeatherNow(longitude, latitude);
        CompletableFuture<WeatherDaily[]> f15d = weatherDataServiceAsync.getWeather15D(longitude, latitude);
        CompletableFuture<WeatherHourly[]> f24h = weatherDataServiceAsync.getWeather24H(longitude, latitude);
        CompletableFuture<MinutelyRain> rain = weatherDataServiceAsync.getMinutely(longitude, latitude);
        CompletableFuture<IndicesItem[]> indices3d = weatherDataServiceAsync.getIndices3D(longitude, latitude);
        CompletableFuture<AirNow> airNow = weatherDataServiceAsync.getAirNow(longitude, latitude);
        CompletableFuture<AirDaily[]> air5d = weatherDataServiceAsync.getAir5D(longitude, latitude);

        CompletableFuture
            .allOf(now, f15d, f24h, rain, indices3d, airNow, air5d)
            .join();

        Map<String, Object> map = new HashMap<>(16);
        map.put("now", now.get());
        map.put("f15d", f15d.get());
        map.put("f24h", f24h.get());
        map.put("rain", rain.get());
        map.put("indices3d", indices3d.get());
        map.put("airNow", airNow.get());
        map.put("air5d", air5d.get());

        return map;
    }
}
