<template>
  <div class="aviation-container">
    <div class="aviation-header">
      <h2>✈ 航空天气风险分析平台</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-click="onTabClick">
      <!-- ===== Tab1: 机场实时风险看板 ===== -->
      <el-tab-pane label="机场实时风险" name="dashboard">
        <el-row :gutter="16" class="kpi-row">
          <el-col :span="6">
            <div class="kpi-card safe"><div class="kpi-num">{{ dash.safeAirports }}</div><div class="kpi-label">🟢 适航机场</div></div>
          </el-col>
          <el-col :span="6">
            <div class="kpi-card delay"><div class="kpi-num">{{ dash.delayWarning }}</div><div class="kpi-label">🟡 延误预警</div></div>
          </el-col>
          <el-col :span="6">
            <div class="kpi-card risk"><div class="kpi-num">{{ dash.highRisk }}</div><div class="kpi-label">🔴 高风险</div></div>
          </el-col>
          <el-col :span="6">
            <div class="kpi-card route"><div class="kpi-num">{{ dash.affectedRoutes }}</div><div class="kpi-label">🛫 受影响航线</div></div>
          </el-col>
        </el-row>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="16">
            <el-card shadow="never" class="map-card">
              <div slot="header">🗺️ 机场风险分布地图</div>
              <div ref="mapChart" class="map-chart"></div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never" class="alert-card">
              <div slot="header">⚠ 实时预警事件流</div>
              <div class="alert-list" ref="alertList">
                <div v-for="(a, i) in dash.recentAlerts" :key="i"
                     class="alert-item" :class="getAlertClass(a)">
                  <span class="alert-time">{{ formatTime(a.alert_time) }}</span>
                  <span class="alert-city">{{ a.city_name }}</span>
                  <span class="alert-desc">{{ a.alert_type }}-{{ a.alert_level }} {{ a.alert_value }}</span>
                </div>
                <el-empty v-if="!dash.recentAlerts || dash.recentAlerts.length === 0" description="暂无预警事件" />
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" style="margin-top:16px">
          <div slot="header">📋 风险机场实时列表</div>
          <el-table :data="dash.riskAirports" stripe max-height="300">
            <el-table-column label="城市" prop="city" width="100" />
            <el-table-column label="风险类型" prop="type" width="100">
              <template slot-scope="s">
                <el-tag :type="s.row.type === '大风' ? 'warning' : 'primary'" size="small">{{ s.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="数值" width="120">
              <template slot-scope="s">{{ s.row.value }}{{ s.row.unit }}</template>
            </el-table-column>
            <el-table-column label="等级" prop="level" width="80">
              <template slot-scope="s">
                <el-tag :type="levelTag(s.row.level)" size="small">{{ s.row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="风速" width="80"><template slot-scope="s">{{ s.row.wind }}km/h</template></el-table-column>
            <el-table-column label="湿度" width="80"><template slot-scope="s">{{ s.row.humidity }}%</template></el-table-column>
            <el-table-column label="天气" prop="weather" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- ===== Tab2: 航线天气风险分析 ===== -->
      <el-tab-pane label="航线分析" name="routes">
        <el-row :gutter="16">
          <el-col :span="12" v-for="(r, i) in routeData.routes" :key="i">
            <el-card shadow="hover" class="route-card" :style="{ borderLeft: '4px solid ' + r.color }">
              <div class="rc-header">
                <span class="rc-name">{{ r.name }}</span>
                <el-tag :type="r.tagType" size="small">{{ r.status }}</el-tag>
              </div>
              <div class="rc-airports">{{ r.depCode }} ━━━ {{ r.arrCode }}</div>
              <el-row :gutter="8" class="rc-weather">
                <el-col :span="12">
                  <div class="rc-dep">
                    <span class="rc-label">出发</span>
                    <span class="rc-city">{{ r.depCity }}</span>
                    <span class="rc-temp">{{ r.depTemp }}°C</span>
                    <span>{{ r.depWeather }} {{ r.depWind }}km/h {{ r.depHumidity }}%</span>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="rc-arr">
                    <span class="rc-label">到达</span>
                    <span class="rc-city">{{ r.arrCity }}</span>
                    <span class="rc-temp">{{ r.arrTemp }}°C</span>
                    <span>{{ r.arrWeather }} {{ r.arrWind }}km/h {{ r.arrHumidity }}%</span>
                  </div>
                </el-col>
              </el-row>
              <el-progress :percentage="r.riskScore" :color="progressColor(r.riskScore)" :stroke-width="8" />
              <div class="rc-advice">{{ r.advice }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" style="margin-top:16px">
          <div slot="header">📊 航线风险矩阵热力图</div>
          <div ref="heatChart" class="heat-chart"></div>
        </el-card>
      </el-tab-pane>

      <!-- ===== Tab3: 极端天气趋势分析 ===== -->
      <el-tab-pane label="趋势统计" name="trend">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never">
              <div slot="header">📈 近7天极端天气机场数</div>
              <div ref="lineChart" class="mid-chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never">
              <div slot="header">🌪️ 极端天气类型分布</div>
              <div ref="barCompareChart" class="mid-chart"></div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="24">
            <el-card shadow="never">
              <div slot="header">🏆 机场可靠性排行 TOP10</div>
              <el-table :data="trendData.reliabilityRank" stripe height="320">
                <el-table-column label="排名" width="60" type="index" />
                <el-table-column label="城市" prop="city_name" width="120" />
                <el-table-column label="预警天数" prop="alert_days" width="120" />
                <el-table-column label="可靠性">
                  <template slot-scope="s">
                    <el-progress :percentage="s.row.score" :color="progressColor3(s.row.score)" :stroke-width="14" />
                  </template>
                </el-table-column>
                <el-table-column label="评分" prop="score" width="100">
                  <template slot-scope="s">
                    <el-tag :type="s.row.score >= 70 ? 'success' : (s.row.score >= 40 ? 'warning' : 'danger')">{{ s.row.score }}分</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboard, getRoutes, getTrend } from '@/api/aviation'
import { HUBS, HUBS_MAP, getBubbleStyle, LEVEL_COLORS, getCityGeo, isHub } from './constants'

export default {
  name: 'AviationAnalysis',
  data() {
    return {
      activeTab: 'dashboard',
      dash: { safeAirports: 0, delayWarning: 0, highRisk: 0, affectedRoutes: 0, riskAirports: [], recentAlerts: [], allWeather: [] },
      routeData: { routes: [], heatmapData: [], riskTypes: [] },
      trendData: { airportCountTrend: [], typeDistribution: [], reliabilityRank: [], calendar: [] },
      loading: false,
      mapChart: null, heatChart: null, lineChart: null, barCompareChart: null,
      dashTimer: null
    }
  },
  mounted() {
    this.fetchDashboard()
    this.dashTimer = setInterval(() => { this.fetchDashboard() }, 10000)
  },
  beforeDestroy() {
    if (this.dashTimer) { clearInterval(this.dashTimer); this.dashTimer = null }
    this.disposeAllCharts()
  },
  methods: {
    onTabClick(tab) {
      this.$nextTick(() => {
        if (tab.name === 'dashboard') this.initMapChart()
        else if (tab.name === 'routes') this.fetchRoutes()
        else if (tab.name === 'trend') this.fetchTrend()
      })
    },

    // ====== Tab1 Dashboard ======
    fetchDashboard() {
      getDashboard().then(res => {
        this.dash = res.data || this.dash
        this.$nextTick(() => { if (this.activeTab === 'dashboard') this.initMapChart() })
      }).catch(() => {})
    },

    initMapChart() {
      if (!this.$refs.mapChart) return
      if (this.mapChart) this.mapChart.dispose()
      this.mapChart = echarts.init(this.$refs.mapChart)

      fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
        .then(r => r.json())
        .then(geo => {
          echarts.registerMap('china', geo)
          this.renderMap()
        })
        .catch(() => {
          // fallback: scatter on blank canvas
          this.renderMap()
        })
    },

    renderMap() {
      if (!this.mapChart) return

      const allW = this.dash.allWeather || []
      const riskAirports = this.dash.riskAirports || []
      const riskMap = {}
      riskAirports.forEach(r => { riskMap[r.city] = r })

      // 所有城市散点
      const normalScatter = []
      const riskScatter = { '黄色': [], '橙色': [], '红色': [] }
      const hubScatter = []

      allW.forEach(w => {
        const city = w.city_name
        const hub = isHub(city)
        const risk = riskMap[city]
        let level = '正常'
        if (risk) level = risk.level || '黄色'

        const pos = getCityGeo(city)
        const bs = getBubbleStyle(level)
        const item = { name: city, value: [...pos, bs.size, level, w.temperature, w.wind_speed, w.humidity, w.weather] }

        if (hub) hubScatter.push(item)
        else if (level !== '正常' && riskScatter[level]) riskScatter[level].push(item)
        else normalScatter.push(item)
      })

      const series = [
        { name: '正常', type: 'scatter', coordinateSystem: 'geo', symbolSize: v => v[2], data: normalScatter, itemStyle: { color: '#67c23a', opacity: 0.6 } },
        { name: '黄色预警', type: 'scatter', coordinateSystem: 'geo', symbolSize: v => v[2], data: riskScatter['黄色'] || [], itemStyle: { color: '#ffd93d', opacity: 0.85 } },
        { name: '橙色预警', type: 'scatter', coordinateSystem: 'geo', symbolSize: v => v[2], data: riskScatter['橙色'] || [], itemStyle: { color: '#ff8800', opacity: 0.9 } },
        { name: '红色预警', type: 'scatter', coordinateSystem: 'geo', symbolSize: v => v[2], data: riskScatter['红色'] || [], itemStyle: { color: '#ff4444', opacity: 0.95 } },
        { name: '枢纽机场', type: 'effectScatter', coordinateSystem: 'geo', symbolSize: v => v[2] + 6, data: hubScatter, rippleEffect: { brushType: 'stroke', scale: 3 }, itemStyle: { color: '#1890ff' }, zlevel: 1 }
      ]

      this.mapChart.setOption({
        tooltip: { trigger: 'item', formatter: p => {
          const d = p.data
          if (!d || !d.value) return p.name
          return p.name + '<br/>' + (d.value[7] || '') + ' ' + (d.value[4] || '--') + '°C<br/>风速:' + (d.value[5] || '--') + 'km/h 湿度:' + (d.value[6] || '--') + '%'
        }},
        geo: { map: 'china', roam: true, zoom: 1.2, center: [105, 36], itemStyle: { areaColor: '#f0f5ff', borderColor: '#c0d0e0' }, emphasis: { itemStyle: { areaColor: '#e0ecff' } }, label: { show: false } },
        series
      })
    },

    // ====== Tab2 Routes ======
    fetchRoutes() {
      if (this.routeData.routes.length > 0) { this.$nextTick(() => this.initHeatChart()); return }
      getRoutes().then(res => {
        this.routeData = res.data || this.routeData
        this.$nextTick(() => this.initHeatChart())
      }).catch(() => {})
    },

    initHeatChart() {
      if (!this.$refs.heatChart) return
      if (this.heatChart) this.heatChart.dispose()
      this.heatChart = echarts.init(this.$refs.heatChart)

      const rt = this.routeData
      const yLabels = rt.routes.map(r => r.routeName)
      const xLabels = rt.riskTypes || ['大风', '暴雨', '低能见度', '综合']

      const rawData = rt.heatmapData || []
      const data = rawData.map(d => [d[1], d[0], d[2]])

      this.heatChart.setOption({
        tooltip: { position: 'top', formatter: p => yLabels[p.data[1]] + ' ' + xLabels[p.data[0]] + ': ' + p.data[2] },
        grid: { left: 100, right: 40, top: 20, bottom: 65 },
        xAxis: { type: 'category', data: xLabels, splitArea: { show: true } },
        yAxis: { type: 'category', data: yLabels, splitArea: { show: true } },
        visualMap: { min: 0, max: 100, calculable: true, orient: 'horizontal', left: 'center', bottom: 8, inRange: { color: ['#67c23a', '#ffd93d', '#ff8800', '#f56c6c'] } },
        series: [{ type: 'heatmap', data, label: { show: true, formatter: p => p.data[2] || '' }, emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } } }]
      })
    },

    // ====== Tab3 Trend ======
    fetchTrend() {
      if (this.trendData.reliabilityRank.length > 0) { this.$nextTick(() => { this.initLineChart(); this.initBarCompare() }); return }
      getTrend().then(res => {
        this.trendData = res.data || this.trendData
        this.$nextTick(() => { this.initLineChart(); this.initBarCompare() })
      }).catch(() => {})
    },

    initLineChart() {
      if (!this.$refs.lineChart) return
      if (this.lineChart) this.lineChart.dispose()
      this.lineChart = echarts.init(this.$refs.lineChart)
      const td = this.trendData.airportCountTrend || []
      this.lineChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 50, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'category', data: td.map(d => d.date) },
        yAxis: { type: 'value', name: '机场数' },
        series: [{ type: 'line', data: td.map(d => d.count), smooth: true, areaStyle: { color: 'rgba(24,144,255,0.15)' }, itemStyle: { color: '#1890ff' }, lineStyle: { width: 2 } }]
      })
    },

    initBarCompare() {
      if (!this.$refs.barCompareChart) return
      if (this.barCompareChart) this.barCompareChart.dispose()
      this.barCompareChart = echarts.init(this.$refs.barCompareChart)
      const td = this.trendData.typeDistribution || []
      this.barCompareChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['大风', '暴雨'], bottom: 0 },
        grid: { left: 50, right: 20, top: 30, bottom: 45 },
        xAxis: { type: 'category', data: td.map(d => d.date) },
        yAxis: { type: 'value', name: '次数' },
        series: [
          { name: '大风', type: 'bar', data: td.map(d => d.wind), itemStyle: { color: '#ff8800', borderRadius: [4, 4, 0, 0] } },
          { name: '暴雨', type: 'bar', data: td.map(d => d.rain), itemStyle: { color: '#1890ff', borderRadius: [4, 4, 0, 0] } }
        ]
      })
    },

    // ====== Helpers ======
    getAlertClass(a) { const l = a.alert_level || ''; if (l.includes('红')) return 'red'; if (l.includes('橙')) return 'orange'; if (l.includes('黄')) return 'yellow'; return 'blue' },
    formatTime(t) { if (!t) return ''; return t.toString().slice(11, 19) },
    levelTag(l) { if (!l) return 'info'; if (l.includes('红')) return 'danger'; if (l.includes('橙')) return 'warning'; if (l.includes('黄')) return ''; return 'info' },
    progressColor(v) { if (v < 30) return '#67c23a'; if (v < 50) return '#e6a23c'; if (v < 70) return '#ff8800'; return '#f56c6c' },
    progressColor3(v) { return v >= 70 ? '#67c23a' : (v >= 40 ? '#e6a23c' : '#f56c6c') },
    disposeAllCharts() {
      [this.mapChart, this.heatChart, this.lineChart, this.barCompareChart].forEach(c => { if (c) c.dispose() })
    }
  }
}
</script>

