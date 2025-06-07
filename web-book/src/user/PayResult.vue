<template>
  <div class="result-page">
    <div class="result-box">
      <i class="el-icon-success success-icon" v-if="success"></i>
      <i class="el-icon-error error-icon" v-else></i>
      <h3>{{ resultTitle }}</h3>
      <div class="result-info">
        <p>支付金额：¥{{ (actualTotal || 0).toFixed(2) }}</p>
        <p v-if="success">支付时间：{{ new Date().toLocaleString() }}</p>
      </div>

      <div class="actions">
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        <el-button @click="$router.push(`/order`)">
          查看订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    actualTotal() {
      const total = parseFloat(this.$route.query.total);
      return isNaN(total) ? 0 : total;
    },
    success() {
      return this.$route.query.success === 'true'
    },
    resultTitle() {
      return this.success ? '支付成功!!!' : '支付失败'
    }
  }
}
</script>

<style scoped>
.result-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-box {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  width: 500px;
}

.success-icon {
  color: #67c23a;
  font-size: 80px;
}

.error-icon {
  color: #f56c6c;
  font-size: 80px;
}

h3 {
  margin: 20px 0;
  font-size: 24px;
}

.result-info {
  margin: 20px 0;
  color: #666;
}

.actions {
  margin-top: 30px;
}

.el-button {
  margin: 0 10px;
}
</style>