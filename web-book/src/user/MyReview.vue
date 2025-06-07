<template>
  <div class="review-container">
    <el-card class="review-card">
      <el-tabs v-model="activeStatus" @tab-click="handleTabChange">
        <!-- 审核中标签页 -->
        <el-tab-pane label="审核中" name="0">
          <div v-loading="loadingPending" class="review-list">
            <el-empty v-if="pendingReviews.length === 0" description="暂无待审核评论"></el-empty>

            <div v-for="item in pendingReviews" :key="item.id" class="review-item">
              <el-card shadow="hover">
                <div class="review-header">
                  <span class="book-name">《{{ item.bookName }}》</span>
                  <el-tag type="warning" size="small">审核中</el-tag>
                </div>

                <div class="review-content">
                  {{ item.content }}
                </div>

                <div class="review-footer">
                  <span class="review-time">
                    {{ formatTime(item.createtime) }}
                  </span>
                  <div class="review-stats">
                    <span class="like-count">
                      <i class="el-icon-thumb"></i> {{ item.likeCount }}
                    </span>
                    <span class="reply-count">
                      <i class="el-icon-chat-dot-round"></i> {{ item.replyCount }}
                    </span>
                  </div>
                </div>
                <div class="action-btns">
                  <el-button
                      type="text"
                      size="small"
                      @click="handleDelete(item.id,item.status)"
                      icon="el-icon-delete"
                      class="delete-btn"
                  >删除</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 已通过标签页 -->
        <el-tab-pane label="已通过" name="1">
          <div v-loading="loadingApproved" class="review-list">
            <el-empty v-if="approvedReviews.length === 0" description="暂无通过评论"></el-empty>

            <div v-for="item in approvedReviews" :key="item.id" class="review-item">
              <el-card shadow="hover">
                <div class="review-header">
                  <span class="book-name">《{{ item.bookName }}》</span>
                  <el-tag type="success" size="small">已通过</el-tag>
                </div>

                <div class="review-content">
                  {{ item.content }}
                </div>

                <div class="review-footer">
                  <span class="review-time">
                    {{ formatTime(item.createtime) }}
                  </span>
                  <div class="review-stats">
                    <span class="like-count">
                      <i class="el-icon-thumb"></i> {{ item.likeCount }}
                    </span>
                    <span class="reply-count">
                      <i class="el-icon-chat-dot-round"></i> {{ item.replyCount }}
                    </span>
                  </div>
                </div>
                <div class="action-btns">
                  <el-button
                      type="text"
                      size="small"
                      @click="handleDelete(item.id,item.status)"
                      icon="el-icon-delete"
                      class="delete-btn"
                  >删除</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 已通过标签页 -->
        <el-tab-pane label="未通过" name="2">
          <div v-loading="loadingApproved" class="review-list">
            <el-empty v-if="refuseReviews.length === 0" description="暂无未通过评论"></el-empty>

            <div v-for="item in refuseReviews" :key="item.id" class="review-item">
              <el-card shadow="hover">
                <div class="review-header">
                  <span class="book-name">《{{ item.bookName }}》</span>
                  <el-tag type="danger" size="small">未通过</el-tag>
                </div>

                <div class="review-content">
                  {{ item.content }}
                </div>

                <div class="review-footer">
                  <span class="review-time">
                    {{ formatTime(item.createtime) }}
                  </span>
                  <div class="review-stats">
                    <span class="like-count">
                      <i class="el-icon-thumb"></i> {{ item.likeCount }}
                    </span>
                    <span class="reply-count">
                      <i class="el-icon-chat-dot-round"></i> {{ item.replyCount }}
                    </span>
                  </div>
                </div>
                <div class="action-btns">
                  <el-button
                      type="text"
                      size="small"
                      @click="handleDelete(item.id,item.status)"
                      icon="el-icon-delete"
                      class="delete-btn"
                  >删除</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>


      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate'

