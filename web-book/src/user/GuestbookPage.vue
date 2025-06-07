<template>
  <div class="guestbook-container">
    <el-card class="book-style-card">
      <!-- 卡片头部 -->
      <div slot="header" class="card-header">
        <div class="title-wrapper">
          <i class="el-icon-notebook-2 header-icon"></i>
          <span class="card-title">读者留言板</span>
        </div>
        <div class="decorative-line"></div>
      </div>

      <!-- 留言输入区 -->
      <div class="message-input-area">
        <el-form @submit.native.prevent="submit">
          <el-form-item>
            <el-input
                type="textarea"
                v-model="form.content"
                :rows="4"
                placeholder="写下您的阅读感悟、书籍建议或任何想对我们说的话..."
                maxlength="500"
                show-word-limit
                class="parchment-input"
                resize="none"
            >
              <i slot="prefix" class="el-icon-edit pen-icon"></i>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
                type="primary"
                native-type="submit"
                class="submit-btn"
            >
              <i class="el-icon-s-promotion btn-icon"></i>
              提交留言
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 留言列表 -->
      <div class="message-list">
        <el-divider class="vintage-divider">
          <i class="el-icon-collection-tag"></i>
          历史留言
        </el-divider>

        <el-table
            :data="messages"
            v-loading="loading"
            style="width: 100%"
            class="book-table"
            empty-text="暂无留言，快来留下第一个书评吧~"
        >
          <el-table-column label="留言时间" width="180" align="center">
            <template slot-scope="{row}">
              <div class="time-cell">
                <i class="el-icon-time time-icon"></i>
                {{ formatTime(row.createTime) }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="留言内容" min-width="300">
            <template slot-scope="{row}">
              <div class="content-cell">
                <div class="quote-mark left">“</div>
                <div class="content-text">{{ row.content }}</div>
                <div class="quote-mark right">”</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="店长回复" width="280" align="center">
            <template slot-scope="{row}">
              <div v-if="row.adminReply" class="reply-cell">
                <div class="admin-label">
                  <i class="el-icon-user-solid"></i>
                  店长回复
                </div>
                <div class="reply-content">{{ row.adminReply }}</div>
                <div class="reply-time">{{ formatTime(row.updateTime) }}</div>
              </div>
              <div v-else class="no-reply">
                <i class="el-icon-coffee-cup"></i>
                等待店长泡咖啡时回复...
              </div>
            </template>
          </el-table-column>

          <el-table-column
              label="操作"
              width="120"
              align="center"
              v-if="loginStatus"
          >
            <template slot-scope="{row}">
              <el-button
                  v-if="row.userId === currentUserId"
                  type="danger"
                  icon="el-icon-delete"
                  circle
                  @click="handleDelete(row.id)"
                  class="delete-btn"
              ></el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
            class="book-pagination"
            @current-change="handlePageChange"
            :current-page="page.pageNum"
            :page-size="page.pageSize"
            :total="total"
            layout="prev, pager, next"
            prev-text="上一页"
            next-text="下一页"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate';

export default {
  data() {
    return {
      currentUserId: null,   // 当前登录用户ID
      loginStatus: false,    // 登录状态
      loading: false,
      form: { content: '' },
      messages: [],
      page: { pageNum: 1, pageSize: 5 },
      total: 0
    }
  },
  mounted() {
    this.checkLoginStatus();
    this.loadData()
  },
  methods: {
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
    async loadData() {
      this.loading = true;
      try {
        const res = await this.$axios.post(
            this.$httpUrl+'/guestbook/user/list',
            this.page
        );
        if(res.data.code === 200) {
          this.messages = res.data.data;
          this.total = res.data.total;
        }
      } catch (error) {
        this.$message.error("获取留言失败");
      } finally {
        this.loading = false;
      }
    },
    async submit() {
      if (!this.form.content.trim()) {
        this.$message.warning("留言内容不能为空哦~");
        return;
      }

      try {
        const res = await this.$axios.post(
            this.$httpUrl+'/guestbook/submit',
            this.form
        );
        if(res.data.code === 200) {
          this.$message.success({
            message: '留言成功！店长会尽快回复~',
            iconClass: 'el-icon-success'
          });
          this.form.content = '';
          this.loadData();
        }
      } catch (error) {
        this.$message.error("提交留言失败");
      }
    },
    checkLoginStatus() {
      const userData = sessionStorage.getItem('CurUser');
      if (userData) {
        try {
          const user = JSON.parse(userData);
          this.currentUserId = user.id;
          this.loginStatus = true;
        } catch (e) {
          console.error('用户信息解析失败:', e);
        }
      }
    },
    handleDelete(id) {
      this.$confirm('确定要删除这条留言吗？', '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '再想想',
        type: 'warning',
        customClass: 'delete-dialog',
        confirmButtonClass: 'el-button--danger',
        cancelButtonClass: 'el-button--primary'
      }).then(async () => {
        try {
          const res = await this.$axios.get(this.$httpUrl+'/guestbook/delete', {
            params: { id }
          });
          if(res.data.code === 200) {
            this.$message.success({
              message: '删除成功',
              iconClass: 'el-icon-success'
            });
            // 重新加载数据
            this.loadData();
          }
        } catch (error) {
          this.$message.error('删除失败，请稍后重试');
        }
      }).catch(() => {});
    },
    handlePageChange(val) {
      this.page.pageNum = val;
      this.loadData();
    }
  }
}
</script>

