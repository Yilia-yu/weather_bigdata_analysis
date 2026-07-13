<template>
  <div class="weather-container">

    <!-- ===== 顶部标题 ===== -->
    <div class="header">
      <h2>🌤️ 实时天气数据监测大屏</h2>
      <div class="header-right">
        <span class="countdown-text">
          ⏱ 下次刷新：<strong>{{ countdownText }}</strong>
        </span>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-refresh"
          :loading="isRefreshing"
          @click="manualRefresh"
        >立即刷新</el-button>
      </div>
    </div>

    <!-- ===== 统计卡片 ===== -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">🏙️ 监控城市</div>
            <div class="stat-value">{{ stats.cityCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">🌡️ 平均温度</div>
            <div class="stat-value">{{ stats.avgTemp || 0 }}°C</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">🔥 最热城市</div>
            <div class="stat-value">{{ stats.hottestCity || '--' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">📊 最高温度</div>
            <div class="stat-value">{{ stats.hottestTemp || 0 }}°C</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 图表区域 ===== -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>🌡️ 天气分布</span>
          </div>
          <div ref="pieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span>📊 温度区间城市分布</span>
          </div>
          <div ref="barChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ===== 底部导航按钮 ===== -->
    <div class="nav-buttons">
      <div class="nav-btn nav-btn-weather" @click="goWeather">
        <div class="nav-btn-icon">
          <svg viewBox="0 0 48 48" width="44" height="44">
            <defs>
              <linearGradient id="weatherGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#4facfe"/>
                <stop offset="100%" style="stop-color:#00f2fe"/>
              </linearGradient>
            </defs>
            <circle cx="17" cy="28" r="5" fill="url(#weatherGrad)" opacity="0.9"/>
            <circle cx="28" cy="24" r="5" fill="url(#weatherGrad)" opacity="0.9"/>
            <circle cx="22" cy="19" r="6" fill="url(#weatherGrad)" opacity="0.9"/>
            <circle cx="22" cy="19" r="4" fill="white" opacity="0.3"/>
            <path d="M8 32 Q14 20 20 32 Q22 28 28 28 Q32 28 32 32 Q34 28 42 26" stroke="url(#weatherGrad)" stroke-width="2" fill="none" stroke-linecap="round"/>
            <line x1="14" y1="36" x2="30" y2="36" stroke="url(#weatherGrad)" stroke-width="1.5" opacity="0.5"/>
          </svg>
        </div>
        <div class="nav-btn-content">
          <span class="nav-btn-title">实时天气</span>
          <span class="nav-btn-desc">查看75城实时天气详情</span>
        </div>
        <div class="nav-btn-arrow">→</div>
      </div>

      <div class="nav-btn nav-btn-screen" @click="goScreen">
        <div class="nav-btn-icon">
          <svg viewBox="0 0 48 48" width="44" height="44">
            <defs>
              <linearGradient id="screenGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#fa709a"/>
                <stop offset="100%" style="stop-color:#fee140"/>
              </linearGradient>
            </defs>
            <rect x="8" y="10" width="30" height="24" rx="3" fill="none" stroke="url(#screenGrad)" stroke-width="2.5"/>
            <rect x="12" y="14" width="22" height="15" rx="1" fill="url(#screenGrad)" opacity="0.15"/>
            <line x1="20" y1="38" x2="26" y2="38" stroke="url(#screenGrad)" stroke-width="2" stroke-linecap="round"/>
            <line x1="23" y1="38" x2="23" y2="42" stroke="url(#screenGrad)" stroke-width="2" stroke-linecap="round"/>
            <rect x="14" y="16" width="18" height="3" rx="1.5" fill="url(#screenGrad)" opacity="0.5"/>
            <circle cx="23" cy="24" r="4" fill="none" stroke="url(#screenGrad)" stroke-width="1.5" opacity="0.6"/>
            <polygon points="23,22 25,24 23,26 21,24" fill="url(#screenGrad)" opacity="0.6"/>
          </svg>
        </div>
        <div class="nav-btn-content">
          <span class="nav-btn-title">可视化大屏</span>
          <span class="nav-btn-desc">跳转独立数据大屏展示</span>
        </div>
        <div class="nav-btn-arrow nav-btn-arrow-external">↗</div>
      </div>

      <div class="nav-btn nav-btn-aviation" @click="goAviation">
        <div class="nav-btn-icon">
          <svg viewBox="0 0 48 48" width="44" height="44">
            <defs>
              <linearGradient id="aviaGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#a18cd1"/>
                <stop offset="100%" style="stop-color:#fbc2eb"/>
              </linearGradient>
            </defs>
            <path d="M8 36 L24 12 L40 36" stroke="url(#aviaGrad)" stroke-width="2.5" fill="none" stroke-linejoin="round"/>
            <circle cx="18" cy="28" r="2" fill="url(#aviaGrad)" opacity="0.4"/>
            <circle cx="30" cy="28" r="2" fill="url(#aviaGrad)" opacity="0.4"/>
            <path d="M22 28 Q24 24 26 28" stroke="url(#aviaGrad)" stroke-width="1.5" fill="none" opacity="0.6"/>
            <line x1="24" y1="12" x2="24" y2="8" stroke="url(#aviaGrad)" stroke-width="2" stroke-linecap="round"/>
            <circle cx="24" cy="7" r="2" fill="url(#aviaGrad)" opacity="0.7"/>
          </svg>
        </div>
        <div class="nav-btn-content">
          <span class="nav-btn-title">航空总览</span>
          <span class="nav-btn-desc">航空天气风险分析平台</span>
        </div>
        <div class="nav-btn-arrow">→</div>
      </div>
    </div>

  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'

const REFRESH_INTERVAL = 5 * 60 * 1000 // 5分钟

export default {
  name: 'WeatherIndex',
  data() {
    return {
      weatherData: [],
      stats: {
        cityCount: 0,
        avgTemp: 0,
        hottestCity: '--',
        hottestTemp: 0
      },
      pieChart: null,
      barChart: null,
      isRefreshing: false,
      lastUpdateTime: '--:--:--',
      // 定时器
      fetchTimer: null,
      countdownTimer: null,
      remainingSeconds: REFRESH_INTERVAL / 1000
    }
  },
  computed: {
    countdownText() {
      const m = Math.floor(this.remainingSeconds / 60)
      const s = this.remainingSeconds % 60
      return m + '分' + (s < 10 ? '0' : '') + s + '秒'
    }
  },
  mounted() {
    this.startAll()
  },
  beforeDestroy() {
    this.stopAll()
  },
  methods: {
    // ========== 核心：启动所有定时任务 ==========
    startAll() {
      this.stopAll()
      this.fetchData()
      this.initCharts()
      this.remainingSeconds = REFRESH_INTERVAL / 1000

      // 每 5 分钟拉取一次数据
      this.fetchTimer = setInterval(() => {
        this.fetchData()
        this.remainingSeconds = REFRESH_INTERVAL / 1000
      }, REFRESH_INTERVAL)

      // 每秒更新倒计时
      this.countdownTimer = setInterval(() => {
        if (this.remainingSeconds > 0) {
          this.remainingSeconds--
        } else {
          this.remainingSeconds = REFRESH_INTERVAL / 1000
        }
      }, 1000)
    },

    stopAll() {
      if (this.fetchTimer) {
        clearInterval(this.fetchTimer)
        this.fetchTimer = null
      }
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    // ========== 手动刷新 ==========
    manualRefresh() {
      this.fetchData()
      this.remainingSeconds = REFRESH_INTERVAL / 1000
    },

    // ========== 底部导航按钮 ==========
    goWeather() {
      this.$router.push('/basee/weather')
    },
    goScreen() {
      window.open('http://localhost:8888', '_blank')
    },
    goAviation() {
      this.$router.push('/basee/aviation')
    },

    // ========== 初始化图表 ==========
    initCharts() {
      this.$nextTick(() => {
        if (this.$refs.pieChart && !this.pieChart) {
          this.pieChart = echarts.init(this.$refs.pieChart)
        }
        if (this.$refs.barChart && !this.barChart) {
          this.barChart = echarts.init(this.$refs.barChart)
        }
      })
      window.addEventListener('resize', this.handleResize)
    },

    handleResize() {
      if (this.pieChart) this.pieChart.resize()
      if (this.barChart) this.barChart.resize()
    },

    // ========== 拉取数据 ==========
    fetchData() {
      this.isRefreshing = true

      Promise.all([
        request({ url: '/weather/current', method: 'get' }),
        request({ url: '/weather/stats', method: 'get' })
      ]).then(([currentRes, statsRes]) => {
        this.weatherData = currentRes.data || []
        this.stats = statsRes.data || {}
        this.lastUpdateTime = new Date().toLocaleString()
        this.$nextTick(() => {
          this.updatePieChart()
          this.updateBarChart()
        })
      }).catch(err => {
        console.error('获取数据失败:', err)
      }).finally(() => {
        this.isRefreshing = false
      })
    },

    // ========== 饼图 ==========
    updatePieChart() {
      if (!this.pieChart) return
      const weatherMap = {}
      this.weatherData.forEach(d => {
        const w = d.weather || '未知'
        weatherMap[w] = (weatherMap[w] || 0) + 1
      })
      const data = Object.entries(weatherMap).map(([name, value]) => ({ name, value }))
      this.pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { color: '#555' } },
        series: [{
          type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'],
          data: data,
          label: { color: '#333', formatter: '{b}\n{d}%' },
          itemStyle: { borderColor: '#fff', borderWidth: 2, borderRadius: 4 },
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } }
        }],
        color: ['#00d4ff', '#0099ff', '#ffcc00', '#ff6b6b', '#6bcbff', '#ffd93d', '#4ecdc4', '#a29bfe', '#fd79a8']
      }, true)
    },

    // ========== 柱状图 ==========
    updateBarChart() {
      if (!this.barChart) return
      const ranges = [
        { label: '< 0°C', min: -100, max: 0 },
        { label: '0~10°C', min: 0, max: 10 },
        { label: '10~20°C', min: 10, max: 20 },
        { label: '20~30°C', min: 20, max: 30 },
        { label: '30~35°C', min: 30, max: 35 },
        { label: '> 35°C', min: 35, max: 100 }
      ]
      const counts = ranges.map(range => ({
        label: range.label,
        count: this.weatherData.filter(d => d.temperature >= range.min && d.temperature < range.max).length
      }))
      this.barChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: p => p[0].name + '<br/>城市数量: <strong>' + p[0].value + ' 个</strong>' },
        grid: { left: 60, right: 30, top: 30, bottom: 50 },
        xAxis: { type: 'category', data: counts.map(d => d.label), axisLabel: { color: '#555' } },
        yAxis: { type: 'value', name: '城市数', nameTextStyle: { color: '#555' }, axisLabel: { color: '#555' }, splitLine: { lineStyle: { color: 'rgba(0,0,0,0.06)' } } },
        series: [{
          type: 'bar',
          data: counts.map(d => ({ value: d.count, itemStyle: { color: d.count > 0 ? '#00d4ff' : '#ddd', borderRadius: [4, 4, 0, 0] } })),
          barWidth: 40,
          label: { show: true, position: 'top', color: '#333', formatter: p => p.value + ' 个' }
        }]
      }, true)
    },
  }
}
</script>

