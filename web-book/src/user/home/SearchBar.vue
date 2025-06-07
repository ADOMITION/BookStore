<template>
  <div class="area">
    <el-row style="margin-left: 5px">
      <el-col :span="24">
        <el-form :inline="true" @submit.native.prevent="handleSearch">
          <el-form-item>
            <img src="@/assets/logo.png" width="40px" height="40px" alt="logo"/>
          </el-form-item>
          <el-form-item>
            <el-input
                v-model="keyword"
                placeholder="请输入书名/作者/出版社"
                size="large"
                clearable
                @keyup.enter.native="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button
                type="primary"
                @click="handleSearch"
                icon="el-icon-search"
            >搜索</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  data() {
    return {
      keyword: ''
    }
  },
  methods: {
    handleSearch() {
      if (!this.keyword.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }

      this.$router.push({
        path: '/search',
        query: {
          keyword: this.keyword.trim(),
          pageNum: 1, // 重置到第一页
          pageSize: this.$route.query.pageSize || 20 // 保持原分页大小
        }
      })
      this.keyword = ''
    }
  }
}
</script>

<style scoped>
.area{
  background: linear-gradient(to bottom, #b6d0dc 0%, #ffffff 100%);
}
</style>

