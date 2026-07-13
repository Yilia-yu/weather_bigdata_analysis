<template>
  <div class="realtime-container">
    <div class="realtime-header">
      <span>📋 各城市实时天气详情</span>
      <span style="float: right; font-size: 12px; color: #999;">
        最近更新：{{ lastUpdateTime }}
      </span>
    </div>
    <div class="table-wrapper">
      <el-table :data="weatherData" v-loading="loading" stripe style="width: 100%" height="100%">
        <el-table-column label="城市" prop="cityName" align="center" width="120" />
        <el-table-column label="省份" prop="province" align="center" width="100" />
        <el-table-column label="区域" prop="region" align="center" width="100" />
        <el-table-column label="天气" prop="weather" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="weatherTagType(scope.row.weather)" size="small">{{ scope.row.weather }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="温度" prop="temperature" align="center" width="80">
          <template slot-scope="scope">
            <span :style="{ color: tempColor(scope.row.temperature) }">{{ scope.row.temperature }}°C</span>
          </template>
        </el-table-column>
        <el-table-column label="湿度" prop="humidity" align="center" width="80">
          <template slot-scope="scope">{{ scope.row.humidity }}%</template>
        </el-table-column>
        <el-table-column label="风速" prop="windSpeed" align="center" width="90">
          <template slot-scope="scope">{{ scope.row.windSpeed }} km/h</template>
        </el-table-column>
        <el-table-column label="记录时间" prop="recordTime" align="center" width="170" />
      </el-table>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'WeatherRealtime',
  data() {
    return {
      weatherData: [],
      loading: false,
      lastUpdateTime: '--'
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      request({ url: '/weather/current', method: 'get' }).then(res => {
        this.weatherData = res.data || []
        this.lastUpdateTime = new Date().toLocaleString()
      }).catch(err => {
        console.error('获取数据失败:', err)
      }).finally(() => {
        this.loading = false
      })
    },
    weatherTagType(weather) {
      if (!weather) return 'info'
      if (weather.includes('晴')) return 'warning'
      if (weather.includes('多云') || weather.includes('阴')) return ''
      if (weather.includes('雨')) return 'primary'
      if (weather.includes('雪')) return 'info'
      return ''
    },
    tempColor(temp) {
      if (temp >= 35) return '#ff4d4f'
      if (temp >= 30) return '#fa8c16'
      if (temp >= 20) return '#52c41a'
      if (temp >= 10) return '#1890ff'
      if (temp >= 0) return '#722ed1'
      return '#2f54eb'
    }
  }
}
</script>

<style scoped>
.realtime-container {
  height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
  padding: 15px 15px 0 15px;
}
.realtime-header {
  flex-shrink: 0;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #e8ecf1;
  border-bottom: none;
  border-radius: 12px 12px 0 0;
  font-weight: 600;
  color: #333;
  overflow: hidden;
}
.table-wrapper {
  flex: 1;
  min-height: 0;
  border: 1px solid #e8ecf1;
  border-top: none;
  border-radius: 0 0 12px 12px;
  background: #fff;
}
.el-table {
  border-radius: 0 0 12px 12px;
}
</style>
