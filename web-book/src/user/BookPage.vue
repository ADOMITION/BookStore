<template>
  <div class="book-detail-container">
    <el-row :gutter="40" class="book-info-row">
      <!-- 封面图片 -->
      <el-col :span="6" :offset="6">
        <!-- 图片加载错误我们添加默认图片 -->
        <el-image
            :src="bean.url || require('@/assets/default-book.png')"
            style="height: 368px;width: 260px"
            fit="contain"
            class="book-cover"
        >
          <div slot="error" class="cover-error">
            <i class="el-icon-picture-outline"></i>
          </div>
        </el-image>
      </el-col>

      <!-- 书籍信息 -->
      <el-col :span="6" class="book-meta">
        <h2 class="book-title">{{ bean.name }}</h2>
        <p class="book-author">{{ bean.author }}</p>
        <p class=publish-info>出版社：{{ bean.press }}</p>
        <p class=publish-date>出版日期：{{ bean.date }}</p>
        <div class="price-section">
          <span v-if="bean.discountPrice!== bean.price" class="original-price">¥{{ bean.price }}</span>
          <span class="current-price">¥{{ bean.discountPrice }}</span>
          <el-tag v-if="bean.discountPrice!== bean.price" type="success" class="discount-tag">
            {{ getDiscountText }}
          </el-tag>
          <el-tag v-if="bean.status === 0" type="danger" class="status-tag">已下架</el-tag>
        </div>

        <div class="stock-section">
          <el-input-number
              v-model="num"
              :min="1"
              :max="bean.stock"
              :disabled="bean.stock <= 0 || bean.status === 0"
          />
          <span
              class="stock-text"
              :class="{ 'no-stock': bean.stock <= 0 || bean.status === 0 }"
          >
            {{ getStockText }}
          </span>
        </div>

        <div class="action-buttons">
          <!-- 正常状态按钮 -->
          <template v-if="bean.stock > 0 && bean.status === 1">
            <el-button
                type="warning"
                @click="handleAddCart"
            >
              加入购物车
            </el-button>
            <el-button
                type="danger"
                @click="handleBuyNow"
            >
              立即购买
            </el-button>
          </template>
          <!-- 无库存状态按钮 -->
          <el-button
              v-if="bean.stock === 0 && bean.status === 1"
              type="warning"
              @click="handleReserve"
          >
            立即预定
          </el-button>
          <!-- 商品下架状态提示 -->
          <el-alert
              v-if="bean.status === 0"
              title="该商品已下架"
              type="warning"
              :closable="false"
              show-icon
              class="status-alert"
          ></el-alert>
        </div>
      </el-col>
    </el-row>

    <!-- 书籍描述 -->
    <el-row class="book-description">
      <el-col :span="18" :offset="3">
        <el-card>
          <h3>书籍简介</h3>
          <p class="description-text">{{ bean.info }}</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 评论区域 -->
    <el-row class="review-section">
      <el-col :span="18" :offset="3">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="reviews">
            <div slot="title" class="review-title">
              书友评价（{{ bean.reviewCount || 0 }}）
            </div>

            <div v-if="review.length === 0" class="no-reviews">
              还没有评论，快来抢沙发吧~
            </div>

            <div v-else class="review-list">
              <el-card v-for="item in review" :key="item.id" class="review-item">
                <el-tag>{{ item.userName }}</el-tag>
                <p>{{ item.content }}</p>
                <el-tag type="info" style="text-align: right">
                  {{ item.createtime | formatDate }}
                </el-tag>
              </el-card>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { formatDate } from '@/store/formatDate'

export default {
  filters: {
    formatDate(time) {
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm')
    }
  },
  data() {
    return {
      bean: {
        status: 1,
        stock: 0,
        price: 0,
        press: '',
        date: '',
        info: ''
      },
      review: [],
      num: 1,
      activeNames: ['reviews']
    }
  },

  computed: {
    getStockText() {
      if (this.bean.status === 0) return '商品已下架'
      return this.bean.stock > 0 ? `库存 ${this.bean.stock} 件` : '暂时缺货'
    },
    getDiscountText() {
      if (this.bean.stock >= 20 && this.bean.stock <= 25) {
        return '八折优惠'
      } else if (this.bean.stock >= 15 && this.bean.stock < 20) {
        return '九折优惠'
      }else if(this.bean.stock > 25){
        return '七折优惠'
      }
      return ''
    }
  },

  created() {
    window.scrollTo(0, 0);
    this.loadBookDetail()
  },

  methods: {
    async loadBookDetail() {
      try {
        const { data } = await this.$axios.get(this.$httpUrl + `/book/${this.$route.query.id}`)
        if (data.code === 200) {
          this.bean = {
            // 初始化默认值
            url: require('@/assets/default-book.png'),
            status: 1,
            stock: 0,
            price: 0,
            press: '',
            date: '',
            info: '',
            ...data.data.product
          }
          //状态为审核通过的书评
          this.review = data.data.reviews.filter(item => item.status === 1) || []
          this.calculateDiscount()
        }
      } catch (error) {
        this.$message.error('获取书籍详情失败')
      }
    },
    calculateDiscount() {
      if (this.bean.stock >= 20 && this.bean.stock <= 25) {
        this.bean.discountPrice = (this.bean.price * 0.8).toFixed(2)
      } else if (this.bean.stock >= 15 && this.bean.stock < 20) {
        this.bean.discountPrice = (this.bean.price * 0.9).toFixed(2)
      } else if (this.bean.stock > 25) {
        this.bean.discountPrice = (this.bean.price * 0.7).toFixed(2)
      }else {
        this.bean.discountPrice = this.bean.price
      }
    },

    async handleReserve() {
      if (!this.checkLogin()) return;

      try {
        const res = await this.$axios.post(this.$httpUrl + '/reservation/create', {
          userId: JSON.parse(sessionStorage.getItem('CurUser')).id,
          bookId: this.bean.id,
          quantity: 1
        });

        if (res.data.code === 200) {
          this.$message.success('预定成功');
          this.$router.push('/myreservation');
        }
      } catch (error) {
        this.$message.error(error.response?.data?.msg || '预定失败');
      }
    },

    handleBuyNow() {
      if (!this.checkLogin()) return
      // 购买逻辑
      let pid = this.bean.id
      let url = `/order/buy?pid=${pid}&num=${this.num}`
      this.$axios.post(this.$httpUrl + url).then(response => {
        const oiid = response.data.data
        /*console.log(oiid)*/
        if (typeof oiid === 'number') {
          this.$router.push({
            path: '/confirm',
            query: {
              oiid: oiid,
            }
          })
        }
      })
    },

    handleAddCart() {
      if (!this.checkLogin()) return
      this.$axios.post(this.$httpUrl + '/cart/add', {
        bookId: this.bean.id,
        quantity: this.num
      }).then(response => {
        if (response.data.code === 200) {
          this.$message.success('已加入购物车')
        }
      }).catch(error => {
        this.$message.error('添加失败：' + (error.response?.data?.msg || '服务器错误'))
      })
    },

    checkLogin() {
      if (!sessionStorage.getItem('CurUser')) {
        this.$router.push('/login')
        return false
      }
      return true
    }
  }
}
</script>

