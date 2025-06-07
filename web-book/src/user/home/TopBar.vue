<template>
  <el-row class="top-bar-container">
    <!-- 书籍装饰线条 -->
    <div class="book-line-decor">
      <div
          class="book"
          v-for="i in 5"
          :key="i"
          :style="{ backgroundColor: randomBookColor() }"
      ></div>
    </div>

    <el-col :span="24">
      <div class="nav-wrapper">
        <!-- 左侧企业标识 -->
        <div class="logo-area" @click="$router.push('/')">
          <img
              src="@/assets/logo.png"
              class="site-logo"
              alt="书店logo"
          >
          <span class="shop-name">网上书店</span>
        </div>

        <!-- 右侧功能区域 -->
        <div class="right-menu">
          <!-- 登录状态 -->
          <template v-if="loginStatus">
            <el-dropdown trigger="hover" placement="bottom">
              <div class="user-info">
                <el-avatar :size="30" :src="avatarUrl" class="user-avatar"></el-avatar>
                <span class="username">{{ user ? user.name : '' }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="goUserCenter">
                  <i class="el-icon-user"></i>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click.native="goOrder">
                  <i class="el-icon-tickets"></i>我的订单
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>

          <!-- 未登录状态 -->
          <template v-else>
            <el-button type="text" @click="goLogin" class="menu-item">登录</el-button>
            <el-button type="text" @click="goRegister" class="menu-item">注册</el-button>
          </template>

          <!-- 通用功能 -->
          <el-button type="text" class="menu-item" @click="goReader">
            <i class="el-icon-notebook-2"></i>读者之家
          </el-button>
          <el-button type="text" class="menu-item" @click="goCart">
            <i class="el-icon-shopping-cart-2"></i>购物车
          </el-button>
          <el-dropdown trigger="hover" class="menu-item">
            <span class="dropdown-link">
              <i class="el-icon-service"></i>服务
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="goGuestbook">
                <i class="el-icon-message"></i>留言板
              </el-dropdown-item>
              <el-dropdown-item @click.native="goAnnouncement">
                <i class="el-icon-warning-outline"></i>公告栏
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'TopBar',
  data() {
    return {
      avatarUrl: '',
      loginStatus: false,
      user: null,
      bookColors: ['#8B572A', '#A0522D', '#D2691E', '#CD853F', '#8B4513']
    }
  },
  created() {
    this.checkLoginStatus()
  },
  watch: {
    $route() {
      this.checkLoginStatus()
    }
  },
  methods: {
    randomBookColor() {
      return this.bookColors[Math.floor(Math.random() * this.bookColors.length)]
    },
    checkLoginStatus() {
      try {
        const userData = sessionStorage.getItem('CurUser')
        if (userData) {
          const parsedUser = JSON.parse(userData)
          this.user = parsedUser
          this.avatarUrl = parsedUser.avatar
          this.loginStatus = true
        } else {
          this.loginStatus = false
          this.user = null
          this.avatarUrl = ''
        }
      } catch (e) {
        console.error('用户数据解析失败:', e)
        sessionStorage.removeItem('CurUser')
        this.loginStatus = false
        this.user = null
        this.avatarUrl = ''
      }
    },

    goLogin() {
      this.$router.push('/login')
    },
    goRegister() {
      this.$router.push('/register')
    },
    goUserCenter() {
      this.$router.push('/center')
    },
    goOrder() {
      this.$router.push('/order')
    },
    goReader() {
      this.$router.push('/reader')
    },
    goCart() {
      this.$router.push('/cart')
    },
    goAnnouncement() {
      this.$router.push('/announcement')
    },
    goGuestbook() {
      this.$router.push('/guestbook')
    },

    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        sessionStorage.removeItem('CurUser')
        this.$message.success('退出成功')
        this.checkLoginStatus()
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
$primary-color: #8B4513; // 深木色
$secondary-color: #DAA520; // 金色
$accent-color: #A0522D; // 土橙色

.top-bar-container {
  background: linear-gradient(150deg, darken($primary-color, 10%), $primary-color);
  border-bottom: 3px solid rgba($secondary-color, 0.2);
  height: 70px;
  line-height: 70px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: linear-gradient(90deg,
        transparent 10%,
        rgba($secondary-color, 0.6) 50%,
        transparent 90%
    );
  }
}

.book-line-decor {
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  display: flex;
  gap: 40px;
  padding: 0 20px;
  opacity: 0.8;
  animation: shelfSlide 25s linear infinite;

  .book {
    width: 25px;
    height: 8px;
    transform: skew(-20deg);
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
    border-radius: 2px;
  }
}

.nav-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 30px;
  height: 100%;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateX(5px);

    .site-logo {
      transform: rotate(-5deg) scale(1.1);
    }

    .shop-name::after {
      width: 100%;
    }
  }
}

