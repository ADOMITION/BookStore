<template>
  <div>
    <!-- 分类标题 -->
    <div class="category-header">
      <h2 class="category-title">
        {{ categoryName + '类图书' }}
        <span class="book-count">（共 {{ total }} 本）</span>
      </h2>
      <el-divider class="custom-divider"></el-divider>
    </div>
    <div class="result-grid">
      <el-row :gutter="30">
        <el-col
            v-for="item in beans"
            :key="item.id"
            :xs="12" :sm="8" :md="6" :lg="4"
            style="margin-bottom: 20px; padding: 0 15px"
        >
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
                <span class="price">¥{{ (item.price || 0).toFixed(2) }}</span>
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
    </div>
    <!-- 分页 -->
    <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next, jumper, sizes, total"
        :page-sizes="[12, 24, 48, 96]"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="display: flex; justify-content: center; margin-top: 20px;"
    />
  </div>
</template>

<script>
export default {
  name: "CategoryPage",
  data(){
    return{
      beans:[],
      total: 0,
      pageNum: 1,
      pageSize: 12,
      categoryName: '',
    }
  },
  created() {
    this.refresh();
  },
  watch: {
    '$route'() {
      this.refresh();
    }
  },
  methods:{
     async refresh() {
      let id = this.$route.query.id
      let url = this.$httpUrl + "/category/" + id +"/books";
      //获取分类信息
      await this.fetchCategoryName(id)
      try{
        const res = (await this.$axios.post(url,{
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          param: { cid: id }
        }));
        if(res.data.code === 200){
          this.beans = res.data?.data || [];
          this.total = res.data?.total || 0;
        }
      }catch (error){
        this.$message.error('出现错误');
        console.log(error)
       }
    },
    async fetchCategoryName(id) {
      try {
        const res = await this.$axios.get(this.$httpUrl+`/category/${id}`)
        if (res.data.code === 200) {
          this.categoryName = res.data.data.name
        }
      } catch (error) {
        console.error('获取分类名称失败:', error)
      }
    },
    handleSkip(id){
      this.$router.push({
        path: '/book',
        query:{
          id:id
        }
      })
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.refresh();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.refresh();
    },

  }
}
</script>

<style scoped>
.category-header {
  margin-bottom: 30px;
  text-align: center;
}

.category-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}
.rating-star {
  font-size: 14px;
  animation: twinkle 1.5s ease-in-out infinite;
}
.result-grid {
  margin: 20px 0;
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