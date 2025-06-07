<template>
  <div class="login-container">
    <div class="login-box">
      <div class="animated fadeIn">
        <h1 class="login-title">书城阅读，知识之旅</h1>
        <el-form
            ref="loginForm"
            :model="loginForm"
            :rules="rules"
            status-icon
            @keyup.enter.native="confirm"
        >
          <el-form-item prop="no">
            <el-input
                v-model="loginForm.no"
                prefix-icon="el-icon-user"
                placeholder="请输入账号"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="loginForm.password"
                prefix-icon="el-icon-lock"
                placeholder="请输入密码"
                show-password
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item>
            <el-button
                type="primary"
                @click="confirm"
                :loading="loading"
                class="submit-btn"
            >
              登录
            </el-button>
          </el-form-item>

          <div class="footer-links">
            <router-link to="/register">立即注册</router-link>
            <span>|</span>
            <a @click="showForget">忘记密码?</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      loginForm: {
        no: '',
        password: ''
      },
      rules: {
        no: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
        ]
      }
    }
  },
  methods: {
    async confirm() {
      try {
        this.loading = true
        await this.$refs.loginForm.validate()

        const { data: res } = await this.$axios.post(this.$httpUrl+'/user/login', this.loginForm)
        if (res.code !== 200) throw new Error(res.msg)

        sessionStorage.setItem("CurUser", JSON.stringify(res.data))
        this.redirectByRole(res.data.roleId)
        this.$message.success('登录成功')
      } catch (err) {
        this.$message.error('登录失败，,请检查用户名或密码')
      } finally {
        this.loading = false
      }
    },

    redirectByRole(roleId) {
      const routes = {
        0: '/admin',
        1: '/'
      }
      this.$router.replace(routes[roleId] || '/')
    },

    showForget() {
      this.$message.info('请联系客服电话(0830-11804000)重置密码')
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('@/assets/default-cover.jpg');
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 420px;
  padding: 45px 40px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-box::before {
  content: '📖';
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 40px;
  background: white;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 35px;
  font-size: 28px;
  font-weight: 600;
  font-family: "STXingkai", "华文行楷", serif;
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: #8B4513;
  border-color: #8B4513;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: #A0522D;
  border-color: #A0522D;
  transform: translateY(-2px);
}

.footer-links {
  text-align: center;
  margin-top: 25px;
  color: #666;
  display: flex;
  justify-content: center;
  align-items: center;
}

.footer-links a {
  color: #8B4513;
  margin: 0 10px;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  font-size: 14px;
}

.footer-links span {
  color: #8B4513;
  opacity: 0.6;
}

.footer-links a:hover {
  color: #A0522D;
  text-decoration: underline;
}

.animated {
  animation-duration: 1s;
}

@keyframes fadeIn {
  from { 
    opacity: 0; 
    transform: translateY(30px); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0); 
  }
}

:deep(.el-input__inner) {
  height: 45px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

:deep(.el-input__inner:focus) {
  border-color: #8B4513;
  box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.1);
}

:deep(.el-input__prefix) {
  left: 10px;
}

:deep(.el-form-item) {
  margin-bottom: 25px;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 15px;
}
</style>