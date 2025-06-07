<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="filter-container">
        <el-input
            v-model="searchForm.bookName"
            placeholder="输入书籍名称"
            clearable
            prefix-icon="el-icon-notebook-1"
            style="width: 200px; margin-right: 10px;"
            @keyup.enter.native="loadReviews"
        />
        <el-input
            v-model="searchForm.userName"
            placeholder="输入用户名称"
            clearable
            prefix-icon="el-icon-user"
            style="width: 200px; margin-right: 10px;"
            @keyup.enter.native="loadReviews"
        />
        <div class="operation-btns">
          <el-button
              type="primary"
              icon="el-icon-search"
              @click="loadReviews"
          >查询</el-button>
          <el-button
              icon="el-icon-refresh"
              @click="resetSearch"
          >重置</el-button>
        </div>
      </div>

      <!-- 评论表格 -->
      <el-table
          :data="reviewList"
          v-loading="loading"
          border
          fit
          highlight-current-row
          style="width: 100%; margin-top: 20px;"
      >
        <el-table-column label="用户" width="180">
          <template slot-scope="{row}">
            <div class="user-info">
              <el-avatar :src="row.avatar" />
              <div style="margin-left: 10px;">
                <div>{{ row.userName }}</div>
                <div style="font-size: 12px; color: #909399;">ID: {{ row.userId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="书籍信息" min-width="180">
          <template slot-scope="{row}">
            <div class="book-info">
              <div class="book-name">{{ row.bookName }}</div>
              <div style="font-size: 12px; color: #909399;">ID: {{ row.bookId }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="content" label="评论内容" min-width="250">
          <template slot-scope="{row}">
            <div class="content-box">{{ row.content }}</div>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="180" align="center">
          <template slot-scope="{row}">
            {{ formatTime(row.createtime) }}
          </template>
        </el-table-column>

        <el-table-column label="互动数据" width="120" align="center">
          <template slot-scope="{row}">
            <div style="line-height: 1.5;">
              <div><i class="el-icon-thumb"></i> {{ row.likeCount }}</div>
              <div><i class="el-icon-chat-dot-round"></i> {{ row.replyCount }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag
                :type="row.status === 0 ? 'warning' : (row.status === 1 ? 'success' : 'danger')"
                effect="dark"
                size="small"
            >
              {{ row.status === 0 ? '待审核' : (row.status === 1 ? '已通过' : '未通过') }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{row}">
            <div class="action-btns">
              <el-button
                  v-if="row.status === 0"
                  type="text"
                  size="small"
                  @click="handleApprove(row.id)"
                  icon="el-icon-check"
              >通过</el-button>
              <el-button
                  v-if="row.status === 0"
                  type="text"
                  size="small"
                  @click="handleRefuse(row.id)"
                  icon="el-icon-close"
                  class="no-btn"
              >不通过</el-button>
              <el-button
                  v-if="row.status === 1 || row.status === 2"
                  type="text"
                  size="small"
                  @click="handleDelete(row.id)"
                  icon="el-icon-delete"
                  class="delete-btn"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            background
            :current-page="pagination.pageNum"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import {formatDate} from "@/store/formatDate";

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return statusMap[status]
    },
    statusText(status) {
      const statusMap = {
        2: '未通过',
        1: '已通过',
        0: '待审核'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      loading: false,
      reviewList: [],
      total: 0,
      pagination: {
        pageNum: 1,
        pageSize: 10
      },
      searchForm: {
        bookName: '',
        userName: ''
      }
    }
  },
  created() {
    this.loadReviews()
  },
  methods: {
    async loadReviews() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          param: {
            bookName: this.searchForm.bookName,
            userName: this.searchForm.userName
          }
        }
        const res = await this.$axios.post(this.$httpUrl+'/review/list', params)
        if (res.data.code === 200) {
          this.reviewList = res.data.data
          this.total = res.data.total
        }
      } catch (error) {
        console.error(error)
        this.$message.error('获取评论列表失败')
      } finally {
        this.loading = false
      }
    },
    formatTime(time) {
      if (!time) return "暂无时间信息";
      try {
        const dateStr = typeof time === "string" ? time : time.toString();
        return formatDate(new Date(dateStr), "yyyy-MM-dd hh:mm:ss");
      } catch (e) {
        console.error("时间格式错误:", time);
        return "时间格式异常";
      }
    },

    resetSearch(){
      this.searchForm = {
        bookName: '',
        userName: ''
      }
      this.pagination.pageNum = 1
      this.loadReviews()
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.loadReviews()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.loadReviews()
    },

    async handleApprove(id) {
      try {
        const res = await this.$axios.post(this.$httpUrl+'/review/updateStatus', { id, status: 1 })
        if (res.data.code === 200) {
          this.$message.success('审核通过')
          this.loadReviews()
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async handleRefuse(id) {
      try {
        const res = await this.$axios.post(this.$httpUrl+'/review/updateStatus', { id, status: 2 })
        if (res.data.code === 200) {
          this.$message.success('审核未通过')
          this.loadReviews()
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    handleDelete(id) {
      this.$confirm('此操作将永久删除该评论，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await this.$axios.post(this.$httpUrl+'/review/delete', { id })
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.loadReviews()
        }
      }).catch(() => {})
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

    .filter-item {
      width: 240px;

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

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      .el-avatar {
        flex-shrink: 0;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }

    th {
      background: linear-gradient(135deg, #f8f9fc, #f1f3f9) !important;
      color: #2c3e50;
      font-weight: 600;
    }
    tr {
      transition: all 0.2s ease;
      &:hover {
        transform: translateX(4px);
        box-shadow: 4px 0 12px rgba(64, 158, 255, 0.1);
      }
    }

    .action-btns {
      // 调整按钮内边距
      .el-button {
        margin: 0;
        flex-shrink: 0;
      }
      // 删除按钮颜色
      .delete-btn {
        color: #F56C6C;
        &:hover {
          color: darken(#F56C6C, 10%);
          background-color: rgba(204, 25, 25, 0.09);
        }
      }
      .no-btn {
        color: #9b22e1;
        &:hover {
          color: darken(#9b22e1, 10%);
          background-color: rgba(112, 25, 204, 0.14);
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
</style>