export default {
  name: 'MyReview',
  data() {
    return {
      activeStatus: '0',  // 当前激活的标签页
      pendingReviews: [],  // 待审核评论
      approvedReviews: [], // 已通过评论
      refuseReviews: [],  //  未通过评论
      loadingPending: false, // 审核中数据加载状态
      loadingApproved: false, // 已通过数据加载状态
      loadingRefuse: false,  // 未通过数据加载状态
      queryParams: {
        pageSize: 10,
        pageNum: 1
      }
    }
  },
  created() {
    // 初始化加载数据
    this.loadReviews(0)
    this.loadReviews(1)
    this.loadReviews(2)
  },
  methods: {
    // 加载评论数据
    async loadReviews(status) {
      const loadingKey = status === 0 ? 'loadingPending' : (status === 1 ? 'loadingApproved':'loadingRefuse')
      try {
        this[loadingKey] = true
        const userId = JSON.parse(sessionStorage.getItem('CurUser')).id
        const res = await this.$axios.post(this.$httpUrl + '/review/listByUser', {
          pageSize: this.queryParams.pageSize,
          pageNum: this.queryParams.pageNum,
          param: {
            userId,
            status
          }
        })

        if (status === 0) {
          this.pendingReviews = res.data.data
        } else if(status === 1){
          this.approvedReviews = res.data.data
        }else{
          this.refuseReviews = res.data.data
        }
      } catch (error) {
        this.$message.error('获取评论失败')
      } finally {
        this[loadingKey] = false
      }
    },

    handleDelete(id,status) {
      this.$confirm('此操作将永久删除该评论，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await this.$axios.post(this.$httpUrl+'/review/delete', { id })
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.loadReviews(status)
        }
      }).catch(() => {})
    },

    // 处理标签切换
    handleTabChange(tab) {
      this.activeStatus = tab.name
    },

    // 格式化时间显示
    formatTime(time) {
      if (!time) return '暂无时间信息';
      try {
        // 处理后端返回的时间格式
        const dateStr = typeof time === 'string' ? time : time.toString();
        return formatDate(new Date(dateStr), 'yyyy-MM-dd hh:mm:ss');
      } catch (e) {
        console.error('时间格式错误:', time);
        return '时间格式异常';
      }
    }
  }
}
</script>

<style scoped lang="scss">
.review-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.review-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.review-card:hover {
  box-shadow: 0 6px 24px rgba(0,0,0,0.12);
}

::v-deep .el-tabs__nav-wrap {
  padding: 0 24px;
}

::v-deep .el-tabs__item {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

::v-deep .el-tabs__item.is-active {
  color: #409EFF;
}

.review-item {
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.review-item:hover {
  transform: translateY(-2px);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.book-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 0.5px;
}

.review-content {
  color: #606266;
  font-size: 15px;
  line-height: 1.7;
  margin-bottom: 16px;
  padding: 0 8px;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
  padding-top: 12px;
}

.review-stats span {
  margin-left: 20px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.review-stats i {
  font-size: 14px;
  color: #909399;
}

/*空状态*/
::v-deep .el-empty__description p {
  color: #909399;
  font-size: 15px;
}

/* 标签样式 */
.el-tag {
  border-radius: 4px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 响应式 */
@media (max-width: 768px) {
  .review-container {
    padding: 12px;
  }

  .book-name {
    font-size: 16px;
  }

  .review-content {
    font-size: 14px;
  }

  ::v-deep .el-tabs__item {
    font-size: 14px;
  }
}

/* 卡片hover动画 */
.el-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
}

.el-card:hover {
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
}

/* 时间文字 */
.review-time {
  font-family: monospace;
  font-size: 13px;
}

/* 状态标签颜色 */
.el-tag--success {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}

.el-tag--warning {
  background-color: #fdf6ec;
  border-color: #faecd8;
  color: #e6a23c;
}

.el-tag--danger {
  background-color: #fef0f0;
  border-color: #fde2e2;
  color: #f56c6c;
}

.action-btns {
  text-align: right;
  .delete-btn{
    color: #F56C6C;
    &:hover {
      color: darken(#f40b0b, 10%);
      background-color: rgba(204, 25, 25, 0.09);
    }
  }
}
</style>