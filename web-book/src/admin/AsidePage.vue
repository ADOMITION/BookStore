<template>
  <el-menu
      :default-active="$route.path"
      class="el-menu-vertical-custom"
      :collapse="isCollapse"
      :collapse-transition="false"
      router
      background-color="#2d3a4b"
      text-color="#b0bac5"
      active-text-color="#409EFF"
  >
    <div class="logo-container">
      <transition name="fade">
        <span v-if="!isCollapse" class="logo-text">Admin System</span>
        <i v-else class="el-icon-s-platform logo-icon"></i>
      </transition>
    </div>

    <el-menu-item
        v-for="item in menuItems"
        :key="item.index"
        :index="item.index"
        :class="{'menu-active': $route.path === item.index}"
    >
      <i :class="item.icon"></i>
      <span slot="title">{{ item.title }}</span>
      <div class="hover-effect"></div>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: "AsidePage",
  computed:{
  },
  props:{
    isCollapse:Boolean
  },
  data(){
    return{
      menuItems: [
        { index: "/admin/home", icon: "el-icon-s-home", title: "首页" },
        { index: "/admin/ad", icon: "el-icon-s-custom", title: "管理员管理" },
        { index: "/admin/user", icon: "el-icon-user", title: "用户管理" },
        { index: "/admin/book", icon: "el-icon-notebook-2", title: "书籍管理" },
        {index: "/admin/category", icon: "el-icon-menu", title: "分类管理"},
        { index: "/admin/order", icon: "el-icon-tickets", title: "订单管理" },
        { index: "/admin/review", icon: "el-icon-chat-line-round", title: "评论管理" },
        { index: "/admin/announcement", icon: "el-icon-s-promotion", title: "公告管理" },
        { index: "/admin/guestbook", icon: "el-icon-chat-dot-round", title: "留言管理" },
        { index: "/admin/record", icon: "el-icon-refresh", title: "出入库记录" },
      ]
    }
  }
}
</script>

<style scoped>
.el-menu-vertical-custom {
  height: 108.3vh;
  border-right: 0;
  transition: all 0.3s ease;
}

.el-menu-vertical-custom:not(.el-menu--collapse) {
  width: 240px;
  min-width: 240px;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  margin: 8px;
  border-radius: 4px;
}

.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  margin-right: 50px;
}

.logo-icon {
  color: #409EFF;
  font-size: 24px;
}

.el-menu-item {
  position: relative;
  margin: 8px;
  border-radius: 4px;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.el-menu-item:hover {
  background: rgba(64, 158, 255, 0.1) !important;
}

.el-menu-item:hover .hover-effect {
  opacity: 1;
}

.el-menu-item i {
  color: inherit;
  margin-right: 16px;
  font-size: 18px;
}

.menu-active {
  background: rgba(64, 158, 255, 0.2) !important;
  color: #409EFF !important;
}

.menu-active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #409EFF;
  border-radius: 0 3px 3px 0;
}

.hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center,
  rgba(64, 158, 255, 0.15) 0%,
  transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-collapse {
  position: absolute;
  bottom: 20px;
  width: 100%;
  text-align: center;
  color: #b0bac5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-collapse:hover {
  color: #409EFF;
  transform: scale(1.1);
}

.menu-collapse i {
  font-size: 20px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 50%;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}


</style>