<template>
  <div class="common-main">
    <el-form :rules="rules" ref="ruleForm" :model="ruleForm" label-width="100px"
             class="search-container">

      <el-form-item label="时间">
        <el-date-picker class="widthauto" v-model="ruleForm.dateRange" :editable="false" value-format="yyyy-MM-dd" format="yyyy-MM-dd" type="datetimerange" start-placeholder="开始日期" end-placeholder="结束日期" clearable>
        </el-date-picker>
      </el-form-item>

      <el-form-item label="姓名" prop="userName">
        <el-input v-model="ruleForm.userName" class="form-input" clearable></el-input>
      </el-form-item>

      <div class="operation-container">
        <el-button icon="el-icon-search" @click="submitForm('ruleForm')">搜索</el-button>
      </div>
    </el-form>

    <el-table
      ref="multipleTable"
      :data="listData"
      tooltip-effect="dark"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      v-loading="listLoading"
    >
      <el-table-column
        prop="group"
        label="日志类型"
      >
      </el-table-column>

      <el-table-column
        prop="created"
        label="时间"
      >
      </el-table-column>
      <el-table-column
        prop="username"
        label="操作人员"
      >
      </el-table-column>
      <el-table-column
        prop="ip"
        label="IP地址"
      >
      </el-table-column>
      <el-table-column
        prop="remark"
        label="详情"
      >
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination-container"
      background
      layout="total,prev, pager, next"
      :current-page.sync="listQuery.current"
      :page-size="listQuery.size"
      :total="listQuery.total"
      @current-change="handlePageChange"
    >
    </el-pagination>

  </div>
</template>

<script>
  import commonMixin from '@/components/mixin/common-mixin'
  import { sysConfigApi } from '@/api/sysConfigApi'

  export default {
    mixins: [commonMixin],
    data() {
      return {
        rules: {},
        ruleForm: {
          userName: '',
          dateRange: ''
        }
      }
    },
    methods: {
      listCallback() {
        return sysConfigApi.getSysUserLog(this.ruleForm,
          this.listQuery.current, this.listQuery.size)
      },
      getInitForm(data) {
        const { role_strings = '' } = data
        return {
          ...data,
          role_strings_array: role_strings.split(',').filter(item => !!item)
        }
      },
      handleCreate() {
        this.$refs.employeeDialog.showDialog(1, this.getInitForm({}))
      },
      handleEdit(index, row) {
        this.$refs.employeeDialog.showDialog(2, this.getInitForm({ ...row }))
      }
    }
  }
</script>

<style scoped>

</style>