<style scoped>
.weather-container {
  padding: 20px;
  background: linear-gradient(135deg, #E8EEF4 0%, #F0EFF4 50%, #EDF2F8 100%);
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header h2 {
  margin: 0;
  color: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.countdown-text {
  font-size: 13px;
  color: #8899bb;
}
.countdown-text strong {
  color: #1a73e8;
  font-size: 14px;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
  padding: 10px 0;
}
.stat-label {
  font-size: 14px;
  color: #8899bb;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1a73e8;
  transition: all 0.3s;
}
.charts-row {
  margin-bottom: 20px;
}
.chart-container {
  width: 100%;
  height: 300px;
}
.el-card {
  background: #ffffff !important;
  border: 1px solid #e8ecf1 !important;
  border-radius: 12px !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04) !important;
}
.el-card >>> .el-card__header {
  border-bottom: 1px solid #e8ecf1;
  color: #333;
  font-weight: 600;
  padding: 16px 20px;
}
.el-card >>> .el-card__body {
  padding: 16px 20px;
}
.el-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  transform: translateY(-2px);
  transition: all 0.3s;
}

/* ===== 底部导航按钮 ===== */
.nav-buttons {
  display: flex;
  gap: 24px;
  margin-top: 24px;
  padding-bottom: 10px;
}
.nav-btn {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e8ecf1;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.nav-btn::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  opacity: 0;
  transition: opacity 0.35s;
  border-radius: 16px;
  z-index: 0;
}
.nav-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
}
.nav-btn:active {
  transform: translateY(-2px) scale(0.98);
}

