<template>
  <header class="app-header">
    <div class="header-left">
      <i :class="['collapse-icon', icon]" @click="collapse"></i>
    </div>

    <h1 class="header-title">文华书店后台管理系统</h1>

    <div class="header-user">
      <el-dropdown trigger="click" placement="bottom">
        <div class="user-container">
          <el-avatar
              :size="40"
              class="user-avatar"
              :src="avatarUrl"
              :key="avatarUrl"
          >
            <img v-if="!avatarUrl" src="@/assets/default-avatar.png" />
          </el-avatar>
          <div class="user-info">
            <span class="user-name">{{ user.name }}</span>
            <span class="user-role">管理员</span>
          </div>
          <i class="el-icon-arrow-down header-arrow"></i>
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <el-dropdown-item
              icon="el-icon-switch-button"
              class="dropdown-item"
              @click.native="logout"
          >
            <span class="dropdown-text">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </header>
</template>
<script>
export default {
  name: "Header",
  data(){
    return{
      //转换
      user : JSON.parse(sessionStorage.getItem('CurUser')),
      avatarUrl: JSON.parse(sessionStorage.getItem('CurUser'))?.avatar || ''
    }
  },
  props:{//接收图标
    icon:String
  },
  methods:{
    logout(){
      console.log("logout")
      //跳转到登录
      this.$confirm('您确定要退出登录吗？', '提示', {
        confirmButtonText: '确 定',  //确认按钮的文字显示
        type: 'warning',
        center: true, //文字居中显示
        closeOnClickModal: false, //是否可以点击空白处关闭弹窗
      })
          .then(() => {
            this.$message({
              type:'success',
              message:'退出登录成功'
            })
            window.location.reload()
            this.$router.push("/login")

            //清空数据
            sessionStorage.clear()
          })
          .catch(() => {
            this.$message({
              type:'info',
              message:'已取消退出登录'
            })
          });
    },
    collapse(){
      this.$emit('doCollapse')
    }
  },
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(45, 58, 75, 0.08);
  position: relative;
  z-index: 100;
}

.header-left {
  flex: 0 0 60px;
}

.collapse-icon {
  font-size: 24px;
  color: #2d3a4b;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px;
  border-radius: 6px;
}

.collapse-icon:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
  transform: rotate(90deg);
}

.header-title {
  flex: 1;
  margin: 0;
  font-size: 30px;
  font-weight: 600;
  color: #2d3a4b;
  letter-spacing: 0.5px;
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, #409EFF 0%, #000000 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-container {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-container:hover {
  background: rgba(64, 158, 255, 0.05);
}

.user-avatar {
  border: 2px solid #e4e7ed;
  transition: all 0.3s ease;
}

.user-container:hover .user-avatar {
  border-color: #409EFF;
}

.user-info {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #2d3a4b;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.header-arrow {
  color: #c0c4cc;
  transition: all 0.3s ease;
}

.user-container:hover .header-arrow {
  color: #409EFF;
  transform: translateY(2px);
}

/* 下拉菜单样式 */
.user-dropdown {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(45, 58, 75, 0.12);
  border: none;
}

.user-dropdown::before {
  content: '';
  position: absolute;
  top: -5px;
  right: 20px;
  width: 10px;
  height: 10px;
  background: #fff;
  transform: rotate(45deg);
  box-shadow: -2px -2px 5px rgba(0, 0, 0, 0.05);
}

.dropdown-item {
  padding: 12px 20px;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background: #f5f7fa !important;
}

.dropdown-text {
  margin-left: 8px;
  color: #606266;
}

</style>