<template>
  <div class="cart-container">
    <el-card class="cart-card">
      <!-- 购物车头部 -->
      <div slot="header" class="cart-header">
        <h2><i class="el-icon-shopping-cart-full"></i> 我的购物车</h2>
        <el-button
            type="danger"
            :disabled="selectedItems.length === 0"
            @click="clearCart"
        >
          清空购物车
        </el-button>
      </div>

      <!-- 购物车列表 -->
      <el-table
          :data="cartItems"
          stripe
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column label="商品信息" width="400">
          <template slot-scope="{row}">
            <div class="product-info">
              <el-image
                  :src="row.book.url || require('@/assets/default-book.png')"
                  class="product-image"
              ></el-image>
              <div class="product-detail">
                <div class="title">{{ row.book.name }}</div>
                <div class="author">{{ row.book.author }}</div>
                <div class="press">{{ row.book.press }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="120">
          <template slot-scope="{row}">
            <div class="price-group">
              <span class="original-price">¥{{ row.book.price.toFixed(2) }}</span>
              <span class="discount-price">¥{{ calculateDiscountPrice(row.book).toFixed(2) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="数量" width="150">
          <template slot-scope="{row}">
            <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.book.stock"
                @change="updateQuantity(row)"
            ></el-input-number>
          </template>
        </el-table-column>

        <el-table-column label="小计" width="120">
          <template slot-scope="{row}">
            ¥{{ (calculateDiscountPrice(row.book) * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="80">
          <template slot-scope="{row}">
            <el-button
                type="text"
                class="delete-btn"
                @click="deleteItem(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 结算栏 -->
      <div class="checkout-bar">
        <div class="total">
          已选 {{ selectedItems.length }} 件商品，总金额：
          <div class="price-summary">
            <div>商品总价：¥{{ originalTotal.toFixed(2) }}</div>
            <div>折扣优惠：-¥{{ discountAmount.toFixed(2) }}</div>
            <div class="grand-total">应付总额：¥{{ totalPrice.toFixed(2) }}</div>
          </div>
        </div>
        <el-button
            type="danger"
            size="large"
            :disabled="selectedItems.length === 0"
            @click="checkout"
        >
          去结算
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      cartItems: [],
      selectedItems: []
    };
  },

  computed: {
    // 计算原始总价
    originalTotal() {
      return this.selectedItems.reduce((sum, item) => {
        return sum + (item.book.price * item.quantity)
      }, 0)
    },
    // 计算折扣总价
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => {
        return sum + (this.calculateDiscountPrice(item.book) * item.quantity)
      }, 0)
    },
    // 计算总优惠金额
    discountAmount() {
      return this.originalTotal - this.totalPrice
    }
  },

  created() {
    this.loadCart();
  },

  methods: {
    // 折扣计算逻辑（与详情页保持一致）
    calculateDiscountPrice(book) {
      if (book.stock > 25) return book.price * 0.7
      if (book.stock >= 20) return book.price * 0.8
      if (book.stock >= 15) return book.price * 0.9
      return book.price
    },
    // 折扣标签显示
    calculateDiscountTag(stock) {
      if (stock > 25) return '七折'
      if (stock >= 20) return '八折'
      if (stock >= 15) return '九折'
      return ''
    },

    async loadCart() {
      try {
        const { data } = await this.$axios.get(this.$httpUrl + "/cart/list");
        if (data.code === 200) {
          this.cartItems = data.data.map(item => ({
            ...item,
            quantity: item.quantity
          }));
        }
      } catch (error) {
        this.$message.error("获取购物车失败");
      }
    },

    async updateQuantity(item) {
      try {
        await this.$axios.post(this.$httpUrl + "/cart/update", {
          bookId: item.book.id,
          quantity: item.quantity
        });
      } catch (error) {
        this.$message.error("更新数量失败");
      }
    },

    async deleteItem(item) {
      try {
        await this.$axios.post(this.$httpUrl + "/cart/delete", {
          bookId: item.book.id
        });
        this.loadCart();
      } catch (error) {
        this.$message.error("删除失败");
      }
    },

    handleSelectionChange(selection) {
      this.selectedItems = selection;
    },

    async clearCart() {
      try {
        await this.$axios.post(this.$httpUrl + "/cart/clear");
        this.loadCart();
        this.$message.success("购物车已清空");
      } catch (error) {
        this.$message.error("清空失败");
      }
    },

    checkout() {
      if (!this.check()) return;
      const selectedItems = this.selectedItems.map(item => ({
        pid: item.book.id,
        num: item.quantity
      }));
      this.$axios.post(this.$httpUrl + '/order/batchBuy', selectedItems)
          .then(res => {
            if (res.data.code === 200) {
              this.$router.push({
                path: '/confirm',
                query: {
                  oiids: JSON.stringify(res.data.data),
                  type: 'batch'
                }
              });
            }
          })
          .catch(err => {
            this.$message.error(err.response?.data?.msg || '结算失败');
          });
    },
    check() {
      if (!sessionStorage.getItem('CurUser')) {
        this.$message.warning("您还未登录哦，请先登录");
        return false
      }
      return true
    }
  }
};
</script>

<style scoped>
.cart-container {
  padding: 30px 15%;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 100px;
  margin-right: 20px;
}

.product-detail .title {
  font-weight: bold;
  margin-bottom: 5px;
}

.product-detail .author,
.product-detail .press {
  color: #666;
  font-size: 12px;
}

.checkout-bar {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
}

.delete-btn {
  color: #f56c6c;
}
.price-group {
  display: flex;
  flex-direction: column;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.discount-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.discount-tag {
  background: linear-gradient(45deg, #ff6b6b, #ff4444);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-top: 5px;
  display: inline-block;
}

.price-summary {
  text-align: right;
  margin-top: 10px;
}

.grand-total {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-top: 10px;
}
</style>