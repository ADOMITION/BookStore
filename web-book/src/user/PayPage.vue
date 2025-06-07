<template>
  <div class="pay-container">
    <el-steps :active="3" simple class="steps">
      <el-step title="首页" icon="el-icon-location-outline" ></el-step>
      <el-step title="确认订单" icon="el-icon-goods" style="margin-right: -20px"></el-step>
      <el-step title="支付" icon="el-icon-wallet" ></el-step>
      <el-step title="完成" icon="el-icon-success"></el-step>
    </el-steps>


    <el-card class="pay-card">
      <div class="order-info">
        <h3>请为商品支付：</h3>
        <p class="total">支付金额：¥{{ order.total.toFixed(2) }}</p>
      </div>

      <div class="payment-methods">
        <el-radio-group v-model="paymentType">
          <el-radio-button label="alipay">
            <img src="@/assets/alipay.png" class="pay-icon" alt="支付宝">
            <span class="pay-text">支付宝</span>
          </el-radio-button>
          <el-radio-button label="wechat">
            <img src="@/assets/wechat-pay.png" class="pay-icon" alt="微信支付">
            <span class="pay-text">微信支付</span>
          </el-radio-button>
        </el-radio-group>
      </div>

      <el-button
          type="success"
          class="pay-btn"
          @click="handlePay"
          :disabled="!paymentType"
          :loading="loading">
        立即支付
      </el-button>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      order: {
        oid: this.$route.query.oid,
        total: parseFloat(this.$route.query.total)
      },
      paymentType: '',
      loading: false
    }
  },
  created() {
    this.order.oid = this.$route.query.oid;
    this.order.total = parseFloat(this.$route.query.total);
  },
  methods: {
    async handlePay() {
      this.loading = true
      try {
        const { data } = await this.$axios.post(this.$httpUrl+'/order/pay', {
          oid: this.order.oid,
        })

        if (data.code === 200) {
          this.$router.push({
            path: '/pay-result',
            query: {
              success: true,
              oid: this.order.oid,
              total: this.order.total
            }
          })
        }
      } catch (error) {
        // 新增库存不足的提示
        if(error.response && error.response.data.msg.includes("库存不足")) {
          this.$message.error('部分商品库存不足，支付失败');
        } else {
          this.$message.error('支付失败');
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.pay-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
}

.steps {
  margin-bottom: 30px;
}

.pay-card {
  padding: 20px;
}

.order-info {
  text-align: center;
  margin-bottom: 30px;
}

.order-info h3 {
  color: #666;
  margin-bottom: 10px;
}

.total {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.payment-methods {
  margin: 30px 0;
  text-align: center;
}

.pay-icon {
  width: 100px;
  vertical-align: middle;
  margin-right: 10px;
}

.pay-btn {
  width: 200px;
  display: block;
  margin: 0 auto;
  height: 45px;
  font-size: 16px;
}

.el-radio__original {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0,0,0,0) !important;
  white-space: nowrap !important;
  border: 0 !important;
}
.el-radio__original:focus {
  outline: none !important;
  box-shadow: none !important;
}
</style>