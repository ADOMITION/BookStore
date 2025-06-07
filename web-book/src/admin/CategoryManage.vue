<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-input
          v-model="searchParams.name"
          placeholder="请输入分类名称"
          prefix-icon="el-icon-collection-tag"
          class="filter-item"
          @keyup.enter.native="loadCategories"
      />

      <div class="operation-btns">
        <el-button
            type="primary"
            icon="el-icon-search"
            @click="loadCategories"
        >查询</el-button>
        <el-button
            icon="el-icon-refresh"
            @click="resetSearch"
        >重置</el-button>
        <el-button
            type="success"
            icon="el-icon-plus"
            @click="openDialog('add')"
        >新增</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="120" align="center" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <div class="action-btns">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="openDialog('edit', scope.row)"
          >编辑</el-button>
          <el-popconfirm
              title="确定要删除该分类吗？"
              @confirm="deleteCategory(scope.row.id)"
          >
            <el-button
                slot="reference"
                size="mini"
                type="text"
                icon="el-icon-delete"
                class="delete-btn"
            >删除</el-button>
          </el-popconfirm>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        background
        :current-page="pagination.current"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 编辑对话框 -->
    <el-dialog
        :title="dialogTitle"
        :visible.sync="dialogVisible"
        width="500px"
    >
      <el-form
          ref="categoryForm"
          :model="formData"
          :rules="formRules"
          label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CategoryManage',
  data() {
    return {
      searchParams: {
        name: ''
      },
      tableData: [],
      loading: false,
      dialogVisible: false,
      dialogType: 'add',
      formData: this.initFormData(),
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      formRules: {
        name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增分类' : '编辑分类'
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    initFormData() {
      return {
        id: null,
        name: ''
      }
    },
    // 加载分类数据
    async loadCategories() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size,
          param: this.searchParams
        }
        const { data } = await this.$axios.post(this.$httpUrl+'/category/listPage', params)
        if (data.code === 200) {
          this.tableData = data.data
          this.pagination.total = data.total
        }
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    // 打开对话框
    openDialog(type, row) {
      this.dialogType = type
      this.dialogVisible = true
      this.$nextTick(() => {
        if (type === 'edit') {
          this.formData = { ...row }
        } else {
          this.formData = this.initFormData()
        }
      })
    },
    // 提交表单
    submitForm() {
      this.$refs.categoryForm.validate(async valid => {
        if (valid) {
          const api = this.dialogType === 'add' ? '/category/save' : '/category/update'
          const { data } = await this.$axios.post(this.$httpUrl+api, this.formData)
          if (data.code === 200) {
            this.$message.success('操作成功')
            this.dialogVisible = false
            this.loadCategories()
          }
        }
      })
    },
    // 删除分类
    async deleteCategory(id) {
      const { data } = await this.$axios.get(this.$httpUrl+`/category/del?id=${id}`)
      if (data.code === 200) {
        this.$message.success('删除成功')
        this.loadCategories()
      }
    },
    // 分页处理
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadCategories()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadCategories()
    },
    // 重置搜索
    resetSearch() {
      this.searchParams = { name: '' }
      this.loadCategories()
    }
  }
}
</script>


<style scoped lang="scss">
.app-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fc 0%, #f1f3f9 100%);
  min-height: calc(100vh - 84px);

  // 搜索区域
  .filter-container {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
    margin-bottom: 24px;
    padding: 20px 24px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
    gap: 16px;

    &:hover {
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    }

    ::v-deep .el-input__inner {
      background: rgba(245, 247, 250, 0.6);
      border-radius: 8px;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(245, 247, 250, 0.8);
        border-color: #c0c4cc;
      }

      &:focus-within {
        background: #fff;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }

    .operation-btns {
      margin-left: auto;
      display: flex;
      gap: 12px;

      .el-button {
        padding: 10px 20px;
        border-radius: 8px;
        font-weight: 500;
        transition: all 0.3s ease;

        &[type="primary"] {
          background: linear-gradient(135deg, #409eff, #3375e0);
          border: none;
          color: white;

          &:hover {
            opacity: 0.9;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
          }
        }

        &[type="success"] {
          background: linear-gradient(135deg, #67c23a, #529b2d);
          border: none;
          color: white;

          &:hover {
            opacity: 0.9;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
          }
        }

        &:not([type]) {
          background: rgba(245, 247, 250, 0.8);
          color: #606266;
          border: 1px solid #dcdfe6;

          &:hover {
            background: rgba(245, 247, 250, 1);
            color: #409eff;
            border-color: #c6e2ff;
          }
        }
      }
    }
  }

  // 表格区域
  ::v-deep .el-table {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    }

    th {
      background: linear-gradient(135deg, #f8f9fc, #f1f3f9) !important;
      color: #2c3e50;
      font-weight: 600;
      height: 52px;
    }

    tr {
      transition: all 0.2s ease;
      td {
        padding: 12px 0;
      }
    }
    .action-btns {
      .delete-btn{
        color: #F56C6C;
        &:hover {
          color: darken(#F56C6C, 10%);
          background-color: rgba(204, 25, 25, 0.09);
        }
      }
    }

    .el-button--text {
      padding: 7px 10px;
      border-radius: 6px;
      transition: all 0.2s ease;
      &:hover {
        background: rgba(64, 158, 255, 0.1);
        transform: scale(1.05);
      }

      &[style*="color: #f56c6c"]:hover {
        background: rgba(245, 108, 108, 0.1);
      }
    }
  }

  // 分页样式
  .el-pagination {
    margin-top: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);

    ::v-deep {
      .btn-prev,
      .btn-next,
      .el-pager li {
        border-radius: 8px;
        margin: 0 4px;
        transition: all 0.2s ease;
      }

      .el-pager li.active {
        background: linear-gradient(135deg, #409eff, #3375e0);
        color: white;
        font-weight: 500;
      }
    }
  }

  // 对话框样式
  ::v-deep .el-dialog {
    border-radius: 16px;
    overflow: hidden;
    background: linear-gradient(145deg, #f8f9fc, #ffffff);

    &__header {
      padding: 20px;
      background: linear-gradient(90deg, #f8f9fc, #ffffff);
      border-bottom: 1px solid #ebeef5;

      .el-dialog__title {
        color: #2c3e50;
        font-size: 18px;
        font-weight: 600;
      }
    }

    &__body {
      padding: 20px;
    }

    .el-form-item__label {
      color: #5c6b77;
      font-weight: 500;
    }

    .el-input__inner {
      border-radius: 8px;
    }
  }
}

// 全局加载样式
::v-deep .el-loading-mask {
  background: rgba(255, 255, 255, 0.8);

  .el-loading-spinner {
    i {
      color: #409eff;
    }

    .el-loading-text {
      color: #606266;
      letter-spacing: 0.5px;
    }
  }
}

.filter-item {
  width: 410px;
}
</style>