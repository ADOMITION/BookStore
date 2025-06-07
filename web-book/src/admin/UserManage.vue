<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-input
          v-model="no"
          placeholder="请输入账号"
          prefix-icon="el-icon-user"
          class="filter-item"
          @keyup.enter.native="loadPost"
      />
      <el-input
          v-model="name"
          placeholder="请输入昵称"
          prefix-icon="el-icon-s-check"
          class="filter-item"
          @keyup.enter.native="loadPost"
      />
      <el-input
          v-model="phone"
          placeholder="请输入电话"
          prefix-icon="el-icon-phone"
          class="filter-item"
          @keyup.enter.native="loadPost"
      />

      <div class="operation-btns">
        <el-button
            type="primary"
            icon="el-icon-search"
            class="filter-item"
            @click="loadPost"
        >查询</el-button>
        <el-button
            icon="el-icon-refresh"
            class="filter-item"
            @click="resetParam"
        >重置</el-button>
        <el-button
            type="success"
            icon="el-icon-plus"
            class="filter-item"
            @click="add"
        >新增</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table
        :data="tableData"
        class="custom-table"
        :header-cell-style="{
        background: '#f8f9fa',
        color: '#495057',
        fontWeight: 600
      }"
        stripe
    >
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="no" label="账号" width="110" />
      <el-table-column prop="avatar" label="头像" width="110">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="昵称" width="150" />
      <el-table-column prop="email" label="邮箱" width="150" />
      <el-table-column prop="roleId" label="角色" width="120" align="center">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.roleId === 0 ? 'primary' : 'success'"
              effect="dark"
          >{{ scope.row.roleId === 0 ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" width="180" />
      <el-table-column prop="company" label="地址" />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="mod(scope.row)"
          >编辑</el-button>
          <el-popconfirm
              title="确定要删除该用户吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button
                slot="reference"
                size="mini"
                type="text"
                icon="el-icon-delete"
                style="color: #f56c6c"
            >删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        class="pagination-container"
        background
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog
        :visible.sync="centerDialogVisible"
        width="500px"
        center
    >
      <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-width="90px"
          label-position="left"
      >
        <el-form-item label="账号：" prop="no">
          <el-input
              v-model="form.no"
              placeholder="请输入账号"
              prefix-icon="el-icon-user"
          />
        </el-form-item>
        <el-form-item label="昵称：" prop="name">
          <el-input
              v-model="form.name"
              placeholder="请输入昵称"
              prefix-icon="el-icon-s-custom"
          />
        </el-form-item>
        <el-form-item label="密码：" prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              show-password
          />
        </el-form-item>
        <el-form-item label="邮箱：" prop="email">
          <el-input
              v-model="form.email"
              placeholder="请输入邮箱"
              prefix-icon="el-icon-message"
          />
        </el-form-item>
        <el-form-item label="电话：" prop="phone">
          <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              prefix-icon="el-icon-mobile-phone"
          />
        </el-form-item>
        <el-form-item label="地址：" prop="company">
          <el-input
              v-model="form.company"
              placeholder="请输入详细地址"
              prefix-icon="el-icon-office-building"
          />
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "UserManage",
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
      tableData: [],
      pageSize:5,
      pageNum:1,
      total:0,
      name:'',
      no:'',
      phone:'',
      centerDialogVisible:false,
      form:{
        id:'',
        no:'',
        name:'',
        password:'',
        email:'',
        phone:'',
        roleId:'1',
        company:'',
      },
      rules: {
        no: [
          {required: true, message: '请输入账号', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'},
          {validator:checkDuplicate,trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请输入昵称', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'}
        ],
        email: [
          {required: true, message: '请输入电子邮件', trigger: 'blur'}
        ],
        phone: [
          {required: true,message: "手机号不能为空",trigger: "blur"},
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur"}
        ],
        company: [
          {required: true, message: '请输入地址', trigger: 'blur'}
        ],
      }
    }
  },

  methods:{
    resetForm() {
      this.$refs.form.resetFields();
    },

    del(id){
      this.$axios.get(this.$httpUrl+'/user/del?id='+id).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code===200){
          this.$message({
            message: '操作成功！',
            type: 'success'
          });
          this.loadPost()
        }else{
          this.$message({
            message: '操作失败！',
            type: 'error'
          });
        }

      })
    },
    mod(row){
      console.log(row)
      this.centerDialogVisible = true
      this.$nextTick(()=>{
        //赋值到表单
        this.form.id = row.id
        this.form.no = row.no
        this.form.name = row.name
        this.form.password = ''
        this.form.email = row.email
        this.form.phone = row.phone
        this.form.roleId = row.roleId
        this.form.company = row.company
      })
    },

    add(){
      this.centerDialogVisible = true
      this.$nextTick(()=>{
        this.resetForm()
      })

    },

    doSave(){
      this.$axios.post(this.$httpUrl+'/user/save',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code==200){

          this.$message({
            message: '操作成功！',
            type: 'success'
          });
          this.centerDialogVisible = false
          this.loadPost()
          this. resetForm()
        }else{
          this.$message({
            message: '操作失败！',
            type: 'error'
          });
        }

      })
    },
    doMod(){
      this.$axios.post(this.$httpUrl+'/user/update',this.form).then(res=>res.data).then(res=>{
        if(res.code===200){
          this.$message({
            message: '操作成功！',
            type: 'success'
          });
          this.centerDialogVisible = false
          this.loadPost()
          this. resetForm()
        }else{
          this.$message({
            message: '操作失败！',
            type: 'error'
          });
        }

      })
    },

    save(){
      this.$refs.form.validate((valid) => {
        if (valid) {
          if(this.form.id){
            this.doMod();
          }else{
            this.doSave();
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });

    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.pageNum=1
      this.pageSize=val
      this.loadPost()
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.pageNum=val
      this.loadPost()
    },

    resetParam(){
      this.name=''
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/user/listPageC1',{
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          name:this.name,
          no:this.no,
          phone:this.phone,
          roleId:'1'
        }
      }).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code===200){
          this.tableData=res.data
          this.total=res.total
        }else{
          alert('获取数据失败')
        }

      })
    }
  },
  beforeMount() {
    this.loadPost()
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  background: #f8f9fa;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.filter-item {
  width: 240px;
}

.operation-btns {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.custom-table {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.custom-table::before {
  display: none;
}

.pagination-container {
  margin-top: 20px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

::v-deep .el-dialog__header {
  border-bottom: 1px solid #eee;
}

::v-deep .el-form-item__label {
  color: #606266;
  font-weight: 500;
}

::v-deep .el-input__inner {
  border-radius: 4px;
}

::v-deep .el-button--text {
  padding: 7px 10px;
}
</style>