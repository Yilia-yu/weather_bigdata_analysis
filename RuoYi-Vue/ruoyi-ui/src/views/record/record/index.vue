<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="城市名称" prop="cityName">
        <el-input
          v-model="queryParams.cityName"
          placeholder="请输入城市名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属省份" prop="province">
        <el-input
          v-model="queryParams.province"
          placeholder="请输入所属省份"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属区域" prop="region">
        <el-input
          v-model="queryParams.region"
          placeholder="请输入所属区域"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="温度(°C)" prop="temperature">
        <el-input
          v-model="queryParams.temperature"
          placeholder="请输入温度(°C)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="湿度(%)" prop="humidity">
        <el-input
          v-model="queryParams.humidity"
          placeholder="请输入湿度(%)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风速(km/h)" prop="windSpeed">
        <el-input
          v-model="queryParams.windSpeed"
          placeholder="请输入风速(km/h)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="天气(晴/多云/雨/雪等)" prop="weather">
        <el-input
          v-model="queryParams.weather"
          placeholder="请输入天气(晴/多云/雨/雪等)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="空气质量指数" prop="aqi">
        <el-input
          v-model="queryParams.aqi"
          placeholder="请输入空气质量指数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="空气质量等级" prop="aqiLevel">
        <el-input
          v-model="queryParams.aqiLevel"
          placeholder="请输入空气质量等级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据来源" prop="dataSource">
        <el-input
          v-model="queryParams.dataSource"
          placeholder="请输入数据来源"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="记录时间" prop="recordTime">
        <el-date-picker clearable
          v-model="queryParams.recordTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择记录时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['record:record:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['record:record:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['record:record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['record:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="城市名称" align="center" prop="cityName" />
      <el-table-column label="所属省份" align="center" prop="province" />
      <el-table-column label="所属区域" align="center" prop="region" />
      <el-table-column label="温度(°C)" align="center" prop="temperature" />
      <el-table-column label="湿度(%)" align="center" prop="humidity" />
      <el-table-column label="风速(km/h)" align="center" prop="windSpeed" />
      <el-table-column label="天气(晴/多云/雨/雪等)" align="center" prop="weather" />
      <el-table-column label="空气质量指数" align="center" prop="aqi" />
      <el-table-column label="空气质量等级" align="center" prop="aqiLevel" />
      <el-table-column label="数据来源" align="center" prop="dataSource" />
      <el-table-column label="记录时间" align="center" prop="recordTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.recordTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['record:record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['record:record:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改天气数据记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="城市名称" prop="cityName">
              <el-input v-model="form.cityName" placeholder="请输入城市名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属省份" prop="province">
              <el-input v-model="form.province" placeholder="请输入所属省份" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属区域" prop="region">
              <el-input v-model="form.region" placeholder="请输入所属区域" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="温度(°C)" prop="temperature">
              <el-input v-model="form.temperature" placeholder="请输入温度(°C)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="湿度(%)" prop="humidity">
              <el-input v-model="form.humidity" placeholder="请输入湿度(%)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风速(km/h)" prop="windSpeed">
              <el-input v-model="form.windSpeed" placeholder="请输入风速(km/h)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="天气(晴/多云/雨/雪等)" prop="weather">
              <el-input v-model="form.weather" placeholder="请输入天气(晴/多云/雨/雪等)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="空气质量指数" prop="aqi">
              <el-input v-model="form.aqi" placeholder="请输入空气质量指数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="空气质量等级" prop="aqiLevel">
              <el-input v-model="form.aqiLevel" placeholder="请输入空气质量等级" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据来源" prop="dataSource">
              <el-input v-model="form.dataSource" placeholder="请输入数据来源" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="记录时间" prop="recordTime">
              <el-date-picker clearable
                v-model="form.recordTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择记录时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRecord, getRecord, delRecord, addRecord, updateRecord } from "@/api/record/record"

export default {
  name: "Record",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 天气数据记录表格数据
      recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        cityName: null,
        province: null,
        region: null,
        temperature: null,
        humidity: null,
        windSpeed: null,
        weather: null,
        aqi: null,
        aqiLevel: null,
        dataSource: null,
        recordTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        cityName: [
          { required: true, message: "城市名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询天气数据记录列表 */
    getList() {
      this.loading = true
      listRecord(this.queryParams).then(response => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        cityName: null,
        province: null,
        region: null,
        temperature: null,
        humidity: null,
        windSpeed: null,
        weather: null,
        aqi: null,
        aqiLevel: null,
        dataSource: null,
        recordTime: null,
        createTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加天气数据记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getRecord(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改天气数据记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRecord(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除天气数据记录编号为"' + ids + '"的数据项？').then(function() {
        return delRecord(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('record/record/export', {
        ...this.queryParams
      }, `record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
