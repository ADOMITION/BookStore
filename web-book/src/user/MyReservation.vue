<template>
  <div class="reservation-container">
    <el-card class="reservation-card">
      <el-tabs v-model="activeStatus">
        <!-- 预定中标签页 -->
        <el-tab-pane label="预定中" name="0">
          <div v-loading="loadingPending" class="reservation-list">
            <el-empty
                v-if="pendingList.length === 0"
                description="暂无预定中的书籍"
            ></el-empty>

            <div
                v-for="item in pendingList"
                :key="item.id"
                class="reservation-item"
            >
              <el-card shadow="hover">
                <el-row :gutter="20">
                  <el-col :span="4">
                    <el-image
                        :src="item.bookCover || require('@/assets/default-book.png')"
                        @click="handleSkip(item.bookId)"
                        class="clickable-cover book-cover"
                        fit="contain"
                    />
                  </el-col>

                  <el-col :span="16">
                    <div class="book-info">
                      <h3 class="book-title">{{ item.bookName }}</h3>
                      <div class="meta-info">
                        <span class="quantity">预定数量：{{ item.quantity }}</span>
                        <el-input-number
                            v-model="item.quantity"
                            :min="1"
                            @change="updateQuantity(item)"
                        />
                        <span class="create-time">预定时间：{{ formatTime(item.createTime) }}</span>
                      </div>
                    </div>
                  </el-col>

                  <el-col :span="4" class="action-col">
                    <el-button
                        type="danger"
                        size="small"
                        @click="handleCancel(item.id)"
                    >
                      取消预定
                    </el-button>
                  </el-col>
                </el-row>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 已到货标签页 -->
        <el-tab-pane label="已到货" name="1">
          <div v-loading="loadingArrived" class="reservation-list">
            <el-empty
                v-if="arrivedList.length === 0"
                description="暂无到货记录"
            ></el-empty>

            <div
                v-for="item in arrivedList"
                :key="item.id"
                class="reservation-item"
            >
              <el-card shadow="hover">
                <el-row :gutter="20">
                  <el-col :span="4">
                    <el-image
                        :src="item.bookCover || require('@/assets/default-book.png')"
                        @click="handleSkip(item.bookId)"
                        class="clickable-cover book-cover"
                        fit="contain"
                    />
                  </el-col>

                  <el-col :span="16">
                    <div class="book-info">
                      <h3 class="book-title">{{ item.bookName }}</h3>
                      <div class="meta-info">
                        <span class="quantity">已预定数量：{{ item.quantity }}</span>
                        <span class="quantity">到货数量：{{ item.bookStock }}</span>
                        <span class="create-time">到货时间：{{ formatTime(item.updateTime) }}</span>
                      </div>
                    </div>
                  </el-col>

                  <el-col :span="4" class="action-col">
                    <el-button
                        type="danger"
                        size="small"
                        @click="handleCancel(item.id)"
                    >
                      移除记录
                    </el-button>
                  </el-col>
                </el-row>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    <template>
      <!-- 新增分页组件 -->
      <el-pagination
          background
          layout="prev, pager, next"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :current-page="pagination.pageNum"
          @current-change="handlePageChange"
      ></el-pagination>
    </template>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate'

export default {
  name: 'MyReservation',
  data() {
    return {
      activeStatus: '0',
      pendingList: [],
      arrivedList: [],
      loadingPending: false,
      loadingArrived: false,
      pagination: {
        pageSize: 10,
        pageNum: 1,
        total: 0
      }
    }
  },
  created() {
    this.loadReservations(0)
    this.loadReservations(1)
  },
  methods: {
    async loadReservations(status) {
      const loadingKey = status === 0 ? 'loadingPending' : 'loadingArrived'
      try {
        this[loadingKey] = true
        const userId = JSON.parse(sessionStorage.getItem('CurUser')).id
        const res = await this.$axios.post(this.$httpUrl + '/reservation/listByUser', {
          pageSize: this.pagination.pageSize,
          pageNum: this.pagination.pageNum,
          param: { userId, status }
        })

        if (status === 0) {
          this.pendingList = res.data.data
          this.pagination.total = res.data.total;
        } else {
          this.arrivedList = res.data.data
          this.pagination.total = res.data.total;
        }
      } catch (error) {
        this.$message.error('获取预定记录失败')
      } finally {
        this[loadingKey] = false
      }
    },

    async handleCancel(id) {
      try {
        await this.$confirm('确定要执行该操作吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await this.$axios.post(this.$httpUrl + '/reservation/cancel', { id })
        if (res.data.code === 200) {
          this.$message.success('操作成功')
          this.loadReservations(0)
          this.loadReservations(1)
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },

    async updateQuantity(item) {
      try {
        const res = await this.$axios.post(
            this.$httpUrl + '/reservation/updateQuantity',
            { id: item.id, quantity: item.quantity }
        );
        this.$message.success('数量修改成功');
      } catch (error) {
        this.$message.error('修改失败');
      }
    },

    handlePageChange(page) {
      this.pagination.pageNum = page;
      this.loadReservations();
    },

    handleSkip(id) {
      this.$router.push({
        path: '/book',
        query: { id }
      });
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

<style scoped>
.reservation-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.reservation-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

::v-deep .el-tabs__nav-wrap {
  padding: 0 20px;
}

.reservation-item {
  margin-bottom: 16px;
}

.book-cover {
  height: 120px;
  width: 100%;
  border-radius: 4px;
}
.clickable-cover {
  cursor: pointer;
  transition: transform 0.3s;
}
.clickable-cover:hover {
  transform: scale(1.05);
}

.book-title {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.meta-info {
  color: #666;
  font-size: 14px;
}

.meta-info span {
  display: block;
  margin-bottom: 4px;
}

.action-col {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>