<style scoped>
.aviation-container { padding: 15px 15px 0 15px; background: #f2f4f8; min-height: calc(100vh - 84px); }
.aviation-header { margin-bottom: 12px; }
.aviation-header h2 { margin: 0; color: #333; font-size: 18px; }

.kpi-row { margin-bottom: 0; }
.kpi-card { text-align: center; padding: 16px 10px; border-radius: 10px; color: #fff; }
.kpi-card.safe { background: linear-gradient(135deg, #67c23a, #85ce61); }
.kpi-card.delay { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.kpi-card.risk { background: linear-gradient(135deg, #f56c6c, #f78989); }
.kpi-card.route { background: linear-gradient(135deg, #909399, #b4b8bc); }
.kpi-num { font-size: 32px; font-weight: 700; }
.kpi-label { font-size: 13px; margin-top: 4px; opacity: 0.9; }

.map-card, .alert-card, .route-card { border-radius: 10px; }
.map-chart { width: 100%; height: 400px; }
.alert-list { height: 380px; overflow-y: auto; padding: 4px 0; }
.alert-item { padding: 6px 10px; border-radius: 4px; margin-bottom: 4px; font-size: 12px; border-left: 3px solid #ddd; }
.alert-item.red { background: rgba(255,68,68,0.08); border-color: #ff4444; }
.alert-item.orange { background: rgba(255,136,0,0.08); border-color: #ff8800; }
.alert-item.yellow { background: rgba(230,162,60,0.08); border-color: #ffd93d; }
.alert-item.blue { background: rgba(0,212,255,0.08); border-color: #00d4ff; }
.alert-time { color: #999; margin-right: 6px; }
.alert-city { font-weight: 600; margin-right: 6px; }

.route-card { cursor: pointer; transition: all 0.3s; margin-bottom: 14px; }
.route-card:hover { transform: translateY(-3px); box-shadow: 0 6px 18px rgba(0,0,0,0.08); }
.rc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.rc-name { font-weight: 600; font-size: 14px; }
.rc-airports { font-size: 12px; color: #999; margin-bottom: 8px; letter-spacing: 1px; }
.rc-weather { font-size: 12px; color: #666; margin-bottom: 10px; }
.rc-dep, .rc-arr { line-height: 1.8; }
.rc-label { display: inline-block; width: 32px; font-size: 11px; color: #999; }
.rc-city { font-weight: 600; margin-right: 6px; }
.rc-temp { color: #1890ff; font-weight: 700; margin-right: 6px; }
.rc-advice { font-size: 12px; color: #888; margin-top: 8px; text-align: center; }

.heat-chart { width: 100%; height: 350px; }
.mid-chart { width: 100%; height: 300px; }

>>> .el-tabs--border-card { border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
>>> .el-tabs__content { padding: 16px; }
</style>
