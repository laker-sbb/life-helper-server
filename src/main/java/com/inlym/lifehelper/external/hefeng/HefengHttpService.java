package com.inlym.lifehelper.external.hefeng;

import com.inlym.lifehelper.external.hefeng.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 和风天气 HTTP 请求服务
 * <p>
 * [主要用途]
 * 将对和风天气 API 的 HTTP 请求封装为内部可直接调用的方法。
 * <p>
 * [注意事项]
 * 1. 仅封装 HTTP 请求，不对响应数据做任何数据处理。
 * 2. 方法入参一般即为发起 HTTP 请求所需的参数，对部分未用到的参数不做封装。
 * 3. 如实地反映请求和响应本身，这一层中不要管最终业务层是如何使用该数据的。
 *
 * @author inlym
 * @since 2022-01-15 18:54
 */
@Service
public class HefengHttpService {
    private final Log logger = LogFactory.getLog(getClass());

    private final RestTemplate restTemplate;

    private final HefengProperties hefengProperties;

    public HefengHttpService(HefengProperties hefengProperties) {
        this.hefengProperties = hefengProperties;

        // 和风天气响应会使用 gzip 压缩，使用这个 HTTP 客户端自动处理 gzip 解压缩
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder
            .create()
            .build());
        this.restTemplate = new RestTemplate(clientHttpRequestFactory);
    }

    /**
     * 城市信息查询
     *
     * @param location 需要查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）
     *
     * @see <a href="https://dev.qweather.com/docs/api/geo/city-lookup/">城市信息查询</a>
     */
    @Cacheable("hefeng:city")
    public HefengLookUpCityResponse lockUpCity(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://geoapi.qweather.com/v2/city/lookup";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getDevKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengLookUpCityResponse data = restTemplate.getForObject(url, HefengLookUpCityResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[城市信息查询] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[城市信息查询] 请求错误, url=" + url);
    }

    /**
     * 获取实时天气（开发版）
     *
     * @param location 需要查询地区的 LocationID 或以英文逗号分隔的经度,纬度坐标
     *
     * @see <a href="https://dev.qweather.com/docs/api/weather/weather-now/">实时天气</a>
     */
    @Cacheable("hefeng:now")
    public HefengWeatherNowResponse getWeatherNow(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://devapi.qweather.com/v7/weather/now";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getDevKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengWeatherNowResponse data = restTemplate.getForObject(url, HefengWeatherNowResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[实时天气] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[实时天气] 请求错误, url=" + url);
    }

    /**
     * 获取逐天天气预报（商业版）
     *
     * @param location 需要查询地区的 LocationID 或以英文逗号分隔的经度,纬度坐标
     * @param days     天数，支持 `3d`,`7d`,`10d`,`15d`,`30d`
     *
     * @see <a href="https://dev.qweather.com/docs/api/weather/weather-daily-forecast/">逐天天气预报</a>
     */
    @Cacheable("hefeng:daily")
    public HefengWeatherDailyForecastResponse getWeatherDailyForecast(String location, String days) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/weather/" + days;

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengWeatherDailyForecastResponse data = restTemplate.getForObject(url, HefengWeatherDailyForecastResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[逐天天气预报] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[逐天天气预报] 请求错误, url=" + url);
    }

    /**
     * 获取逐小时天气预报（商业版）
     *
     * @param location 需要查询地区的 LocationID 或以英文逗号分隔的经度,纬度坐标
     * @param hours    小时数，支持 `24h`,`72h`,`168h`
     *
     * @see <a href="https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/">逐小时天气预报</a>
     */
    @Cacheable("hefeng:hourly")
    public HefengWeatherHourlyForecastResponse getWeatherHourlyForecast(String location, String hours) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/weather/" + hours;

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengWeatherHourlyForecastResponse data = restTemplate.getForObject(url, HefengWeatherHourlyForecastResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[逐小时天气预报] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[逐小时天气预报] 请求错误, url=" + url);
    }

    /**
     * 获取格点实时天气（商业版）
     *
     * @param location 需要查询地区的以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）
     *
     * @see <a href="https://dev.qweather.com/docs/api/grid-weather/grid-weather-now/">格点实时天气</a>
     */
    @Cacheable("hefeng:grid-now")
    public HefengGridWeatherNowResponse getGridWeatherNow(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/grid-weather/now";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengGridWeatherNowResponse data = restTemplate.getForObject(url, HefengGridWeatherNowResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[格点实时天气] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[格点实时天气] 请求错误, url=" + url);
    }

    /**
     * 获取格点逐天天气预报（商业版）
     *
     * @param location 需要查询地区的以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）
     * @param days     天数，支持 `3d`,`7d`
     *
     * @see <a href="https://dev.qweather.com/docs/api/grid-weather/grid-weather-daily-forecast/">格点逐天天气预报</a>
     */
    @Cacheable("hefeng:grid-daily")
    public HefengGridWeatherDailyForecastResponse getGridWeatherDailyForecast(String location, String days) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/weather/" + days;

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengGridWeatherDailyForecastResponse data = restTemplate.getForObject(url, HefengGridWeatherDailyForecastResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[格点逐天天气预报] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[格点逐天天气预报] 请求错误, url=" + url);
    }

    /**
     * 获取格点逐小时天气预报（商业版）
     *
     * @param location 需要查询地区的以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）
     * @param hours    小时数，支持 `24h`,`72h`
     *
     * @see <a href="https://dev.qweather.com/docs/api/grid-weather/grid-weather-hourly-forecast/">格点逐小时天气预报</a>
     */
    @Cacheable("hefeng:grid-hourly")
    public HefengGridWeatherHourlyForecastResponse getGridWeatherHourlyForecast(String location, String hours) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/weather/" + hours;

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengGridWeatherHourlyForecastResponse data = restTemplate.getForObject(url, HefengGridWeatherHourlyForecastResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[格点逐小时天气预报] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[格点逐小时天气预报] 请求错误, url=" + url);
    }

    /**
     * 获取分钟级降水（商业版）
     *
     * @param location 需要查询地区的以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）
     *
     * @see <a href="https://dev.qweather.com/docs/api/grid-weather/minutely/">分钟级降水</a>
     */
    @Cacheable("hefeng:grid-minutely-rain")
    public HefengGridWeatherMinutelyRainResponse getGridWeatherMinutelyRain(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/minutely/5m";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengGridWeatherMinutelyRainResponse data = restTemplate.getForObject(url, HefengGridWeatherMinutelyRainResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[分钟级降水] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[分钟级降水] 请求错误, url=" + url);
    }

    /**
     * 获取天气生活指数（开发版）
     *
     * @param location 需要查询地区的 LocationID 或以英文逗号分隔的经度,纬度坐标
     *
     * @see <a href="https://dev.qweather.com/docs/api/indices/">天气生活指数</a>
     */
    @Cacheable("hefeng:indices")
    public HefengIndicesResponse getIndices(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://devapi.qweather.com/v7/indices/1d";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getDevKey())
            .queryParam("type", "0")
            .build()
            .toUriString();
        logger.debug(url);

        HefengIndicesResponse data = restTemplate.getForObject(url, HefengIndicesResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[天气生活指数] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[天气生活指数] 请求错误, url=" + url);
    }

    /**
     * 获取实时空气质量（开发版）
     *
     * @param location 需要查询地区的LocationID或以英文逗号分隔的经度,纬度坐标
     *
     * @see <a href="https://dev.qweather.com/docs/api/air/air-now/">实时空气质量</a>
     */
    @Cacheable("hefeng:air-now")
    public HefengAirNowResponse getAirNow(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://devapi.qweather.com/v7/air/now";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getDevKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengAirNowResponse data = restTemplate.getForObject(url, HefengAirNowResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[实时空气质量] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[实时空气质量] 请求错误, url=" + url);
    }

    /**
     * 获取未来5天空气质量预报（商业版）
     *
     * @param location 需要查询地区的LocationID或以英文逗号分隔的经度,纬度坐标
     *
     * @see <a href="https://dev.qweather.com/docs/api/air/air-daily-forecast/">空气质量预报</a>
     */
    @Cacheable("hefeng:air-daily")
    public HefengAirDailyForecastResponse getAirDailyForecast(String location) throws Exception {
        // 不含参数的请求地址前缀
        String baseURL = "https://api.qweather.com/v7/air/5d";

        // 包含请求参数的完整请求地址
        String url = UriComponentsBuilder
            .fromHttpUrl(baseURL)
            .queryParam("location", location)
            .queryParam("key", hefengProperties.getProKey())
            .build()
            .toUriString();
        logger.debug(url);

        HefengAirDailyForecastResponse data = restTemplate.getForObject(url, HefengAirDailyForecastResponse.class);

        if (data != null && data
            .getCode()
            .equals("200")) {
            logger.info("[空气质量预报] location=" + location + ", data=" + data);

            return data;
        }

        throw new Exception("[空气质量预报] 请求错误, url=" + url);
    }
}
