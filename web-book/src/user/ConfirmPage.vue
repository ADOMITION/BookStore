<template>
  <div class="confirm-container">
    <!-- 头部导航 -->
    <el-steps :active="2" simple class="steps">
      <el-step title="首页" icon="el-icon-location-outline" ></el-step>
      <el-step title="确认订单" icon="el-icon-goods" style="margin-right: -20px"></el-step>
      <el-step title="支付" icon="el-icon-wallet" ></el-step>
      <el-step title="完成" icon="el-icon-success"></el-step>
    </el-steps>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 收货信息 -->
      <el-card class="shipping-card">
        <div slot="header" class="card-header">
          <i class="el-icon-location-outline"></i>
          <span>收货信息</span>
        </div>
        <el-form
            :model="form"
            :rules="rules"
            ref="formRef"
            label-width="100px"
            class="shipping-form"
        >
          <el-form-item label="收货地址" prop="address">
            <div class="address-selector">
              <el-select
                  v-model="form.district"
                  placeholder="请选择北京市辖区"
                  style="width: 150px; margin-right: 10px;"
                  @change="updateAddress"
              >
                <el-option
                    v-for="district in beijingDistricts"
                    :key="district.value"
                    :label="district.label"
                    :value="district.value"
                ></el-option>
              </el-select>
              <el-input
                  v-model="form.detailAddress"
                  placeholder="请输入详细地址（街道、门牌号等）"
                  clearable
                  style="width: calc(100% - 160px);"
                  @input="updateAddress"
              ></el-input>
            </div>
            <div class="full-address-preview" v-if="form.fullAddress">
              <i class="el-icon-info"></i> 完整地址: {{ form.fullAddress }}
            </div>
          </el-form-item>

          <el-form-item label="收件人" prop="recipient">
            <el-input
                v-model="form.recipient"
                placeholder="请输入收件人姓名"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="联系方式" prop="tel">
            <el-input
                v-model="form.tel"
                placeholder="请输入手机号码"
                clearable
            ></el-input>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 商品信息 -->
      <el-card class="goods-card">
        <div slot="header" class="card-header">
          <i class="el-icon-shopping-cart-2"></i>
          <span>商品信息</span>
        </div>
        <el-table
            :data="confirmDataList"
            border
            class="goods-table"
            v-if="confirmDataList"
            header-row-class-name="table-header"
        >
          <el-table-column label="商品信息" width="400">
            <template slot-scope="{row}">
              <div class="goods-info">
                <el-image
                    :src="row.book.url || require('@/assets/default-book.png')"
                    class="goods-image"
                ></el-image>
                <div class="goods-desc">
                  <div class="name">{{ row.book.name }}</div>
                  <div class="author">{{ row.book.author }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="原价" align="center">
            <template slot-scope="{row}">
              <span class="original-price">
                ¥{{ row.originalPrice.toFixed(2) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="折扣价" align="center">
            <template slot-scope="{row}">
              <span class="discount-price">
                ¥{{ row.discountPrice.toFixed(2) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column
              prop="orderItem.quantity"
              label="数量"
              align="center"
          />

          <el-table-column label="小计" align="center">
            <template slot-scope="{row}">
              ¥{{ (row.discountPrice * row.orderItem.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加加载状态提示 -->
        <div v-else class="loading-container">
          <i class="el-icon-loading"></i>
          正在加载订单信息...
        </div>
      </el-card>

      <!-- 金额汇总 -->
        <div class="total-line grand-total">
          <span>总额：</span>
          <span class="red-text">¥{{ total.toFixed(2) }}</span>
        </div>
        <el-button
            type="danger"
            class="submit-btn"
            @click="handleSubmit"
        >
          提交订单
        </el-button>
      </div>
    </div>
</template>

<script>
export default {
  data() {
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };

    return {
      beijingDistricts: [
        { value: '东城区', label: '东城区' },
        { value: '西城区', label: '西城区' },
        { value: '朝阳区', label: '朝阳区' },
        { value: '丰台区', label: '丰台区' },
        { value: '石景山区', label: '石景山区' },
        { value: '海淀区', label: '海淀区' },
        { value: '门头沟区', label: '门头沟区' },
        { value: '房山区', label: '房山区' },
        { value: '通州区', label: '通州区' },
        { value: '顺义区', label: '顺义区' },
        { value: '昌平区', label: '昌平区' },
        { value: '大兴区', label: '大兴区' },
        { value: '怀柔区', label: '怀柔区' },
        { value: '平谷区', label: '平谷区' },
        { value: '密云区', label: '密云区' },
        { value: '延庆区', label: '延庆区' }
      ],
      confirmDataList: [],
      form: {
        address: '',
        recipient: '',
        tel: '',
        district: '',
        detailAddress: '',
        fullAddress: '',
      },
      rules: {
        address: [
          { required: true, message: '请输入收货地址', trigger: 'blur' }
        ],
        recipient: [
          { required: true, message: '请输入收件人姓名', trigger: 'blur' }
        ],
        tel: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ]
      },
      total: 0
    };
  },
  created() {
    this.loadConfirmData();
  },
  methods: {
    updateAddress() {
      // 更新完整地址
      this.form.fullAddress = `北京市${this.form.district}${this.form.detailAddress}`;
      // 将完整地址提交到后端，可以在这里更新form.address
      this.form.address = this.form.fullAddress;
    },
    async loadConfirmData() {
      try {
        const type = this.$route.query.type;
        if (type === 'batch') {
          const oiids = JSON.parse(this.$route.query.oiids);
          const { data } = await this.$axios.post(this.$httpUrl + '/order/confirmBatch', {
            oiids: oiids
          });
          if (data.code === 200) {
            this.confirmDataList = data.data;
            this.total = data.data.reduce((sum, item) =>
                sum + (item.discountPrice * item.orderItem.quantity), 0);
          }
        } else {
          const oiid = this.$route.query.oiid;
          const {data} = await this.$axios.get(
              `${this.$httpUrl}/order/confirm?oiid=${oiid}`
          );
          if (data.code === 200) {
            console.log(data.data)
            this.confirmDataList = [data.data];
            this.total = data.data.discountPrice * data.data.orderItem.quantity;
          }
        }
      } catch (error) {
        this.$message.error('获取订单信息失败');
      }
    },

    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          try {
            const { data } = await this.$axios.post(
                `${this.$httpUrl}/order/create`,
                {
                  oiids: this.confirmDataList.map(item => item.orderItem.id),
                  ...this.form
                }
            );
            if (data.code === 200) {
              this.$router.push({
                path: '/pay',
                query: {
                  oid: data.data.oid,
                  total: data.data.total
                }
              });
            }
          } catch (error) {
            this.$message.error('提交订单失败');
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.confirm-container {
  padding: 20px 200px;
  background: #f5f5f5;
}

.steps {
  margin-bottom: 30px;
}

.main-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.card-header {
  font-size: 18px;
  color: #333;
}

.card-header i {
  margin-right: 8px;
  color: #409EFF;
}

.shipping-form {
  padding: 0 50px;
}

.goods-info {
  display: flex;
  align-items: center;
}

.goods-image {
  width: 60px;
  height: 80px;
  margin-right: 15px;
}

.goods-desc .name {
  font-weight: bold;
  margin-bottom: 5px;
}

.goods-desc .author {
  color: #666;
  font-size: 12px;
}

.original-price {
  text-decoration: line-through;
  color: #999;
}

.discount-price {
  color: #f56c6c;
  font-weight: bold;
}



.total-line {
  margin: 10px 0;
  font-size: 14px;
}

.grand-total {
  font-size: 18px;
  margin-top: 20px;
}

.red-text {
  color: #f56c6c;
  font-weight: bold;
  font-size: 24px;
}

.submit-btn {
  margin-top: 20px;
  width: 200px;
  height: 50px;
  font-size: 18px;
}
.address-selector {
  display: flex;
  align-items: center;
}

.full-address-preview {
  margin-top: 5px;
  color: #606266;
  font-size: 12px;
}

.full-address-preview i {
  margin-right: 5px;
}
</style>