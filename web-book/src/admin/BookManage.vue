<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-input
          v-model="searchParams.name"
          placeholder="请输入书名"
          prefix-icon="el-icon-notebook-1"
          class="filter-item"
          @keyup.enter.native="loadBooks"
      />
      <el-input
          v-model="searchParams.author"
          placeholder="请输入作者"
          prefix-icon="el-icon-user-solid"
          class="filter-item"
          @keyup.enter.native="loadBooks"
      />
      <el-input
          v-model="searchParams.press"
          placeholder="请输入出版社"
          prefix-icon="el-icon-office-building"
          class="filter-item"
          @keyup.enter.native="loadBooks"
      />

      <div class="operation-btns">
        <el-button
            type="primary"
            icon="el-icon-search"
            @click="loadBooks"
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
        <el-button
            type="warning"
            icon="el-icon-download"
            :disabled="!selectedBook"
            @click="openStockDialog('out')"
        >出库</el-button>
        <el-button
            type="success"
            icon="el-icon-upload2"
            :disabled="!selectedBook"
            @click="openStockDialog('in')"
        >入库</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <!-- 添加选择列 -->
      <el-table-column type="selection" width="55" :selectable="checkSelectable"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column label="封面" width="120">
        <template slot-scope="{row}">
          <el-image
              :src="row.url"
              style="width: 80px; height: 100px"
              fit="contain"
          ></el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="press" label="出版社" />
      <el-table-column prop="date" label="出版日期" width="120" />
      <el-table-column label="价格" width="120">
        <template slot-scope="{row}">¥{{ row.price.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="状态" width="120">
        <template slot-scope="{row}">
          <el-tag :type="row.status ? 'success' : 'danger'">
            {{ row.status ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
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
              title="确定要删除该书籍吗？"
              @confirm="deleteBook(scope.row.id)"
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
        width="600px"
    >
      <el-form
          ref="bookForm"
          :model="formData"
          :rules="formRules"
          label-width="80px"
      >
        <el-form-item label="书名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="formData.press" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
              v-model="formData.price"
              :min="0"
              :precision="2"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number
              v-model="formData.stock"
              :min="0"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="评分" prop="mark">
          <el-input-number
              v-model="formData.mark"
              :min="5"
              :max="10"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="出版日期" prop="date">
          <el-date-picker
              v-model="formData.date"
              type="date"
              value-format="yyyy-MM-dd"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类" prop="cid">
          <el-select
              v-model="formData.cid"
              placeholder="请选择分类"
              style="width: 100%"
          >
            <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面" prop="url">
          <el-upload
              :action="$httpUrl + '/file/uploadBookCover'"
              :data="{ bookId: formData.id }"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
          >
            <el-button type="primary">上传封面</el-button>
            <div v-if="formData.url" class="preview">
              <img :src="formData.url" alt="封面预览" style="max-width: 200px; margin-top: 10px">
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介" prop="info">
          <el-input
              type="textarea"
              :rows="4"
              v-model="formData.info"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
              v-model="formData.status"
              :active-value="1"
              :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 出入库对话框 -->
    <el-dialog
        :title="stockDialogTitle"
        :visible.sync="stockDialogVisible"
        width="500px"
    >
      <el-form ref="stockForm" :model="stockForm" :rules="stockRules" label-width="80px">
        <el-form-item label="书名">
          <el-input
              :value="selectedBook ? selectedBook.name : ''"
              disabled
              placeholder="请先在表格中选择书籍"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-tag :type="stockType === 'in' ? 'success' : 'warning'">
            {{ stockType === 'in' ? '入库' : '出库' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number
              v-model="stockForm.quantity"
              :min="1"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="mark">
          <el-input
              type="textarea"
              :rows="3"
              v-model="stockForm.mark"
              placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStock">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'BookManage',
  data() {
    return {
      selectedBook: null, // 添加选中书籍状态
      stockDialogVisible: false,
      stockType: 'in',
      stockForm: {
        quantity: 1,
        mark: ''
      },
      stockRules: {
        quantity: [
          { required: true, message: '请输入数量', trigger: 'blur' },
          { type: 'number', min: 1, message: '数量至少为1', trigger: 'blur' }
        ]
      },

      searchParams: {
        name: '',
        author: '',
        press: ''
      },
      tableData: [],
      categories: [],
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
        name: [{ required: true, message: '请输入书名', trigger: 'blur' }],
        author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
        press: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
        date: [{ required: true, message: '请选择出版日期', trigger: 'change' }],
        cid: [{ required: true, message: '请选择分类', trigger: 'change' }],
        mark: [{ required: true, message: '请输入评分', trigger: 'blur' }],
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增书籍' : '编辑书籍'
    },
    stockDialogTitle() {
      return this.stockType === 'in' ? '入库操作' : '出库操作'
    }
  },
  created() {
    this.loadBooks()
    this.loadCategories()
  },
  methods: {
    initFormData() {
      return {
        id: null,
        name: '',
        author: '',
        press: '',
        price: 0,
        stock: 0,
        date: '',
        cid: null,
        url: '',
        mark:'',
        info: '',
        status: 1
      }
    },
    // 加载分类数据
    async loadCategories() {
      const { data } = await this.$axios.get(this.$httpUrl+'/category/list')
      if (data.code === 200) {
        this.categories = data.data
      }
    },
    // 加载书籍数据
    async loadBooks() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size,
          param: this.searchParams
        }
        const { data } = await this.$axios.post(this.$httpUrl+'/book/listPage', params)
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
      this.$refs.bookForm.validate(async valid => {
        if (valid) {
          const api = this.dialogType === 'add' ? '/book/save' : '/book/update'
          const { data } = await this.$axios.post(this.$httpUrl+api, this.formData)
          if (data.code === 200) {
            this.$message.success('操作成功')
            this.dialogVisible = false
            this.loadBooks()
          }
        }
      })
    },
    // 删除书籍
    async deleteBook(id) {
      const { data } = await this.$axios.get(this.$httpUrl+`/book/del?id=${id}`)
      if (data.code === 200) {
        this.$message.success('删除成功')
        this.loadBooks()
      }
    },
    //打开出入库对话框
    openStockDialog(type) {
      if (!this.selectedBook) {
        this.$message.warning('请先选择书籍');
        return;
      }
      this.stockType = type;
      this.stockDialogVisible = true;
    },
    //提交表单
    async submitStock() {
      this.$refs.stockForm.validate(async valid => {
        if (valid) {
          const params = {
            bookId: this.selectedBook.id,
            type: this.stockType === 'in' ? 1 : 0,
            quantity: this.stockForm.quantity,
            mark: this.stockForm.mark
          };
          try {
            const { data } = await this.$axios.post(this.$httpUrl + '/record/save', params);
            if (data.code === 200) {
              this.$message.success('操作成功');
              this.stockDialogVisible = false;
              this.loadBooks();
            }
          } catch (error) {
            this.$message.error(error.response?.data?.msg || '操作失败');
          }
        }
      });
    },

    // 处理行选择变化
    handleSelectionChange(selection) {
      this.selectedBook = selection.length > 0 ? selection[0] : null
    },

    // 可选：限制只能选择有效行（如已上架书籍）
    checkSelectable(row) {
      return row.status === 1 // 只能选择已上架的书籍
    },

    // 分页处理
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadBooks()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadBooks()
    },
    // 重置搜索
    resetSearch() {
      this.searchParams = { name: '', author: '', press: '' }
      this.loadBooks()
    },
    // 图片上传处理
    handleUploadSuccess(res) {
      if (res.code === 200){
        this.formData.url = res.data.url
        this.loadBooks();
      }
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

    .filter-item {
      width: 240px;
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
      &:hover {
        transform: translateX(4px);
      }
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

    .preview img {
      border: 1px solid #eee;
      border-radius: 4px;
      padding: 5px;
      margin-top: 10px;
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
</style>