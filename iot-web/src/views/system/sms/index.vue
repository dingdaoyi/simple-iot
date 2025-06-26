<script>
import { addSmsConfig, deleteSmsConfig, getSmsConfigList, sendSms, setDefaultConfig, updateConfigStatus, updateSmsConfig } from '@/api/sms'

export default {
  name: 'SmsConfig',
  data() {
    return {
      list: null,
      listLoading: true,
      listQuery: {
        name: undefined,
      },
      temp: {
        id: undefined,
        name: '',
        supplier: '',
        accessKey: '',
        secretKey: '',
        signName: '',
        templateId: '',
        endpoint: '',
        region: '',
      },
      sendTemp: {
        phone: '',
        message: '',
        configId: undefined,
      },
      dialogFormVisible: false,
      sendDialogVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建',
      },
      rules: {
        name: [{ required: true, message: '配置名称必填', trigger: 'blur' }],
        supplier: [{ required: true, message: '供应商必选', trigger: 'change' }],
        accessKey: [{ required: true, message: '访问密钥必填', trigger: 'blur' }],
        secretKey: [{ required: true, message: '密钥必填', trigger: 'blur' }],
      },
      sendRules: {
        phone: [
          { required: true, message: '手机号必填', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
        ],
        message: [{ required: true, message: '短信内容必填', trigger: 'blur' }],
      },
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      getSmsConfigList(this.listQuery).then((response) => {
        this.list = response.data
        this.listLoading = false
      })
    },
    handleFilter() {
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        supplier: '',
        accessKey: '',
        secretKey: '',
        signName: '',
        templateId: '',
        endpoint: '',
        region: '',
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          addSmsConfig(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000,
            })
            this.getList()
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          updateSmsConfig(this.temp).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000,
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('此操作将永久删除该配置, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        deleteSmsConfig(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000,
          })
          this.getList()
        })
      })
    },
    handleSetDefault(row) {
      setDefaultConfig(row.id).then(() => {
        this.$notify({
          title: '成功',
          message: '设置默认配置成功',
          type: 'success',
          duration: 2000,
        })
        this.getList()
      })
    },
    handleStatusChange(row) {
      updateConfigStatus(row.id, row.status).then(() => {
        this.$notify({
          title: '成功',
          message: '状态更新成功',
          type: 'success',
          duration: 2000,
        })
      })
    },
    handleSendSms() {
      this.sendTemp = {
        phone: '',
        message: '',
        configId: undefined,
      }
      this.sendDialogVisible = true
      this.$nextTick(() => {
        this.$refs.sendForm.clearValidate()
      })
    },
    sendSms() {
      this.$refs.sendForm.validate((valid) => {
        if (valid) {
          sendSms(this.sendTemp).then(() => {
            this.sendDialogVisible = false
            this.$notify({
              title: '成功',
              message: '短信发送成功',
              type: 'success',
              duration: 2000,
            })
          })
        }
      })
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    },
  },
}
</script>

<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.name"
        placeholder="配置名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter="handleFilter"
      />
      <el-button class="filter-item" type="primary" icon="Search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="Plus" @click="handleCreate">
        添加
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="success" @click="handleSendSms">
        发送短信
      </el-button>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="ID" width="95">
        <template #default="scope">
          {{ scope.row.id }}
        </template>
      </el-table-column>
      <el-table-column label="配置名称">
        <template #default="scope">
          {{ scope.row.name }}
          <el-tag v-if="scope.row.isDefault" type="success" size="small">
            默认
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="供应商" width="110" align="center">
        <template #default="scope">
          <span>{{ scope.row.supplier }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签名" width="110" align="center">
        <template #default="scope">
          {{ scope.row.signName }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="2"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180" align="center">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button v-if="!scope.row.isDefault" type="success" size="small" @click="handleSetDefault(scope.row)">
            设为默认
          </el-button>
          <el-button v-if="!scope.row.isDefault" size="small" type="danger" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogFormVisible" :title="textMap[dialogStatus]">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="100px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-select v-model="temp.supplier" placeholder="请选择供应商">
            <el-option label="阿里云" value="alibaba" />
            <el-option label="腾讯云" value="tencent" />
            <el-option label="华为云" value="huawei" />
          </el-select>
        </el-form-item>
        <el-form-item label="访问密钥" prop="accessKey">
          <el-input v-model="temp.accessKey" />
        </el-form-item>
        <el-form-item label="密钥" prop="secretKey">
          <el-input v-model="temp.secretKey" type="password" />
        </el-form-item>
        <el-form-item label="短信签名" prop="signName">
          <el-input v-model="temp.signName" />
        </el-form-item>
        <el-form-item label="模板ID" prop="templateId">
          <el-input v-model="temp.templateId" />
        </el-form-item>
        <el-form-item label="接入点" prop="endpoint">
          <el-input v-model="temp.endpoint" />
        </el-form-item>
        <el-form-item label="区域" prop="region">
          <el-input v-model="temp.region" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="sendDialogVisible" title="发送短信">
      <el-form
        ref="sendForm"
        :rules="sendRules"
        :model="sendTemp"
        label-position="left"
        label-width="100px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="sendTemp.phone" />
        </el-form-item>
        <el-form-item label="短信内容" prop="message">
          <el-input v-model="sendTemp.message" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="配置">
          <el-select v-model="sendTemp.configId" placeholder="使用默认配置" clearable>
            <el-option
              v-for="item in list"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="sendDialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="sendSms">
            发送
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
