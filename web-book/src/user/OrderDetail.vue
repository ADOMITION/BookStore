<template>
  <div class="detail-container">
    <el-card class="order-card">
      <!-- 订单基本信息 -->
      <div class="order-header">
        <h2>订单号：{{ order.id }}</h2>
        <el-tag :type="statusTagType(order.status)" size="medium">
          {{ order.status }}
        </el-tag>
      </div>

      <!-- 商品信息 -->
      <el-divider content-position="left">商品信息</el-divider>
      <el-table :data="order.items" border>
        <el-table-column label="商品封面" width="120" align="center">
          <template slot-scope="{row}">
            <el-image
                :src="row.book.url || require('@/assets/default-book.png')"
                class="book-cover"
            ></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="book.name" label="书名"></el-table-column>
        <el-table-column label="单价" width="120" align="center">
          <template slot-scope="{row}">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center"></el-table-column>
        <el-table-column label="小计" width="120" align="center">
          <template slot-scope="{row}">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 金额汇总 -->
      <div class="amount-summary">
        <div class="total-line">
          <span>商品总价：</span>
          <span>¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 订单信息 -->
      <el-divider content-position="left">订单信息</el-divider>
      <div class="order-info">
        <div class="info-item">
          <label>下单时间：</label>
          <span>{{ formatTime(order.createtime) }}</span>
        </div>
        <div class="info-item" v-if="order.status === '已付款' || order.status === '已完成'|| order.status === '已发货'">
          <label>支付时间：</label>
          <span>{{ formatTime(order.payTime) }}</span>
          <span v-if="remainingRefundTime && order.status === '已发货'" class="refund-tip">
            （剩余退款时间：{{ remainingRefundTime }}）
          </span>
          <span v-if=" order.status === '已付款'" class="refund-tip">
            （已经付款时间：{{paidTime }}）
          </span>
        </div>
        <!-- 状态提示信息 -->
        <div class="info-item status-message" v-if="order.status === '已付款'">
          <el-tag type="primary" effect="light">正在等待发货</el-tag>
        </div>
        <div class="info-item status-message" v-if="order.status === '已发货'">
          <el-tag type="success" effect="light">已发货，你的包裹已在路上</el-tag>
        </div>
        <!-- 可编辑信息区块 -->
        <template v-if="order.status === '待付款'">
          <el-divider content-position="left">收货信息（可编辑）</el-divider>
          <el-form :model="form" ref="formRef" label-width="100px">
            <el-form-item label="收货地址" prop="address">
              <el-input v-model="form.address" clearable></el-input>
            </el-form-item>
            <el-form-item label="收件人" prop="recipient">
              <el-input v-model="form.recipient" clearable></el-input>
            </el-form-item>
            <el-form-item label="联系方式" prop="tel">
              <el-input v-model="form.tel" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </template>
        <template v-else>
          <el-divider content-position="left">收货信息</el-divider>
          <div class="info-item">
            <label>收货地址：</label>
            <span>{{ order.address }}</span>
          </div>
          <div class="info-item">
            <label>收件人：</label>
            <span>{{ order.recipient }}</span>
          </div>
          <div class="info-item">
            <label>联系方式：</label>
            <span>{{ order.tel }}</span>
          </div>
        </template>
      </div>
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
            v-if="order.status === '待付款'"
            type="primary"
            @click="handlePay"
            :loading="loading"
        >
          立即支付
        </el-button>
        <el-button
            v-if="order.status === '待付款'"
            type="danger"
            @click="handleCancel"
            :loading="loading"
        >
          取消订单
        </el-button>

        <el-button
            v-if="['已完成', '已取消'].includes(order.status)"
            type="danger"
            @click="handleDelete"
            :loading="loading"
        >
          删除订单
        </el-button>

        <el-button
            v-if="showRefundButton"
            type="warning"
            @click="handleRefund"
            :loading="loading"
        >
          申请退款
        </el-button>

        <el-button @click="$router.go(-1)">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate';

