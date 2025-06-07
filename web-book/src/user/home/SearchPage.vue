<template>
  <div>
    <div v-loading="loading">
      <!--未找到结果-->
      <el-alert
          v-if="!loading && beans.length === 0  "
          title="没有相关结果哦，换个关键字如何？"
          type="info"
          :closable="false"
      />

      <!--找到结果-->
      <div v-else>
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
      </div>
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
  </div>
</template>

<script>
export default {
  name: "SearchPage",
  data() {
    return {
      loading : true,
      beans: [],
      total: 0,
      pageNum: 1,
      pageSize: 12,
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
  methods: {
    async refresh() {
      this.loading = true;
      try {
        const res = (await this.$axios.post(this.$httpUrl + '/book/search', {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          param: {
            keyword: this.$route.query.keyword
          }
        }));
        if (res.data.code === 200) {
          this.beans = res.data?.data || [];
          this.total = res.data?.total || 0;
          console.log(res)
          console.log(res.data.data)
        }
      } catch (error) {
        this.$message.error('搜索失败，请稍后重试');
      }finally {
        this.loading =false
      }
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.refresh();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.refresh();
    },

    handleSkip(id) {
      this.$router.push({
        path: '/book',
        query: { id }
      });
    }
  }
}
</script>

<style scoped>
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