<template>
  <div class="order-container">
    <h3 class="page-title">我的订单</h3>

    <el-table
        :data="orders"
        border
        style="width: 90%; margin: 20px auto"
        v-loading="loading"
    >
      <el-table-column prop="id" label="订单号" width="120" align="center"></el-table-column>

      <el-table-column label="商品信息" width="400">
        <template slot-scope="{row}">
          <div v-for="item in row.items" :key="item.id" class="goods-item">
            <el-image
                :src="item.book.url || require('@/assets/default-book.png')"
                class="book-cover"
            ></el-image>
            <div class="goods-info">
              <div class="book-name">{{ item.book.name }}</div>
              <div class="book-author">{{ item.book.author }}</div>
              <div class="quantity">x{{ item.quantity }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="总金额" width="120" align="center">
        <template slot-scope="{row}">
          ￥{{ row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>

      <el-table-column label="订单状态" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag :type="statusTagType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="下单时间" width="180" align="center">
        <template slot-scope="{row}">
          {{ formatTime(row.createtime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button
              type="info"
              size="mini"
              @click="viewDetail(row.id)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate';

export default {
  name: "OrderPage",
  data() {
    return {
      orders: [],
      loading: false
    }
  },
  created() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      this.loading = true;
      try {
        const { data } = await this.$axios.get(`${this.$httpUrl}/order/list`);
        if (data.code === 200) {
          // 遍历订单，检查是否过期并调用后端接口
          for (const order of data.data) {
            if (order.status === '已发货' && this.isOrderExpired(order.payTime)) {
              try {
                await this.$axios.post(`${this.$httpUrl}/order/auto-complete`, {
                  oid: order.id
                });
              } catch (error) {
                console.error("更新订单状态失败:", error);
              }
            }
          }
          // 重新加载订单列表
          const { data: newData } = await this.$axios.get(`${this.$httpUrl}/order/list`);
          this.orders = newData.data.sort((a, b) => {
            // 定义状态优先级
            const statusOrder = {
              '待付款': 1,
              '已付款': 2,
              '已发货': 3,
              '已完成': 4,
              '已取消': 5
            };
            // 获取状态优先级
            const aStatus = statusOrder[a.status] || 6;
            const bStatus = statusOrder[b.status] || 6;
            // 先按状态排序
            if(aStatus !== bStatus) {
              // 如果 a 的优先级比 b 高，则返回负数，表示 a 应该排在 b 前面
              // 如果 a 的优先级比 b 低，则返回正数，表示 b 应该排在 a 前面
              return aStatus - bStatus;
            }
            // 状态相同则按创建时间倒序
            return new Date(b.createtime) - new Date(a.createtime);
          });
        }
      } catch (error) {
        this.$message.error('获取订单失败');
      } finally {
        this.loading = false;
      }
    },
    isOrderExpired(payTime) {
      if (!payTime) return false;
      const payDate = new Date(payTime);
      const deadline = new Date(payDate.getTime() + 3 * 86400000); // 精确到毫秒
      return Date.now() > deadline;
    },
    statusTagType(status) {
      const map = {
        '待付款': 'warning',
        '已付款': 'success',
        '已发货': 'success',
        '已完成': 'info',
        '已取消': 'danger'
      }
      return map[status] || 'info';
    },
    formatTime(timeStr) {
      //将后端返回的时间字符串转换为Date对象
      const date = new Date(timeStr);
      return formatDate(date, 'yyyy-MM-dd hh:mm');
    },
    viewDetail(orderId) {
      this.$router.push(`/order/detail/${orderId}`);
    }
  }
}
</script>

<style scoped>
.page-title {
  text-align: center;
  margin: 20px 0;
  color: #333;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.book-cover {
  width: 60px;
  height: 80px;
  margin-right: 15px;
}

.goods-info {
  flex: 1;
}

.book-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.book-author {
  color: #666;
  font-size: 12px;
}

.quantity {
  color: #f56c6c;
  font-size: 14px;
}
</style>