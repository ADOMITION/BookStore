<template>
  <div class="user-announcement-container">
    <el-card class="announcement-card">
      <!-- 搜索栏 -->
      <div class="filter-container">
        <el-input
            v-model="searchForm.title"
            placeholder="搜索公告标题"
            clearable
            prefix-icon="el-icon-search"
            class="bookish-search"
            @keyup.enter.native="loadAnnouncements"
        >
          <template #prefix>
            <i class="el-icon-notebook-1 search-icon"></i>
          </template>
          <template #append>
            <el-button
                class="search-btn"
                @click="loadAnnouncements"
            >
              搜索公告
              <i class="el-icon-reading"></i>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 公告列表 -->
      <div class="announcement-list">
        <transition-group name="staggered-fade" tag="div">
          <div
              v-for="(item, index) in announcementList"
              :key="item.id"
              class="announcement-item"
              :style="{ transitionDelay: `${index * 0.1}s` }"
          >
            <div class="bookmark">
              <i class="el-icon-collection-tag"></i>
            </div>
            <div class="content-wrapper">
              <div class="announcement-header">
                <h3 class="title">
                  <i class="el-icon-chat-line-round title-icon"></i>
                  {{ item.title }}
                </h3>
                <div class="meta">
                    <el-tag type="info" effect="plain">
                      <i class="el-icon-clock"></i>
                      {{ formatTime(item.createtime) }}
                    </el-tag>
                    <el-tag type="warning" effect="plain">
                      <i class="el-icon-user-solid"></i>
                      发布人：{{ item.name || '书城管理员' }}
                    </el-tag>
                </div>
              </div>
              <div class="content">
                <div class="content-text">{{ item.content }}</div>
                <div class="divider">
                  <span class="left-line"></span>
                  <i class="el-icon-notebook-2"></i>
                  <span class="right-line"></span>
                </div>
              </div>
            </div>
          </div>
        </transition-group>

        <!-- 空状态 -->
        <el-empty
            v-if="announcementList.length === 0"
            description="暂时没有新的公告哦～"
            class="empty-state"
        >
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            background
            :current-page="pagination.pageNum"
            :page-sizes="[10, 20, 30]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            class="book-pagination"
        />
      </div>
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
          param: {
            title: this.searchForm.title
          }
        };

        const res = await this.$axios.post(
            this.$httpUrl + "/announcement/listPage",
            params
        );

        if (res.data.code === 200) {
          this.announcementList = res.data.data || [];
          this.total = res.data.total || 0;
        }
      } catch (error) {
        this.$message.error("获取公告列表失败");
      } finally {
        this.loading = false;
      }
    },

    formatTime(time) {
      if (!time) return "";
      try {
        return formatDate(new Date(time), "yyyy-MM-dd hh:mm");
      } catch (e) {
        console.error("时间格式错误:", time);
        return "";
      }
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.loadAnnouncements();
    },

    handleCurrentChange(val) {
      this.pagination.pageNum = val;
      this.loadAnnouncements();
    }
  }
};
</script>

<style scoped lang="scss">
.user-announcement-container {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f9f5f0 0%, #f0e6dc 100%);
  min-height: 100vh;

  .announcement-card {
    border-radius: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: none;
    background: rgba(255, 255, 255, 0.9);

    ::v-deep .el-card__body {
      padding: 30px;
    }
  }

  .filter-container {
    margin-bottom: 30px;
    display: flex;
    justify-content: center;

    .bookish-search {
      width: 600px;

      ::v-deep .el-input__inner {
        height: 50px;
        border-radius: 25px 0 0 25px;
        border: 2px solid #d4b996;
        font-size: 16px;
        color: #6b4f3a;
        padding-left: 50px;
        background: rgba(255, 255, 255, 0.8);

        &::placeholder {
          color: #b8a895;
        }
      }

      .search-icon {
        position: absolute;
        left: 20px;
        top: 50%;
        transform: translateY(-50%);
        color: #9e7e5e;
        font-size: 20px;
      }

      .search-btn {
        height: 50px;
        width: 120px;
        border-radius: 0 25px 25px 0;
        background: linear-gradient(135deg, #d4b996, #b89a7c);
        color: white;
        border: none;
        font-size: 16px;
        transition: all 0.3s;

        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 12px rgba(184, 154, 124, 0.3);
        }
      }
    }
  }

  .announcement-list {
    .announcement-item {
      position: relative;
      padding: 25px 30px;
      margin-bottom: 25px;
      background: #fff9f2;
      border-radius: 12px;
      box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border-left: 5px solid #d4b996;
      overflow: hidden;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);

        .bookmark {
          color: #c94f4f;
        }
      }

      .bookmark {
        position: absolute;
        right: -8px;
        top: -8px;
        font-size: 40px;
        color: #d4b996;
        transform: rotate(15deg);
        transition: color 0.3s;
      }

      .content-wrapper {
        position: relative;
        z-index: 1;
      }

      .announcement-header {
        margin-bottom: 20px;

        .title {
          color: #6b4f3a;
          margin: 0 0 10px 0;
          font-size: 22px;
          font-weight: 600;
          font-family: 'Microsoft YaHei', serif;
          display: flex;
          align-items: center;

          .title-icon {
            margin-right: 12px;
            color: #c94f4f;
            font-size: 24px;
          }
        }
      }

      .content {
        color: #7a6655;
        line-height: 1.8;
        font-size: 15px;
        position: relative;

        .content-text {
          white-space: pre-wrap;
          padding-bottom: 20px;
        }

        .divider {
          display: flex;
          align-items: center;
          color: #d4b996;
          margin: 20px 0;

          .left-line,
          .right-line {
            flex: 1;
            height: 1px;
            background: linear-gradient(90deg, #e5d5c3, transparent);
          }

          .right-line {
            background: linear-gradient(270deg, #e5d5c3, transparent);
          }

          i {
            margin: 0 15px;
            font-size: 18px;
          }
        }
      }
    }
  }

  .empty-state {
    ::v-deep .el-empty__description {
      color: #9e7e5e;
      font-size: 16px;
      margin-top: 20px;
    }

    .empty-image {
      width: 200px;
      opacity: 0.8;
    }
  }

  .book-pagination {
    ::v-deep {
      .btn-prev,
      .btn-next,
      .el-pager li {
        border-radius: 8px;
        margin: 0 5px;
        background: #f0e6dc;
        color: #9e7e5e;
        min-width: 36px;

        &:hover {
          color: #c94f4f;
        }

        &.active {
          background: linear-gradient(135deg, #d4b996, #b89a7c);
          color: white;
        }
      }
    }
  }
}

// 入场动画
.staggered-fade-enter-active,
.staggered-fade-leave-active {
  transition: all 0.5s ease;
}
.staggered-fade-enter,
.staggered-fade-leave-to {
  opacity: 0;
  transform: translateY(30px);
}
</style>