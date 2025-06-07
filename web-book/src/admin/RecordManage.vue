<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-input
          v-model="searchParams.bookName"
          placeholder="书名"
          prefix-icon="el-icon-notebook-1"
          class="filter-item"
          style="width: 240px"
          @keyup.enter.native="loadRecords"
      />
      <el-select
          v-model="searchParams.type"
          placeholder="类型"
          clearable
          class="filter-item"
          style="width: 140px"
      >
        <el-option label="入库" :value="1" />
        <el-option label="出库" :value="0" />
      </el-select>
      <el-date-picker
          v-model="searchParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          class="filter-item"
          style="width: 360px"
      />

      <div class="operation-btns">
        <el-button
            type="primary"
            icon="el-icon-search"
            @click="loadRecords"
        >搜索</el-button>
        <el-button
            icon="el-icon-refresh"
            @click="resetSearch"
        >重置</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="bookName" label="书名" />
      <el-table-column label="类型" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.type ? 'success' : 'warning'" effect="dark">
            {{ row.type ? '入库' : '出库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="120" align="center" />
      <el-table-column prop="userName" label="操作人" />
      <el-table-column prop="createTime" label="操作时间" width="180" />
      <el-table-column prop="mark" label="备注" show-overflow-tooltip />
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
  </div>
</template>

<script>
export default {
  name: 'RecordManage',
  data() {
    return {
      searchParams: {
        bookName: '',
        type: null,
        dateRange: []
      },
      tableData: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    this.loadRecords()
  },
  methods: {
    async loadRecords() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size,
          param: {
            bookName: this.searchParams.bookName,
            type: this.searchParams.type,
            startDate: this.searchParams.dateRange?.[0] || '',
            endDate: this.searchParams.dateRange?.[1] || ''
          }
        }
        const {data} = await this.$axios.post(this.$httpUrl + '/record/listPage', params)
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
    resetSearch() {
      this.searchParams = {
        bookName: '',
        type: null,
        dateRange: []
      }
      this.loadRecords()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadRecords()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadRecords()
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

    ::v-deep .el-input__inner,
    ::v-deep .el-range-input,
    ::v-deep .el-range-separator {
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

    .el-tag {
      font-weight: 500;

      &--success {
        background: rgba(103, 194, 58, 0.1);
        border-color: rgba(103, 194, 58, 0.2);
        color: #67c23a;
      }

      &--warning {
        background: rgba(230, 162, 60, 0.1);
        border-color: rgba(230, 162, 60, 0.2);
        color: #e6a23c;
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
  ::v-deep .el-range-separator {
    padding: 0 8px;
  }
}
</style>