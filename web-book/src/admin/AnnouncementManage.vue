<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="filter-container">
        <el-input
            v-model="searchForm.title"
            placeholder="输入公告标题"
            clearable
            prefix-icon="el-icon-notebook-1"
            style="width: 200px; margin-right: 10px;"
            @keyup.enter.native="loadAnnouncements"
        />
        <div class="operation-btns">
          <el-button
              type="primary"
              icon="el-icon-search"
              @click="loadAnnouncements"
          >查询</el-button>
          <el-button
              icon="el-icon-refresh"
              @click="resetSearch"
          >重置</el-button>
          <el-button
              type="success"
              icon="el-icon-plus"
              @click="handleCreate"
          >新增公告</el-button>
        </div>
      </div>

      <!-- 公告表格 -->
      <el-table
          :data="announcementList"
          v-loading="loading"
          border
          fit
          highlight-current-row
          style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="title" label="公告标题" width="180" />
        <el-table-column prop="content" label="公告内容" min-width="250">
          <template slot-scope="{row}">
            <div class="content-box">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template slot-scope="{row}">
            {{ formatTime(row.createtime) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="创建人" width="120" align="center" >
        <template slot-scope="{row}">
          <div>{{ row.name }}</div>
        </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{row}">
            <div class="action-btns">
              <el-button
                  type="text"
                  size="small"
                  @click="handleEdit(row)"
                  icon="el-icon-edit"
              >编辑</el-button>
              <el-button
                  type="text"
                  size="small"
                  @click="handleDelete(row.id)"
                  icon="el-icon-delete"
                  class="delete-btn"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            background
            :current-page="pagination.pageNum"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>

      <!-- 编辑对话框 -->
      <el-dialog
          :title="dialogTitle"
          :visible.sync="dialogVisible"
          width="600px"
      >
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input v-model="form.content" type="textarea" rows="5" />
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { formatDate } from "@/store/formatDate";

export default {
  data() {
    return {
      loading: false,
      announcementList: [],
      total: 0,
      pagination: {
        pageNum: 1,
        pageSize: 10
      },
      searchForm: {
        title: ""
      },
      name: JSON.parse(sessionStorage.getItem('CurUser')).name,
      dialogVisible: false,
      dialogTitle: "",
      form: {
        id: null,
        title: "",
        content: "",
      },
      rules: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        content: [{ required: true, message: "请输入内容", trigger: "blur" }],
      }
    };
  },
  created() {
    this.loadAnnouncements();
  },
  methods: {
    async loadAnnouncements() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          param: this.searchForm
        };
        const res = await this.$axios.post(this.$httpUrl+'/announcement/listPage', params);
        if (res.data.code === 200) {
          this.announcementList = res.data.data;
          this.total = res.data.total;
        }
      } catch (error) {
        this.$message.error("获取公告列表失败");
      } finally {
        this.loading = false;
      }
    },

    formatTime(time) {
      if (!time) return "暂无时间信息";
      try {
        const dateStr = typeof time === "string" ? time : time.toString();
        return formatDate(new Date(dateStr), "yyyy-MM-dd hh:mm:ss");
      } catch (e) {
        console.error("时间格式错误:", time);
        return "时间格式异常";
      }
    },

    resetSearch() {
      this.searchForm = { title: "" };
      this.pagination.pageNum = 1;
      this.loadAnnouncements();
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadAnnouncements();
    },

    handleCurrentChange(val) {
      this.pagination.pageNum = val;
      this.loadAnnouncements();
    },

    handleCreate() {
      this.form = { id: null, title: "", content: "", name: null };
      this.dialogTitle = "新增公告";
      this.dialogVisible = true;
    },

    handleEdit(row) {
      this.form = { ...row };
      this.dialogTitle = "编辑公告";
      this.dialogVisible = true;
    },

    async handleDelete(id) {
      this.$confirm("确认删除该公告？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(async () => {
        try {
          const res = await this.$axios.get(this.$httpUrl+'/announcement/delete', {
            params: { id }
          });

          if (res.data.code === 200) {
            this.$message.success("删除成功");
            this.loadAnnouncements();
          } else {
            this.$message.error(res.data.msg || "删除失败");
          }
        } catch (error) {
          this.$message.error("删除操作失败");
        }
      });
    },

    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            const postData = {
              ...this.form,
              name: this.name
            }
            const isEdit = !!this.form.id;
            const url = isEdit ? '/announcement/update' : '/announcement/add';

            const res = await this.$axios.post(this.$httpUrl+url, postData);

            if (res.data.code === 200) {
              this.$message.success(isEdit ? "修改成功" : "新增成功");
              this.dialogVisible = false;
              this.loadAnnouncements();
            } else {
              this.$message.error(res.data.msg || "操作失败");
            }
          } catch (error) {
            this.$message.error("请求失败，请检查网络");
          }
        }
      });
    }
  }
};
</script>

<style scoped lang="scss">
.app-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fc 0%, #f1f3f9 100%);
  min-height: calc(100vh - 84px);

  .filter-container {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
    margin-bottom: 24px;
    padding: 20px 24px;
    display: flex;
    align-items: center;
    gap: 16px;

    .operation-btns {
      margin-left: auto;
      display: flex;
      gap: 12px;

      .el-button {
        padding: 10px 20px;
        border-radius: 8px;
        font-weight: 500;

        &[type="primary"] {
          background: linear-gradient(135deg, #409eff, #3375e0);
        }

        &[type="success"] {
          background: linear-gradient(135deg, #67c23a, #4a9b20);
        }
      }
    }
  }

  .el-table {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);

    .content-box {
      line-height: 1.6;
      max-height: 60px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }

    .action-btns .delete-btn {
      color: #f56c6c;
      &:hover {
        color: darken(#f56c6c, 10%);
      }
    }
  }

  .el-dialog {
    border-radius: 12px;
    .el-form-item__content {
      .el-textarea__inner {
        min-height: 120px !important;
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
  }
}
</style>