<template>
  <div class="login-container">
    <div class="login-box">
      <div class="animated fadeIn">
        <h1 class="login-title">书城会员注册</h1>
        <el-form
            ref="registerForm"
            :model="form"
            :rules="rules"
            @keyup.enter.native="submit"
        >
          <!-- 账号 -->
          <el-form-item prop="no">
            <el-input
                v-model="form.no"
                prefix-icon="el-icon-user"
                placeholder="请输入账号（3-16位字母数字）"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 昵称 -->
          <el-form-item prop="name">
            <el-input
                v-model="form.name"
                prefix-icon="el-icon-user-solid"
                placeholder="请输入昵称"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                prefix-icon="el-icon-lock"
                placeholder="至少8位，包含字母和数字"
                show-password
                clearable
            ></el-input>
            <password-strength :password="form.password" />
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item prop="checkPass">
            <el-input
                v-model="form.checkPass"
                prefix-icon="el-icon-lock"
                placeholder="请再次输入密码"
                show-password
                clearable
            ></el-input>
          </el-form-item>

          <!-- 电话 -->
          <el-form-item prop="phone">
            <el-input
                v-model="form.phone"
                prefix-icon="el-icon-phone"
                placeholder="请输入电话"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item prop="email">
            <el-input
                v-model="form.email"
                prefix-icon="el-icon-message"
                placeholder="请输入有效邮箱"
                clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="company">
            <el-input
                v-model="form.company"
                prefix-icon="el-icon-office-building"
                placeholder="请输入地址"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 底部按钮 -->
          <el-button
              type="primary"
              @click="submit"
              :loading="loading"
              class="submit-btn"
          >
            立即注册
          </el-button>

          <div class="footer-links">
            <router-link to="/login">已有账号？立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import PasswordStrength from '@/PasswordStrength'

export default {
  components: { PasswordStrength },
  data() {
    let checkDuplicate =(rule,value,callback)=>{
      if(this.form.id){
        return callback();
      }
      this.$axios.get(this.$httpUrl+"/user/findByNo?no="+this.form.no).then(res=>res.data).then(res=>{
        if(res.code!==200){
          callback()
        }else{
          callback(new Error('账号已经存在'));
        }
      })
    };
    return {
      loading: false,
      form: {
        no: '',
        name: '',
        password: '',
        checkPass: '',
        email: '',
        phone: '',
        company: ''
      },
      rules: {
        no: [
          {required: true, message: '请输入账号', trigger: 'blur'},
          {min: 3, max: 16, message: '长度3-16位', trigger: 'blur'},
          {validator:checkDuplicate,trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {pattern: /^(?=.*[A-Za-z])(?=.*\d).{8,}$/, message: '至少8位字母+数字'}
        ],
        checkPass: [
          {validator: (r, v, c) => v === this.form.password ? c() : c(new Error('两次密码不一致'))}
        ],
        name:[
          {required: true, message: '请输入昵称', trigger: 'blur'},
        ],
        phone: [
          {required: true,message: "请输入手机号码",trigger: "blur"},
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur"}
        ],
        email: [
          {required: true, message: '请输入邮箱', trigger: 'blur'},
          {type: 'email', message: '邮箱格式不正确'}
        ],
        company: [
          { required: true, message: '请输入地址', trigger: 'blur' },
          { min: 2, max: 30, message: '长度在2到30个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async submit() {
      try {
        await this.$refs.registerForm.validate()
        this.loading = true

        const {data} = await this.$axios.post(this.$httpUrl+'/user/register', this.form)
        if (data.code === 200) {
          this.$message.success('注册成功')
          await this.$router.push('/login')
        } else {
          this.$message.error(data.msg)
        }
      } finally {
        this.loading = false
      }
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
  margin-bottom: 30px;
  font-size: 26px;
  font-weight: 600;
  font-family: "STXingkai", "华文行楷", serif;
}

.submit-btn {
  width: 100%;
  height: 42px;
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
}

.footer-links a {
  color: #8B4513;
  margin: 0 10px;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  font-size: 14px;
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
  height: 35px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
  padding-left: 38px;
}

:deep(.el-input__inner:focus) {
  border-color: #8B4513;
  box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.1);
}

:deep(.el-input__prefix) {
  left: 10px;
  display: flex;
  align-items: center;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

</style>