<style scoped>
.book-detail-container {
  padding: 40px 0;
  background-color: #f9f9f9;
}

.book-cover {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  background: #fff;
  padding: 8px;
}
.book-cover:hover {
  transform: scale(1.05);
}
/*主题*/
.book-title {
  font-size: 32px;
  color: #2b2f36;
  margin-bottom: 12px;
  font-weight: 700;
  letter-spacing: -0.5px;
  position: relative;
  display: inline-block;
}
.book-title::after {
  content: "";
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 60px;
  height: 3px;
  background: #ff6b6b;
  border-radius: 2px;
}
/*作者*/
.book-author {
  color: #6c757d;
  font-size: 18px;
  margin-bottom: 24px;
  position: relative;
  padding-left: 24px;
  font-weight: bold;
}
.book-author::before {
  content: "🐼";
  position: absolute;
  left: 0;
  top: 1px;
  color: #adb5bd;
}
/*价格*/
.price-section {
  margin: 15px 0;
  padding: 20px;
  background: rgba(255, 107, 107, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 107, 107, 0.15);
}
.original-price {
  font-size: 16px;
  color: #adb5bd;
  text-decoration: line-through;
  margin-right: 12px;
}
.current-price {
  font-size: 40px;
  color: #ff4444;
  font-weight: 800;
  text-shadow: 0 2px 4px rgba(255, 68, 67, 0.15);
  position: relative;
  margin-right: 16px;
}
.current-price::after {
  content: "限时优惠";
  position: absolute;
  right: -86px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #fff;
  background: #ff6b6b;
  padding: 2px 8px;
  border-radius: 4px;
}
.discount-tag {
  transform: rotate(-5deg);
  font-weight: bold;
  background: linear-gradient(45deg, #ff9f4a, #ff6b6b);
  color: white;
  border: none;
  padding: 4px 10px;
}
/*下架*/
.status-tag {
  background: linear-gradient(45deg, #ff8787, #ff6b6b);
  color: white;
  border: none;
}
/*库存*/
.stock-section {
  margin: 28px 0;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.stock-text {
  font-size: 14px;
  color: #495057;
  margin-left: 12px;
  padding: 6px 12px;
  background: #f8f9fa;
  border-radius: 4px;
}
/*没有库存*/
.no-stock {
  color: #999;
}

/*按钮*/
.action-buttons {
  margin: 30px 0;
  display: flex;
}
.el-button--warning {
  width: 160px;
  height: 40px;
  background: linear-gradient(135deg, #ffb347, #ff9966);
  box-shadow: 0 4px 6px rgba(255, 140, 0, 0.2);
}
.el-button--danger {
  width: 160px;
  height: 40px;
  background: linear-gradient(135deg, #ff6b6b, #ff4444);
  box-shadow: 0 4px 6px rgba(255, 68, 67, 0.2);
}

.el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}


.publish-info {
  margin-top: -15px;
  color: #666;
  line-height: 1.8;
  font-size: 16px;
}
.publish-date{
  color: #666;
  line-height: 1.8;
  font-size: 16px;
}

/*简介*/
.book-description h3 {
  font-size: 20px;
  color: #2b2f36;
  border-left: 4px solid #ff6b6b;
  padding-left: 12px;
  margin-bottom: 16px;
}
.description-text {
  line-height: 1.8;
  text-indent: 2em;
  font-size: 16px;
  color: #555;
}

/*评论*/
.review-title{
  color: #050505;
  font-size: 18px;
  font-weight: bold;
  position: relative;
  padding-left: 24px;
}
.review-title::before {
  content: "🕍";
  position: absolute;
  left: 0;
  top: 1px;
  color: #adb5bd;
}
.review-section {
  margin-top: 20px;
}
.review-item {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}
.review-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}
.no-reviews {
  color: #999;
  text-align: center;
  padding: 20px;
  font-size: 16px;
}
.status-alert {
  width: 100%;
  margin-top: 10px;
}
</style>