/* 实时天气 — 蓝色系 */
.nav-btn-weather::before {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.08), rgba(0, 242, 254, 0.04));
}
.nav-btn-weather:hover {
  border-color: #4facfe;
  box-shadow: 0 12px 28px rgba(79, 172, 254, 0.18);
}
.nav-btn-weather:hover::before { opacity: 1; }
.nav-btn-weather .nav-btn-arrow {
  color: #4facfe;
}

/* 可视化大屏 — 珊瑚暖色系 */
.nav-btn-screen::before {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.08), rgba(254, 225, 64, 0.04));
}
.nav-btn-screen:hover {
  border-color: #fa709a;
  box-shadow: 0 12px 28px rgba(250, 112, 154, 0.18);
}
.nav-btn-screen:hover::before { opacity: 1; }
.nav-btn-screen .nav-btn-arrow {
  color: #fa709a;
}

/* 航空总览 — 紫色系 */
.nav-btn-aviation::before {
  background: linear-gradient(135deg, rgba(161, 140, 209, 0.08), rgba(251, 194, 235, 0.04));
}
.nav-btn-aviation:hover {
  border-color: #a18cd1;
  box-shadow: 0 12px 28px rgba(161, 140, 209, 0.18);
}
.nav-btn-aviation:hover::before { opacity: 1; }
.nav-btn-aviation .nav-btn-arrow {
  color: #a18cd1;
}

.nav-btn-icon {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: #f5f7fb;
  transition: all 0.35s;
  z-index: 1;
}
.nav-btn:hover .nav-btn-icon {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.nav-btn-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  z-index: 1;
}
.nav-btn-title {
  font-size: 17px;
  font-weight: 700;
  color: #2c3e50;
  letter-spacing: 0.5px;
  transition: color 0.3s;
}
.nav-btn-desc {
  font-size: 13px;
  color: #8899bb;
  transition: color 0.3s;
}
.nav-btn:hover .nav-btn-desc {
  color: #5a6d8a;
}
.nav-btn-arrow {
  flex-shrink: 0;
  font-size: 20px;
  font-weight: 300;
  transition: all 0.35s;
  z-index: 1;
  opacity: 0.5;
}
.nav-btn:hover .nav-btn-arrow {
  opacity: 1;
  transform: translateX(3px);
}
.nav-btn-arrow-external {
  font-size: 18px;
}
.nav-btn:hover .nav-btn-arrow-external {
  transform: translate(2px, -2px);
}
</style>