<style scoped lang="scss">
.guestbook-container {
  padding: 30px;
  min-height: calc(100vh - 60px);

  .book-style-card {
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(94, 73, 62, 0.15);
    border: none;

    .card-header {
      .title-wrapper {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        .header-icon {
          font-size: 28px;
          color: #8b572a;
          margin-right: 12px;
        }

        .card-title {
          font-size: 24px;
          font-family: 'Microsoft YaHei', cursive;
          color: #4a3527;
          letter-spacing: 2px;
        }
      }

      .decorative-line {
        height: 2px;
        background: linear-gradient(90deg, transparent, #d4c8b8, transparent);
      }
    }

    .message-input-area {
      margin: 24px 0;

      .parchment-input {
        ::v-deep .el-textarea__inner {
          background: #fff9f0;
          border: 1px solid #e5d5c3;
          border-radius: 8px;
          padding: 16px 20px;
          font-size: 15px;
          color: #66594d;
          box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);

          &::placeholder {
            color: #b8a795;
          }
        }

        .pen-icon {
          font-size: 20px;
          color: #8b572a;
          margin: 10px;
        }
      }

      .submit-btn {
        background: linear-gradient(135deg, #8b572a, #6d4221);
        border: none;
        border-radius: 24px;
        padding: 12px 36px;
        font-size: 16px;
        letter-spacing: 2px;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(139, 87, 42, 0.3);
        }

        .btn-icon {
          margin-right: 8px;
        }
      }
    }

    .message-list {
      .vintage-divider {
        ::v-deep .el-divider__text {
          background: #f5efe9;
          color: #6d4221;
          font-size: 16px;
          padding: 0 20px;

          i {
            margin-right: 8px;
          }
        }
      }

      .book-table {
        margin: 20px 0;

        .time-cell {
          color: #8b572a;
          display: flex;
          align-items: center;
          justify-content: center;

          .time-icon {
            margin-right: 6px;
            font-size: 16px;
          }
        }

        .content-cell {
          position: relative;
          padding: 12px;
          background: #fbf6f0;
          border-radius: 6px;

          .quote-mark {
            position: absolute;
            color: #d4c8b8;
            font-size: 32px;
            font-family: serif;
            &.left { left: 8px; top: -8px; }
            &.right { right: 8px; bottom: -12px; }
          }

          .content-text {
            line-height: 1.8;
            color: #66594d;
          }
        }

        .reply-cell {
          background: #f0f5ff;
          border-radius: 6px;
          padding: 12px;
          border-left: 3px solid #8b572a;

          .admin-label {
            color: #8b572a;
            font-size: 12px;
            margin-bottom: 6px;

            i {
              margin-right: 4px;
            }
          }

          .reply-content {
            color: #4a4a4a;
            line-height: 1.6;
          }

          .reply-time {
            color: #999;
            font-size: 12px;
            margin-top: 6px;
          }
        }

        .no-reply {
          color: #b8a795;
          font-style: italic;
          padding: 8px;

          i {
            margin-right: 6px;
          }
        }
      }

      .book-pagination {
        margin-top: 24px;
        ::v-deep .btn-prev,
        ::v-deep .btn-next {
          background: #f5efe9 !important;
          border-radius: 4px;
          padding: 0 12px;
        }

        ::v-deep .el-pager li {
          background: #f5efe9;
          border-radius: 4px;
          margin: 0 4px;
          &.active {
            background: #8b572a;
            color: white;
          }
        }
      }
    }
  }
}
.delete-btn {
  transform: scale(0.8);
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1);
    box-shadow: 0 2px 6px rgba(255, 73, 73, 0.3);
  }

  &:active {
    transform: scale(0.9);
  }
}

::v-deep .delete-dialog {
  .el-message-box__headerbtn {
    &:hover .el-icon-close {
      color: #ff4949;
    }
  }

  .el-message-box__btns {
    .el-button--danger {
      background: linear-gradient(135deg, #ff4949, #d93636);
      border: none;
      &:hover {
        opacity: 0.9;
      }
    }

    .el-button--primary {
      background: linear-gradient(135deg, #8b572a, #6d4221);
      border: none;
    }
  }
}
</style>