<template>
  <div class="user-center-container">
    <!-- 头部区域 -->
    <div class="header-banner">
      <img src="@/assets/logo.png" class="store-logo" alt="书店logo">
      <h1 class="welcome-text">欢迎回来，{{ userInfo.name || '书友' }}！</h1>
    </div>

    <div class="main-content">
      <!-- 左侧个人信息 -->
      <div class="profile-section">
        <el-card class="profile-card">
          <div slot="header" class="clearfix">
            <span class="card-title">📖 我的账户</span>
          </div>

          <!-- 用户头像 -->
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar || require('@/assets/default-avatar.png')"></el-avatar>
            <el-button type="text" class="upload-btn" @click="showUploadDialog">更换头像</el-button>
          </div>

          <!-- 会员信息 -->
          <div class="membership-info">
            <div class="info-item">
              <span class="label">会员等级：</span>
              <el-tag :type="levelTagType(currentLevel)">{{ currentLevel }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">积分：</span>
              <span class="value">{{ userInfo.score }} 分</span>
            </div>
          </div>
        </el-card>

        <!--最近订单-->
        <el-card class="order-card">
          <div slot="header" class="clearfix">
            <span class="card-title">📦 最近订单</span>
            <el-button
                type="text"
                @click="$router.push('/order')"
                :loading="loadingOrders"
            >
              {{ loadingOrders ? '加载中...' : '查看全部' }}
            </el-button>
          </div>
          <div class="order-list">
            <div v-for="order in recentOrders" :key="order.id" class="order-item">
              <div class="order-info">
                <span class="order-title">{{ order.title }}</span>
                <span class="order-date">{{ order.date }}</span>
              </div>
              <el-tag :type="statusTagType(order.status)">
                {{ order.status }}
              </el-tag>
            </div>
            <div v-if="!loadingOrders && recentOrders.length === 0" class="empty-orders">
              暂无最近订单
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧表单 -->
      <div class="form-section">
        <el-card class="form-card">
          <div slot="header" class="clearfix">
            <span class="card-title">✏️ 编辑资料</span>
          </div>
          <el-form
              :model="userInfo"
              ref="userInfoForm"
              :rules="rules"
              label-width="100px"
              class="user-form"
          >
            <el-form-item label="用户名" prop="no">
              <el-input v-model="userInfo.no"></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="name">
              <el-input v-model="userInfo.name"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="userInfo.password"></el-input>
            </el-form-item>
            <el-form-item label="电子邮件" prop="email">
              <el-input v-model="userInfo.email"></el-input>
            </el-form-item>
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="userInfo.phone"></el-input>
            </el-form-item>
            <el-form-item label="我的地址" prop="company">
              <el-input v-model="userInfo.company"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveInfo">保存信息</el-button>
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  @click="saveInfo"
                  class="save-btn"
                  :loading="saving"
              >
                <i class="el-icon-check"></i> 保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>

    <div class="service-cards">
      <el-card class="service-card">
        <div class="service-content">
          <i class="el-icon-tickets service-icon"></i>
          <h3>我的评论</h3>
          <p>查看您发出的评论是否通过</p>
          <el-button type="text" @click="goMyReview">立即查看 ></el-button>
        </div>
      </el-card>

      <el-card class="service-card">
        <div class="service-content">
          <i class="el-icon-star-off service-icon"></i>
          <h3>我的求购/预定</h3>
          <p>查看您的求购或预定是否到货</p>
          <el-button type="text" @click="goMyReservation">立即查看 ></el-button>
        </div>
      </el-card>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog
        title="更换头像"
        :visible.sync="uploadDialogVisible"
        width="30%"
        :close-on-click-modal="false"
    >
      <div class="upload-content">
        <el-upload
            class="avatar-uploader"
            action=""
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleFileChange"
            accept="image/jpeg,image/png"
        >
          <div class="avatar-preview">
            <img v-if="avatarPreview" :src="avatarPreview" class="avatar">
            <div v-else class="upload-trigger">
              <i class="el-icon-plus avatar-uploader-icon"></i>
              <div class="upload-tips">点击上传头像</div>
            </div>
            <div class="hover-mask">
              <i class="el-icon-zoom-in"></i>
            </div>
          </div>
        </el-upload>
        <div class="action-buttons">
          <el-button
              type="primary"
              :loading="uploading"
              @click="uploadAvatar"
          >确认上传</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {formatDate} from "@/store/formatDate";

export default {
  name: "UserCenter",
  data() {
    let checkDuplicate =(rule,value,callback)=>{
      if(this.userInfo.id){
        return callback();
      }
      this.$axios.get(this.$httpUrl+"/user/findByNo?no="+this.userInfo.no).then(res=>res.data).then(res=>{
        if(res.code!==200){
          callback()
        }else{
          callback(new Error('用户名已经存在'));
        }
      })
    };
    return {
      levels: ['青铜会员', '白银会员', '黄金会员', '铂金会员', '钻石会员'],
      levelThresholds: [1000, 2000, 3000, 4000, 5000], // 各等级所需最低积分
      recentOrders: [],
      loadingOrders: false,
      saving: false,
      uploadDialogVisible: false,
      avatarPreview: '',
      uploading: false,
      userInfo: {
        id:'',
        no:'',
        name:'',
        password:'',
        email:'',
        phone:'',
        company:'',
        avatar:''
      },

      rules: {
        no: [
          {required: true, message: '请更新您的用户名', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'},
          {validator:checkDuplicate,trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请更新您的昵称', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请更新您的密码', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'}
        ],
        email: [
          {required: true, message: '请请更新您的电子邮件', trigger: 'blur'}
        ],
        phone: [
          {required: true,message: "手机号不能为空，请更新您的手机号",trigger: "blur"},
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur"}
        ],
        company: [
          {required: true, message: '请更新您的地址', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.loadUserProfile();
    this.loadRecentOrders();
  },
  computed: {
    // 计算当前会员等级
    currentLevel() {
      if (!this.userInfo.score) return this.levels[0];
      const levelIndex = Math.floor(this.userInfo.score / 1000);
      return this.levels[Math.min(levelIndex, this.levels.length - 1)];
    }
  },
  methods: {
    // 修改标签类型映射
    levelTagType(level) {
      const map = {
        '青铜会员': 'info',
        '白银会员': 'primary',
        '黄金会员': 'success',
        '铂金会员': 'warning',
        '钻石会员': 'danger'
      };
      return map[level] || 'info';
    },
    statusTagType(status) {
      const map = {
        '待付款': 'warning',
        '已付款': 'primary',
        '已发货': 'success',
        '已完成': 'success',
        '已取消': 'danger'
      }
      return map[status] || 'info';
    },
    async loadRecentOrders() {
      try {
        this.loadingOrders = true;
        const userId = JSON.parse(sessionStorage.getItem('CurUser')).id;
        const res = await this.$axios.get(this.$httpUrl+'/user/recent', {
          params: { userId }
        });
        if (res.data.code === 200) {
          this.recentOrders = res.data.data.map(order => ({
            id: order.id,
            title: this.formatBookNames(order.items),
            date: this.formatTime(order.createtime),
            status: order.status
          }));
        }
      } catch (error) {
        this.$message.error('获取订单失败');
      } finally {
        this.loadingOrders = false;
      }
    },

    formatBookNames(items) {
      if (items.length === 1) {
        return `《${items[0].book.name}》`;
      }
      return `《${items[0].book.name}》等${items.length}件商品`;
    },
    formatTime(timeStr) {
      const date = new Date(timeStr);
      return formatDate(date, 'yyyy-MM-dd');
    },
    async loadUserProfile() {
      try {
        this.loading = true;
        // 从本地存储获取用户ID
        const userId = JSON.parse(sessionStorage.getItem('CurUser')).id;
        const res = await this.$axios.get(this.$httpUrl+'/user/profile', {
          params: {
            id: userId
          }
        });
        if (res.data.code === 200) {
          this.userInfo = res.data.data;
          this.userInfo.score = this.userInfo.score || 1500;
        }
      } catch (error) {
        this.$message.error('获取用户信息失败');
      }
    },
    async saveInfo() {
      try {
        await this.$refs.userInfoForm.validate();
        // 构造更新数据
        const updateData = {
          ...this.userInfo,
          // 如果密码为空则保留原密码
          password: this.userInfo.password || undefined
        };
        const res = await this.$axios.post(this.$httpUrl+'/user/uupdate', updateData);
        if (res.data.code === 200) {
          this.$message.success('信息更新成功');
          // 更新本地存储
          const storedUser = JSON.parse(sessionStorage.getItem('CurUser'));
          sessionStorage.setItem('CurUser', JSON.stringify({
            ...storedUser,
            ...updateData,
            password: '' // 清除本地存储在浏览器的密码
          }));
        }
      } catch (error) {
        this.$message.error(error.response?.data?.msg || '更新失败');
      }
    },
    showUploadDialog() {
      this.uploadDialogVisible = true
    },
    handleFileChange(file) {
      const isImage = file.raw.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('只能上传图片格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过2MB!');
        return false;
      }

      this.avatarPreview = URL.createObjectURL(file.raw);
      this.uploadFile = file.raw;
    },

    async uploadAvatar() {
      if (!this.uploadFile) {
        this.$message.warning('请选择要上传的头像');
        return;
      }

      try {
        this.uploading = true;
        const formData = new FormData();
        formData.append('file', this.uploadFile);
        formData.append('userId', this.userInfo.id);

        const res = await this.$axios.post(
            this.$httpUrl + '/file/uploadAvatar',
            formData,
            {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            }
        );
        if (res.data.code === 200) {
          this.userInfo.avatar = res.data.data;
          this.$message.success('头像更新成功');
          this.uploadDialogVisible = false;
          // 更新本地存储
          const storedUser = JSON.parse(sessionStorage.getItem('CurUser'));
          storedUser.avatar = res.data.data;
          sessionStorage.setItem('CurUser', JSON.stringify(storedUser));
        }
      } catch (error) {
        this.$message.error('上传失败');
      } finally {
        this.uploading = false;
      }
    },
    goMyReview(){
      this.$router.push('/myreview');
    },
    goMyReservation(){
      this.$router.push('/myreservation');
    }
  }
}
</script>

<style scoped>
.user-center-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-banner {
  background: linear-gradient(135deg, #2c3e50, #3498db);
  padding: 40px;
  border-radius: 8px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
}

.store-logo {
  width: 120px;
  margin-bottom: 20px;
}

.welcome-text {
  color: white;
  font-size: 28px;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
  margin-bottom: 24px;
}

.profile-card, .form-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.upload-btn {
  margin-top: 10px;
}

.membership-info {
  padding: 20px 0;
}

.info-item {
  margin: 15px 0;
  display: flex;
  align-items: center;
}

.label {
  color: #666;
  min-width: 80px;
}

.value {
  color: #2c3e50;
  font-weight: 500;
}

.order-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.order-list {
  max-height: 300px;
  overflow-y: auto;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.order-title {
  font-weight: 500;
  color: #333;
}

.order-date {
  font-size: 12px;
  color: #999;
  display: block;
}

.service-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.service-card {
  transition: transform 0.3s ease;
  border-radius: 12px;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.1);
}

.service-content {
  text-align: center;
  padding: 30px;
}

.service-icon {
  font-size: 40px;
  color: #3498db;
  margin-bottom: 15px;
}

.service-content h3 {
  color: #2c3e50;
  margin: 10px 0;
}

.service-content p {
  color: #666;
  margin-bottom: 15px;
}

.save-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}
.upload-content {
  text-align: center;
}

.avatar-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.hover-mask i {
  color: white;
  font-size: 24px;
}

.avatar-preview:hover .hover-mask {
  opacity: 1;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #8c939d;
}

.avatar-uploader-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.upload-tips {
  font-size: 12px;
}

.action-buttons {
  margin-top: 20px;
}
.el-tag.info {
  background-color: #e0f2f1;
  color: #009688;
}
.el-tag.primary {
  background-color: #e3f2fd;
  color: #2196f3;
}
.el-tag.success {
  background-color: #e8f5e9;
  color: #4caf50;
}
.el-tag.warning {
  background-color: #fff3cd;
  color: #ff9800;
}
.el-tag.danger {
  background-color: #ffebee;
  color: #f44336;
}
</style>