-- 天气历史表
CREATE TABLE IF NOT EXISTS weather_history (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    city         VARCHAR(50) NOT NULL,
    temperature  DECIMAL(5,2),
    humidity     DECIMAL(5,2),
    wind_speed   DECIMAL(5,2),
    weather      VARCHAR(50),
    aqi          INT,
    aqi_level    VARCHAR(20),
    update_time  VARCHAR(50),
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 城市信息表
CREATE TABLE IF NOT EXISTS city_info (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    city_name    VARCHAR(50) NOT NULL UNIQUE,
    province     VARCHAR(50),
    region       VARCHAR(50),
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 告警记录表
CREATE TABLE IF NOT EXISTS alert_records (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    city         VARCHAR(50) NOT NULL,
    alert_type   VARCHAR(50),
    alert_level  VARCHAR(20),
    alert_value  DECIMAL(10,2),
    threshold    DECIMAL(10,2),
    status       VARCHAR(20),
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolve_time TIMESTAMP NULL
);

-- 插入测试城市数据
INSERT INTO city_info (city_name, province, region) VALUES
    ('Beijing', '北京', '华北'),
    ('Shanghai', '上海', '华东'),
    ('Guangzhou', '广东', '华南'),
    ('Shenzhen', '广东', '华南'),
    ('Chengdu', '四川', '西南'),
    ('Wuhan', '湖北', '华中'),
    ('Hangzhou', '浙江', '华东'),
    ('Chongqing', '重庆', '西南'),
    ('Nanjing', '江苏', '华东'),
    ('Xi\'an', '陕西', '西北')
ON DUPLICATE KEY UPDATE city_name=city_name;
