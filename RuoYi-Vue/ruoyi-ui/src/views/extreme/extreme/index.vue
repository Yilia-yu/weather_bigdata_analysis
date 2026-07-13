<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="cityName">
        <el-input
          v-model="queryParams.cityName"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="province">
        <el-input
          v-model="queryParams.province"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="alertLevel">
        <el-input
          v-model="queryParams.alertLevel"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="metricField">
        <el-input
          v-model="queryParams.metricField"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="alertValue">
        <el-input
          v-model="queryParams.alertValue"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="weatherDesc">
        <el-input
          v-model="queryParams.weatherDesc"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="alertTime">
        <el-date-picker clearable
          v-model="queryParams.alertTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择${comment}">
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
          v-hasPermi="['extreme:extreme:add']"
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
          v-hasPermi="['extreme:extreme:edit']"
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
          v-hasPermi="['extreme:extreme:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['extreme:extreme:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="extremeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="${comment}" align="center" prop="cityName" />
      <el-table-column label="${comment}" align="center" prop="province" />
      <el-table-column label="${comment}" align="center" prop="alertType" />
      <el-table-column label="${comment}" align="center" prop="alertLevel" />
      <el-table-column label="${comment}" align="center" prop="metricField" />
      <el-table-column label="${comment}" align="center" prop="alertValue" />
      <el-table-column label="${comment}" align="center" prop="weatherDesc" />
      <el-table-column label="${comment}" align="center" prop="alertTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alertTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['extreme:extreme:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['extreme:extreme:remove']"
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

    <!-- 添加或修改极端天气预警记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="cityName">
              <el-input v-model="form.cityName" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="province">
              <el-input v-model="form.province" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="alertLevel">
              <el-input v-model="form.alertLevel" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="metricField">
              <el-input v-model="form.metricField" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="alertValue">
              <el-input v-model="form.alertValue" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="weatherDesc">
              <el-input v-model="form.weatherDesc" placeholder="请输入${comment}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="${comment}" prop="alertTime">
              <el-date-picker clearable
                v-model="form.alertTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择${comment}">
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
import { listExtreme, getExtreme, delExtreme, addExtreme, updateExtreme } from "@/api/extreme/extreme"

export default {
  name: "Extreme",
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
      // 极端天气预警记录表格数据
      extremeList: [],
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
        alertType: null,
        alertLevel: null,
        metricField: null,
        alertValue: null,
        weatherDesc: null,
        alertTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        cityName: [
          { required: true, message: "$comment不能为空", trigger: "blur" }
        ],
        alertType: [
          { required: true, message: "$comment不能为空", trigger: "change" }
        ],
        alertLevel: [
          { required: true, message: "$comment不能为空", trigger: "blur" }
        ],
        metricField: [
          { required: true, message: "$comment不能为空", trigger: "blur" }
        ],
        alertValue: [
          { required: true, message: "$comment不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询极端天气预警记录列表 */
    getList() {
      this.loading = true
      listExtreme(this.queryParams).then(response => {
        this.extremeList = response.rows
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
        alertType: null,
        alertLevel: null,
        metricField: null,
        alertValue: null,
        weatherDesc: null,
        alertTime: null,
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
      this.title = "添加极端天气预警记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getExtreme(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改极端天气预警记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateExtreme(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addExtreme(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除极端天气预警记录编号为"' + ids + '"的数据项？').then(function() {
        return delExtreme(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('extreme/extreme/export', {
        ...this.queryParams
      }, `extreme_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
