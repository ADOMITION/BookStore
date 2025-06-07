<template>
  <div>
    <el-row
        v-for="row in processedBooks"
        :key="row.id"
        style="text-align: center;margin: 0 15%"
        :gutter="30">

      <el-col>
        <h3>{{row.name}}
          <el-button
              type="info"
              icon="el-icon-arrow-right"
              size="mini"
              @click="handleMore(row.id)"/>
        </h3>
      </el-col>

      <el-col
          v-for="item in row.books"
          :key="item.id"
          :span="6"
          style="margin-bottom: 20px; padding: 0 15px">

        <el-card
            :body-style="{padding:'0px'}"
            style="width: 195px; cursor: pointer;"
            shadow="hover"
            @click.native="handleSkip(item.id)">

          <el-image
              :src="item.image"
              style="height: 276px;width: 195px"
              fit="cover">
            <div slot="error" class="image-placeholder">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>

          <div style="padding: 10px; margin-top: -10px">
            <p class="book-title">{{ item.name }}</p>
            <p class="book-author">{{ item.author }}</p>
            <p class="book-press">{{ item.press }}</p>

            <!-- 评分 -->
            <div class="price-rating">
              <span class="price">¥{{ item.price }}</span>
              <div class="rating-box">
                <span> 评分：</span>
                <span class="rating-star">⭐</span>
                {{ item.mark || '暂无评分' }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 空状态提示 -->
    <el-empty
        v-if="!loading && processedBooks.length === 0"
        description="暂无图书数据"
        style="margin-top: 50px"></el-empty>
  </div>
</template>

<script>
export default {
  name: "NavBar",
  data() {
    return {
      bean: [],
      loading: true
    };
  },
  computed: {
    // 添加数据清洗逻辑
    processedBooks() {
      return this.bean.map(category => ({
        ...category,
        books: (category.books || []).map(book => ({
          ...book,
          // 确保评分格式正确
          mark: book.mark ? book.mark.toFixed(1) : '0.0'}))
            .filter(book => book.id)})) // 过滤无效数据
          .filter(category => category.books.length > 0); // 过滤空分类
    }
  },

  created() {
    this.fetchData();
  },

  methods: {
    async fetchData() {
      try {
        const res = await this.$axios.get(this.$httpUrl+'/book/preview');
        console.log(res)
        if (res.data.code === 200) {
          this.bean = res.data.data;
        }
      } catch (error) {
        console.error('数据加载失败:', error);
      }
    },
    handleMore(id){
      this.$router.push({
        path: '/category',
        query:{
          id:id
        }
      })
    },
    handleSkip(id){
      this.$router.push({
        path: '/book',
        query:{
          id:id
        }
      })
    }
  }
}
</script>

<style scoped>
.rating-star {
  font-size: 14px;
  animation: twinkle 1.5s ease-in-out infinite;
}
.price-rating {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.rating-box {
  background: #aea49a;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 12px;
  color: #ffffff;
}

.book-title {
  font-size: 14px;
  font-weight: bold;
  margin: 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  color: #909399;
  font-size: 12px;
  margin: 5px 0;
}

.book-press {
  color: #909399;
  font-size: 12px;
  margin: 5px 0;
}

.price {
  color: #ff4443;
  font-size: 20px;
  font-weight: bold;
}

.image-placeholder {
  height: 276px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
</style>