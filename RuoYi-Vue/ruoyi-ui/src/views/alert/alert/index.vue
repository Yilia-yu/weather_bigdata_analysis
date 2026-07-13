<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监测字段(temperature/humidity/wind_speed)" prop="metricField">
        <el-input
          v-model="queryParams.metricField"
          placeholder="请输入监测字段(temperature/humidity/wind_speed)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下限阈值" prop="thresholdMin">
        <el-input
          v-model="queryParams.thresholdMin"
          placeholder="请输入下限阈值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上限阈值" prop="thresholdMax">
        <el-input
          v-model="queryParams.thresholdMax"
          placeholder="请输入上限阈值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警级别(红色/橙色/黄色/蓝色)" prop="alertLevel">
        <el-input
          v-model="queryParams.alertLevel"
          placeholder="请输入告警级别(红色/橙色/黄色/蓝色)"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['alert:alert:add']"
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
          v-hasPermi="['alert:alert:edit']"
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
          v-hasPermi="['alert:alert:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alert:alert:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alertList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="规则名称" align="center" prop="ruleName" />
      <el-table-column label="告警类型(高温/低温/大风/暴雨)" align="center" prop="alertType" />
      <el-table-column label="监测字段(temperature/humidity/wind_speed)" align="center" prop="metricField" />
      <el-table-column label="下限阈值" align="center" prop="thresholdMin" />
      <el-table-column label="上限阈值" align="center" prop="thresholdMax" />
      <el-table-column label="告警级别(红色/橙色/黄色/蓝色)" align="center" prop="alertLevel" />
      <el-table-column label="状态(1=启用 0=停用)" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['alert:alert:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alert:alert:remove']"
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

    <!-- 添加或修改天气告警规则配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监测字段(temperature/humidity/wind_speed)" prop="metricField">
              <el-input v-model="form.metricField" placeholder="请输入监测字段(temperature/humidity/wind_speed)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="下限阈值" prop="thresholdMin">
              <el-input v-model="form.thresholdMin" placeholder="请输入下限阈值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="上限阈值" prop="thresholdMax">
              <el-input v-model="form.thresholdMax" placeholder="请输入上限阈值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="告警级别(红色/橙色/黄色/蓝色)" prop="alertLevel">
              <el-input v-model="form.alertLevel" placeholder="请输入告警级别(红色/橙色/黄色/蓝色)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listAlert, getAlert, delAlert, addAlert, updateAlert } from "@/api/alert/alert"

export default {
  name: "Alert",
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
      // 天气告警规则配置表格数据
      alertList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleName: null,
        alertType: null,
        metricField: null,
        thresholdMin: null,
        thresholdMax: null,
        alertLevel: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ruleName: [
          { required: true, message: "规则名称不能为空", trigger: "blur" }
        ],
        alertType: [
          { required: true, message: "告警类型(高温/低温/大风/暴雨)不能为空", trigger: "change" }
        ],
        metricField: [
          { required: true, message: "监测字段(temperature/humidity/wind_speed)不能为空", trigger: "blur" }
        ],
        alertLevel: [
          { required: true, message: "告警级别(红色/橙色/黄色/蓝色)不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询天气告警规则配置列表 */
    getList() {
      this.loading = true
      listAlert(this.queryParams).then(response => {
        this.alertList = response.rows
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
        ruleName: null,
        alertType: null,
        metricField: null,
        thresholdMin: null,
        thresholdMax: null,
        alertLevel: null,
        status: null,
        createTime: null,
        updateTime: null,
        remark: null
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
      this.title = "添加天气告警规则配置"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAlert(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改天气告警规则配置"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlert(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAlert(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除天气告警规则配置编号为"' + ids + '"的数据项？').then(function() {
        return delAlert(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alert/alert/export', {
        ...this.queryParams
      }, `alert_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
