<template>
  <div class="home-container">
    <div class="welcome-card">
      <h1 class="welcome-title">{{ '欢迎回来，' + user.name + '！' }}</h1>
    </div>

    <el-card class="profile-card">
      <el-descriptions
          title="个人中心"
          :column="2"
          border
          class="custom-descriptions"
      >
        <el-descriptions-item>
          <template slot="label" class="desc-label">
            <i class="el-icon-s-custom label-icon"></i>
            <span class="label-text">账号</span>
          </template>
          <span class="desc-content">{{ user.no }}</span>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-message label-icon"></i>
            <span class="label-text">电子邮件</span>
          </template>
          <span class="desc-content highlight">{{ user.email }}</span>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone label-icon"></i>
            <span class="label-text">联系电话</span>
          </template>
          <span class="desc-content">{{ user.phone || '--' }}</span>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline label-icon"></i>
            <span class="label-text">联系地址</span>
          </template>
          <span class="desc-content">{{ user.company || '--' }}</span>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-s-opportunity label-icon"></i>
            <span class="label-text">系统角色</span>
          </template>
          <el-tag
              :class="user.roleId === 0 ? 'admin-tag' : 'user-tag'"
              effect="dark"
          >
            {{ user.roleId === 0 ? "管理员" : "普通用户" }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div class="date-section">
        <DateUtils class="custom-date-utils" />
      </div>
    </el-card>
  </div>
</template>

<script>
import DateUtils from "./DateUtils.vue";
export default {
  name: "Home",
  components: {DateUtils},
  data() {

    return {
      user:{}
    }
  },
  methods:{
    init(){
      this.user = JSON.parse(sessionStorage.getItem('CurUser')) || {}
    }
  },
  created(){
    this.init()
  }
}
</script>

<style scoped>
.home-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e6eff9 100%);
  height: calc(100vh - 80px);
  padding: 40px 24px;
  overflow: hidden;
  box-sizing: border-box;
}

.welcome-card {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-title {
  font-size: 2.5rem;
  color: #2d3a4b;
  margin: 0;
  font-weight: 600;
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
  padding-bottom: 15px;
}

.profile-card {
  max-width: 1000px;
  margin: 0 auto;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(45, 58, 75, 0.1);
  border: none;
}

::v-deep .custom-descriptions {
  padding: 24px;
}

::v-deep .custom-descriptions .el-descriptions__header {
  margin-bottom: 32px;
}

::v-deep .custom-descriptions .el-descriptions__title {
  font-size: 20px;
  color: #2d3a4b;
  font-weight: 600;
}

.label-icon {
  font-size: 18px;
  color: #409EFF;
  margin-right: 8px;
}

.label-text {
  font-weight: 500;
  color: #606266;
}

.desc-content {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.highlight {
  color: #409EFF;
  font-weight: 600;
}

.admin-tag {
  background: linear-gradient(45deg, #409EFF, #2d3a4b);
  border: none;
  letter-spacing: 1px;
}

.date-section {
  margin-top: 40px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 8px;
}

</style>