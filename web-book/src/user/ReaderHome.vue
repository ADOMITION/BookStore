<template>
  <div class="reader-home-container">
    <!-- 热门书籍推荐 -->
    <section class="hot-books-section">
      <h2 class="section-title">热门好书推荐 🔥</h2>
      <el-carousel :interval="5000" height="300px" arrow="always">
        <el-carousel-item v-for="group in bookGroups" :key="group.id">
          <div class="book-group">
            <div v-for="book in group.items" :key="book.id" class="book-item" @click="goBookDetail(book.id)">
              <el-image
                  :src="book.url"
                  fit="cover"
                  class="book-cover"
              ></el-image>
              <div class="book-info">
                <h3>{{ book.name }}</h3>
                <p class="author">{{ book.author }}</p>
                <div class="meta">
                  <el-rate
                      v-model="book.mark"
                      disabled
                      :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  />
                  <span class="price">¥{{ book.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 读者讨论区 -->
    <section class="discussion-section">
      <div class="discussion-header">
        <h2 class="section-title">书友交流区 💬</h2>
        <el-button
            type="primary"
            icon="el-icon-edit"
            @click="openReviewDialog"
        >发表书评</el-button>
      </div>

      <!-- 书评列表 -->
      <div class="review-list">
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <el-avatar :src="review.avatar" class="user-avatar"></el-avatar>
          <div class="review-content">
            <div class="review-header">
              <span class="username">{{ review.userName }}</span>
              <!-- 评分显示 -->
              <el-rate
                  v-model="review.mark"
                  disabled
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  :max="5"
                  :allow-half="true"
                  class="review-rating"
              />
              <span class="time">{{ formatTime(review.createtime) }}</span>
            </div>
            <p class="review-text">{{ review.content }}</p>
            <div class="review-actions">
              <el-button
                  type="text"
                  icon="el-icon-thumb"
                  :class="{ 'liked': review.hasLiked }"
                  @click="handleLike(review)"
              >
                点赞 {{ review.likeCount || 0 }}
              </el-button>

              <el-button
                  type="text"
                  icon="el-icon-chat-line-round"
                  @click="toggleReplyInput(review.id)"
              >
                回复 {{ review.replyCount || 0 }}
              </el-button>
            </div>

            <!-- 回复输入框 -->
            <div v-if="activeReplyId === review.id" class="reply-section">
              <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  placeholder="写下你的回复..."
                  resize="none"
                  maxlength="200"
                  show-word-limit
              ></el-input>
              <div class="reply-buttons">
                <el-button
                    size="mini"
                    @click="cancelReply"
                >取消</el-button>
                <el-button
                    type="primary"
                    size="mini"
                    :loading="replying"
                    @click="submitReply(review)"
                >提交</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 发表书评对话框 -->
    <el-dialog title="发表书评" :visible.sync="reviewDialogVisible">
      <el-form :model="reviewForm">
        <el-form-item label="选择书籍">
          <el-select
              v-model="reviewForm.bookId"
              filterable
              remote
              placeholder="输入书名搜索"
              :remote-method="searchBooks"
              :loading="searchLoading"
          >
            <el-option
                v-for="book in searchResults"
                :key="book.id"
                :label="book.name"
                :value="book.id"
            >
              <span>{{ book.name }}</span>
              <span style="float:right;color:#8492a6;font-size:13px">{{ book.author }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="我的评分">
          <el-rate v-model="reviewForm.mark" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
        </el-form-item>
        <el-form-item label="书评内容">
          <el-input
              type="textarea"
              :rows="4"
              v-model="reviewForm.content"
              maxlength="500"
              show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交书评</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'ReaderHome',
  data() {
    return {
      // 回复相关状态
      activeReplyId: null,  // 当前展开回复的评论ID
      replyContent: '',     // 回复内容
      replying: false,      // 提交加载状态
      bookGroups: [],
      reviews: [],
      reviewDialogVisible: false,
      reviewForm: {
        bookId: null,
        content: '',
        mark: 5
      },
      searchResults: [],
      searchLoading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        // 加载热门书籍
        const bookRes = await this.$axios.get(this.$httpUrl + '/book/hot', {
          pageNum: 1,
          pageSize: 8
        })
        if(bookRes.data.code === 200) {
          this.bookGroups = this.chunkArray(bookRes.data.data, 4)
        }

        // 加载热门书评（状态为已经审核通过的书评）
        const reviewRes = await this.$axios.get(this.$httpUrl + '/review/hot')
        if(reviewRes.data.code === 200) {
          this.reviews = reviewRes.data.data.filter(item => item.status === 1) // 新增过滤
        }
      } catch (error) {
        console.error('数据加载失败:', error)
        this.$message.error('数据加载失败')
      }
    },

    // 将数组分块
    chunkArray(arr, size) {
      return arr.reduce((acc, _, i) => {
        if (i % size === 0) {
          // 为每个分组添加唯一标识
          acc.push({
            id: `group-${i/size}`, // 生成唯一ID
            items: arr.slice(i, i + size)
          })
        }
        return acc
      }, [])
    },

    formatTime(time) {
      return dayjs(time).format('YYYY-MM-DD HH:mm')
    },

    openReviewDialog() {
      const user = JSON.parse(sessionStorage.getItem('CurUser'))
      if (!user || !user.id) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.reviewDialogVisible = true
    },

    async searchBooks(query) {
      if (query) {
        this.searchLoading = true
        try {
          const res = await this.$axios.post(this.$httpUrl + '/book/search', {
            pageNum: 1,
            pageSize: 10,
            param: {
              keyword: query
            }
          })
          if(res.data.code === 200) {
            this.searchResults = res.data.data
          }
        } catch (error) {
          this.$message.error('搜索失败')
        } finally {
          this.searchLoading = false
        }
      }
    },

    async submitReview() {
      try {
        const user = JSON.parse(sessionStorage.getItem('CurUser'))
        if (!user) return

        const payload = {
          ...this.reviewForm,
          mark:this.reviewForm.mark,
          userId: user.id,
          avatar: user.avatar
        }
        const res = await this.$axios.post(
            this.$httpUrl + '/review/submit',
            payload
        )
        if(res.data.code === 200) {
          this.$message.success('书评发表成功,正在等待审核！！！')
          this.reviewDialogVisible = false
          await this.loadData()
        } else {
          console.log(res.data.msg)
          this.$message.error(res.data.msg || '提交失败')
        }
      } catch (error) {
        this.$message.error('提交失败: ' + (error.response?.data?.msg || error.message))
      }
    },

    goBookDetail(id) {
      this.$router.push({
        path: '/book',
        query:{
          id:id
        }
      })
    },

  }
}
</script>

<style scoped>
.reader-home-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.section-title {
  color: #2c3e50;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
  margin: 30px 0;
}

/* 书籍推荐样式 */
.book-group {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px;
}

.book-item {
  cursor: pointer;
  transition: transform 0.3s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.book-item:hover {
  transform: translateY(-5px);
}

.book-cover {
  width: 100%;
  height: 180px;
}

.book-info {
  padding: 15px;
}

.book-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
}

.author {
  color: #909399;
  font-size: 12px;
  margin-bottom: 10px;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #F56C6C;
  font-weight: bold;
}

/* 讨论区样式 */
.discussion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.review-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.review-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.user-avatar {
  flex-shrink: 0;
  margin-right: 15px;
}

.review-content {
  flex-grow: 1;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  font-weight: bold;
  margin-right: 15px;
}

.time {
  color: #909399;
  font-size: 12px;
  margin-left: auto;
}

.review-text {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.review-actions {
  margin-top: 10px;
}

.review-actions {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 点赞按钮激活状态 */
.liked {
  color: #f56c6c !important;
}

/* 回复区域样式 */
.reply-section {
  margin-top: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
}

.reply-buttons {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 评分组件调整 */
.review-rating {
  margin-left: 10px;
}
.review-rating ::v-deep .el-rate__icon {
  font-size: 18px;
}

/* 时间显示样式 */
.time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}
</style>