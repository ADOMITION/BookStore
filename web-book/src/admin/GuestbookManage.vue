<template>
  <div class="app-container">
    <el-card>
      <!-- 标题栏 -->
      <div class="filter-container">
        <h2 class="page-title">📬 留言管理</h2>
        <div class="operation-btns">
          <el-button
              type="success"
              icon="el-icon-refresh"
              @click="loadData"
          >刷新</el-button>
        </div>
      </div>

      <!-- 留言表格 -->
      <el-table
          :data="messages"
          v-loading="loading"
          border
          fit
          highlight-current-row
          style="width: 100%; margin-top: 20px;"
      >
        <el-table-column label="留言时间" width="180" align="center">
          <template slot-scope="{row}">
            <div class="time-box">{{ formatTime(row.createTime) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="留言内容" min-width="250">
          <template slot-scope="{row}">
            <div class="content-box">{{ row.content }}</div>
          </template>
        </el-table-column>

        <el-table-column label="管理员回复" min-width="300">
          <template slot-scope="{row}">
            <div v-if="!row.edit" class="reply-box">
              <span v-if="row.adminReply" class="reply-content">{{ row.adminReply }}</span>
              <span v-else class="no-reply">暂未回复</span>
              <div class="update-time" v-if="row.updateTime">
                最后更新：{{ formatTime(row.updateTime) }}
              </div>
            </div>
            <el-input
                v-else
                v-model="row.tempReply"
                type="textarea"
                :rows="3"
                placeholder="请输入回复内容"
                resize="none"
                class="reply-input"
            ></el-input>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{row}">
            <div class="action-btns">
              <el-button
                  v-if="!row.edit"
                  type="text"
                  size="small"
                  @click="startEdit(row)"
                  icon="el-icon-edit-outline"
              >回复</el-button>
              <template v-else>
                <el-button
                    type="text"
                    size="small"
                    @click="saveReply(row)"
                    icon="el-icon-check"
                    class="success-btn"
                >保存</el-button>
                <el-button
                    type="text"
                    size="small"
                    @click="cancelEdit(row)"
                    icon="el-icon-close"
                    class="cancel-btn"
                >取消</el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            background
            :current-page="page.pageNum"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="page.pageSize"
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
import { formatDate } from "@/store/formatDate";

export default {
  data() {
    return {
      loading: false,
      messages: [],
      page: {
        pageNum: 1,
        pageSize: 10
      },
      total: 0
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const res = await this.$axios.post(
            this.$httpUrl + "/guestbook/admin/list",
            this.page
        );
        if (res.data.code === 200) {
          this.messages = res.data.data.map(item => ({
            ...item,
            edit: false,
            tempReply: item.adminReply
          }));
          this.total = res.data.total;
        }
      } catch (error) {
        this.$message.error("获取留言列表失败");
      } finally {
        this.loading = false;
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

    startEdit(row) {
      row.edit = true;
    },

    cancelEdit(row) {
      row.edit = false;
      row.tempReply = row.adminReply;
    },

    async saveReply(row) {
      try {
        const res = await this.$axios.post(
            this.$httpUrl + "/guestbook/admin/reply",
            {
              id: row.id,
              adminReply: row.tempReply
            }
        );
        if (res.data.code === 200) {
          this.$message.success("回复成功");
          row.adminReply = row.tempReply;
          row.updateTime = new Date().toISOString();
          row.edit = false;
        }
      } catch (error) {
        this.$message.error("回复失败");
      }
    },

    handleSizeChange(val) {
      this.page.pageSize = val;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.page.pageNum = val;
      this.loadData();
    }
  }
};
</script>

<style scoped lang="scss">
.app-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fc 0%, #f1f3f9 100%);
  min-height: calc(100vh - 84px);

  .filter-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    padding: 16px 24px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

    .page-title {
      margin: 0;
      font-size: 20px;
      color: #2c3e50;
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .operation-btns {
      .el-button {
        padding: 10px 20px;
        border-radius: 8px;
        transition: all 0.3s ease;

        &[type="info"] {
          background: linear-gradient(135deg, #909399, #6b6d7a);
          color: white;
        }
      }
    }
  }

  .el-table {
    background: rgba(255, 255, 255, 0.98);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

    .time-box {
      color: #666;
      font-size: 13px;
    }

    .content-box {
      line-height: 1.6;
      color: #34495e;
      max-height: 60px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }

    .reply-box {
      .reply-content {
        color: #67c23a;
        background: #f0f9eb;
        padding: 6px 12px;
        border-radius: 4px;
        display: inline-block;
      }

      .no-reply {
        color: #f56c6c;
        font-style: italic;
      }

      .update-time {
        margin-top: 8px;
        font-size: 12px;
        color: #999;
      }
    }

    .reply-input {
      ::v-deep .el-textarea__inner {
        border-color: #409eff;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
      }
    }

    .action-btns {
      display: flex;
      gap: 8px;
      justify-content: center;

      .el-button {
        padding: 6px 12px;
        transition: all 0.2s;

        &.success-btn {
          color: #67c23a;
          &:hover {
            color: darken(#67c23a, 10%);
          }
        }

        &.cancel-btn {
          color: #909399;
          &:hover {
            color: darken(#909399, 10%);
          }
        }

        &:not(.is-disabled):hover {
          transform: translateY(-1px);
        }
      }
    }
  }

  .pagination-container {
    margin-top: 24px;
    padding: 16px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}
</style>