export default {
  name: 'OrderDetail',
  data() {
    return {
      order: {
        id: null,
        items: [],
        status: '',
        totalAmount: 0,
        createtime: '',
        address: '',
        recipient: '',
        tel: ''
      },
      form: {
        address: '',
        recipient: '',
        tel: ''
      },
      payTime: null,       // 从接口获取实际支付时间
      loading: false,
      refundDeadline: 3    // 退款有效期天数
    }
  },


  watch: {
    // 当订单数据加载完成后初始化表单
    order: {
      handler(newVal) {
        this.form = {
          address: newVal.address,
          recipient: newVal.recipient,
          tel: newVal.tel
        }
      },
      immediate: true
    }
  },
  computed: {
    statusTagType() {
      return (status) => {
        const map = {
          '待付款': 'warning',
          '已付款': 'success',
          '已发货': 'success',
          '已完成': 'info',
          '已取消': 'danger'
        }
        return map[status] || 'info';
      }
    },

    //显示退款按钮条件
    showRefundButton() {
      return this.order.status === '已付款' || (this.order.status === '已发货' && this.remainingRefundTime !== '已过期')
    },
    paidTime() {
      // 参数检查：若支付时间不存在，直接返回 null
      if (!this.payTime) return null
      // 初始化支付时间对象
      const payDate = new Date(this.payTime)
      const now = new Date()
      // 时间差计算（当前时间 - 支付时间）
      const diff = now - payDate
      // 时间单位转换
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor(
          (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
      )
      return `${days}天${hours}小时`
    },


    // 计算剩余退款时间
    remainingRefundTime() {
      //参数检查：若支付时间不存在，直接返回 null（无有效数据可计算）
      if (!this.payTime) return null
      //初始化支付时间对象（将存储的支付时间转为 Date 类型）
      const payDate = new Date(this.payTime)
      //计算退款截止日期：支付时间 + 退款期限天数（3天）
      //`setDate`方法会修改原日期对象并返回毫秒时间戳，重新创建Date对象确保正确性
      const deadline = new Date(payDate.setDate(payDate.getDate() + this.refundDeadline))
      const now = new Date()
      // 时间比较：若当前时间超过截止日期，说明退款已过期
      if (now > deadline) return '已过期'
      const diff = deadline - now
      //Math.floor 向下取整，避免出现小数
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      //计算剩余小时数：先取模得到不足一天的毫秒数，再除以一小时的毫秒数
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      return `${days}天${hours}小时`
    }
  },
  created() {
    this.loadOrderDetail()
  },
  methods: {
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
    },
    async handleSave() {
      try {
        // 添加表单验证
        if (!/^(北京市)?(朝阳区|海淀区|东城区|西城区|丰台区|石景山区|通州区|顺义区|昌平区|大兴区|房山区|门头沟区|平谷区|密云区|怀柔区|延庆区)[\s\S]*$/.test(this.form.address)) {
          this.$message.error('地址需以北京市下某区开头（如：朝阳区XXX路）');
          return;
        }
        if (!this.form.recipient.trim()) {
          this.$message.error('请输入收件人');
          return;
        }
        if (!/^1[3-9]\d{9}$/.test(this.form.tel)) {
          this.$message.error('手机号格式错误');
          return;
        }
        const { data } = await this.$axios.post(`${this.$httpUrl}/order/update`, {
          id: this.order.id,
          address: this.form.address,
          recipient: this.form.recipient,
          tel: this.form.tel
        });
        if(data.code === 200) {
          this.$message.success('修改成功');
          //刷新订单数据
          await this.loadOrderDetail();
        }
      } catch (error) {
        //显示具体错误信息
        const msg = error.response?.data?.msg || '修改失败';
        this.$message.error(msg);
      }
    },
    async loadOrderDetail() {
      const orderId = this.$route.params.oid
      try {
        const { data } = await this.$axios.get(`${this.$httpUrl}/order/detail/${orderId}`)
        if (data.code === 200) {
          this.order = data.data
          this.payTime = data.data.payTime || null
        }
      } catch (error) {
        this.$message.error('获取订单详情失败')
      }
    },

    async handleCancel() {
      this.$confirm('确定要取消此订单吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        this.loading = true
        try {
          const { data } = await this.$axios.post(`${this.$httpUrl}/order/cancel`, {
            oid: this.order.id
          })
          if (data.code === 200) {
            this.order.status = '已取消'
            this.$message.success('订单已取消')
          }
        } finally {
          this.loading = false
        }
      })
    },
    async handleDelete() {
      this.$confirm('确定要永久删除此订单吗？', '警告', {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }).then(async () => {
        this.loading = true;
        try {
          const { data } = await this.$axios.post(`${this.$httpUrl}/order/delete`, {
            oid: this.order.id
          });
          if (data.code === 200) {
            this.$message.success('订单已删除');
            this.$router.go(-1); // 返回订单列表
          }
        } catch (error) {
          const msg = error.response?.data?.msg || '删除失败';
          this.$message.error(msg);
        } finally {
          this.loading = false;
        }
      }).catch(() => {});
    },
    async handleRefund() {
      this.$confirm('确定要申请退款吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        this.loading = true
        try {
          // 模拟退款接口
          const { data } = await this.$axios.post(`${this.$httpUrl}/order/refund`, {
            oid: this.order.id
          })
          if (data.code === 200) {
            this.order.status = '已取消'
            this.$message.success('退款申请已提交')
          }
        } finally {
          this.loading = false
        }
      })
    },
    handlePay() {
      this.$router.push({
        path: '/pay',
        query: {
          oid: this.order.id,
          total: this.order.totalAmount
        }
      });
    },
  }
}
</script>

<style scoped>
.detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.book-cover {
  width: 60px;
  height: 80px;
}

.amount-summary {
  margin: 20px 0;
  text-align: right;
  padding: 10px;
  background: #f5f7fa;
}

.total-line {
  font-size: 16px;
  margin: 10px 0;
}

.order-info {
  padding: 0 20px;
}

.info-item {
  margin: 12px 0;
  font-size: 14px;
}

.info-item label {
  display: inline-block;
  width: 100px;
  color: #909399;
}

.refund-tip {
  color: #e6a23c;
  margin-left: 10px;
}

.action-buttons {
  margin-top: 30px;
  text-align: center;
}
.action-buttons .el-button {
  margin: 0 10px;
}
.el-form {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>