.site-logo {
  width: 45px;
  height: 45px;
  object-fit: contain;
  border-radius: 50%;
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.shop-name {
  font-size: 26px;
  font-weight: 600;
  font-family: 'Georgia', 'Times New Roman', serif;
  background: linear-gradient(45deg, $secondary-color, lighten($secondary-color, 15%));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 1.5px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -6px;
    left: 0;
    width: 0;
    height: 3px;
    background: $secondary-color;
    transition: width 0.3s ease;
  }
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 15px;

  .menu-item {
    color: rgba(255,255,255,0.9);
    margin-left: 20px;
    font-size: 15px;
    transition: all 0.3s ease;
    position: relative;
    padding: 8px 15px;
    letter-spacing: 0.8px;

    &:hover {
      color: $secondary-color;
      text-shadow: 0 0 8px rgba($secondary-color, 0.4);

      &::before {
        transform: scaleX(1);
      }
    }

    &::before {
      content: '';
      position: absolute;
      left: 0;
      bottom: 10px;
      width: 100%;
      height: 1px;
      background: currentColor;
      transform: scaleX(0);
      transition: transform 0.3s ease;
    }

    i {
      margin-right: 8px;
      font-size: 16px;
      vertical-align: middle;
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 20px;
  transition: all 0.3s ease;
  border-radius: 30px;
  background: rgba(0,0,0,0.1);

  &:hover {
    background: rgba(0,0,0,0.2);
    transform: translateY(-2px);

    .username {
      color: $secondary-color;
    }
  }

  .user-avatar {
    margin-right: 12px;
    border: 2px solid rgba($secondary-color, 0.5);
    background: rgba(0,0,0,0.2);
  }

  .username {
    color: rgba(255,255,255,0.9);
    margin-right: 10px;
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    font-style: italic;
    transition: color 0.3s ease;
  }

  .el-icon-caret-bottom {
    color: rgba($secondary-color, 0.8);
  }
}

.el-dropdown-menu {
  background: lighten($primary-color, 5%) !important;
  border: 1px solid rgba($secondary-color, 0.2) !important;
  box-shadow: 0 6px 16px rgba(0,0,0,0.2) !important;

  .el-dropdown-menu__item {
    color: rgba(255,255,255,0.9) !important;
    font-size: 14px !important;
    padding: 12px 45px !important;
    position: relative;
    transition: all 0.3s ease;

    &:hover {
      background: rgba($secondary-color, 0.1) !important;
      color: $secondary-color !important;
      padding-left: 50px !important;
    }

    &::before {
      content: '📖';
      position: absolute;
      left: 15px;
      top: 50%;
      transform: translateY(-50%);
      opacity: 0.6;
    }

    i {
      margin-right: 12px;
      font-size: 16px;
    }
  }
}

@keyframes shelfSlide {
  from { transform: translateX(-100%); }
  to { transform: translateX(100%); }
}

@media (max-width: 768px) {
  .nav-wrapper {
    padding: 0 15px;
  }

  .shop-name {
    font-size: 20px;
  }

  .right-menu .menu-item {
    margin-left: 12px;
    padding: 6px 10px;
    font-size: 13px;
  }

  .user-info {
    padding: 6px 12px;

    .username {
      max-width: 80px;
    }
  }
}
</style>