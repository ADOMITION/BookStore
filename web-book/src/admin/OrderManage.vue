<template>
  <div class="order-container">
    <!-- 搜索结果区域 -->
    <div class="filter-card">
      <div class="filter-content">
        <el-input
            v-model.number="searchParams.id"
            placeholder="订单ID"
            class="search-input"
            clearable
            @keyup.enter.native="loadOrders"
        >
          <template #prefix>
            <i class="el-icon-search"></i>
          </template>
        </el-input>
        <el-input
            v-model.number="searchParams.userId"
            placeholder="用户ID"
            class="search-input"
            clearable
            @keyup.enter.native="loadOrders"
        >
          <template #prefix>
            <i class="el-icon-user"></i>
          </template>
        </el-input>
        <el-select
            v-model="searchParams.status"
            placeholder="订单状态"
            clearable
            class="status-select"
        >
          <template #prefix>
            <i class="el-icon-more"></i>
          </template>
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>

        <div class="operation-group">
          <el-button
              type="primary"
              class="operation-btn gradient-blue"
              @click="loadOrders"
          >
            <i class="el-icon-search"></i>查询
          </el-button>
          <el-button
              class="operation-btn reset-btn"
              @click="resetSearch"
          >
            <i class="el-icon-refresh-left"></i>重置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <el-table
          :data="orderList"
          v-loading="loading"
          element-loading-spinner="el-icon-loading"
          style="width: 100%"
          header-row-class-name="table-header"
          stripe
          @expand-change="handleExpand"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <div class="sub-table-container">
              <el-table
                  :data="props.row.items"
                  border
                  class="sub-table"
                  v-loading="props.row.loading"
                  element-loading-text="正在加载商品..."
                  empty-text="暂无商品数据"
              >
                <el-table-column label="图书名称">
                  <template slot-scope="{row}">
                    <span v-if="row.book && row.book.name">
                      {{ row.book.name }}
                    </span>
                    <span v-else class="muted-text">
                      {{ row.bookName || '商品信息已失效' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="quantity" label="数量" width="100" align="center" />
                <el-table-column label="原价" width="120" align="right">
                  <template slot-scope="{row}">
                    <span v-if="row.book && row.book.price">
                      ¥{{ Number(row.book.price).toFixed(2) }}
                    </span>
                    <span v-else class="muted-text">--</span>
                  </template>
                </el-table-column>
                <el-table-column label="折扣价" width="120" align="right">
                  <template slot-scope="{row}">
                    <span v-if="row.price">
                      ¥{{ Number(row.price).toFixed(2) }}
                    </span>
                    <span v-else class="muted-text">--</span>
                  </template>
                </el-table-column>
                <el-table-column label="小计" width="140" align="right">
                  <template slot-scope="{row}">
                    <span v-if="row.price && row.quantity">
                      ¥{{ (Number(row.price) * Number(row.quantity)).toFixed(2) }}
                    </span>
                    <span v-else class="muted-text">--</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="订单号" width="100" />
        <el-table-column prop="userId" label="用户ID" width="60" align="center" />
        <el-table-column prop="userName" label="用户名" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="100" align="right">
          <template slot-scope="{row}">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="150">
          <template slot-scope="{row}">
            <el-tag :type="statusTagType[row.status]">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createtime" label="创建时间" width="180">
          <template slot-scope="{row}">
            {{ formatTime(row.createtime) }}
          </template>
        </el-table-column>
        <el-table-column prop="recipient" label="收件人" width="120" />
        <el-table-column prop="tel" label="联系方式" width="140" />
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="text"
                class="detail-btn"
                @click="showOrderDetail(scope.row)"
            >
              <i class="el-icon-document"></i>详情
            </el-button>
            <el-button
                v-if="scope.row.status === '已付款'"
                type="text"
                size="small"
                @click="handleApprove(scope.row.id)"
                icon="el-icon-check"
                class="yes-btn"
            >发货</el-button>
            <el-popconfirm
                title="确定要取消此订单吗？"
                @confirm="cancelOrder(scope.row.id)"
                v-if="scope.row.status === '待付款'"
            >
              <el-button
                  slot="reference"
                  size="mini"
                  type="text"
                  class="cancel-btn"
              >
                <i class="el-icon-close"></i>取消
              </el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            background
            :current-page="pagination.current"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pagination.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
        :title="`订单详情 - #${currentOrder.id}`"
        :visible.sync="detailVisible"
        width="800px"
        class="order-dialog"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusTagType[currentOrder.status]">
            {{ currentOrder.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">
          ¥{{ currentOrder.totalAmount?.toFixed(2) }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatTime(currentOrder.createtime) }}
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">
          {{ currentOrder.payTime ? formatTime(currentOrder.payTime) : '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="收件地址" span="2">
          {{ currentOrder.address }}
        </el-descriptions-item>
        <el-descriptions-item label="收件人">
          {{ currentOrder.recipient }}（{{ currentOrder.tel }}）
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">商品明细</el-divider>
      <el-table :data="currentOrder.items" border>
        <el-table-column label="图书名称">
          <template slot-scope="{row}">
            <span v-if="row.book && row.book.name">
              {{ row.book.name }}
            </span>
            <span v-else class="muted-text">
              {{ row.bookName || '商品信息已失效' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="原价" width="120" align="right">
          <template slot-scope="{row}">
            <span v-if="row.book && row.book.price">
              ¥{{ Number(row.book.price).toFixed(2) }}
            </span>
            <span v-else class="muted-text">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="折扣价" width="120" align="right">
          <template slot-scope="{row}">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" align="center" />
        <el-table-column label="小计" width="140" align="right">
          <template slot-scope="{row}">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from "@/store/formatDate";

export default {
  name: "OrderManage",
  data() {
    return {
      searchParams: {
        id: null,
        userId: null,
        status: null,
      },
      statusOptions: [
        { value: "待付款", label: "待付款" },
        { value: "已付款", label: "已付款" },
        { value: "已发货", label: "已发货" },
        { value: "已完成", label: "已完成" },
        { value: "已取消", label: "已取消" },
      ],
      statusTagType: {
        "待付款": "warning",
        "已付款": "success",
        "已发货": "success",
        "已完成": "info",
        "已取消": "danger",
      },
      orderList: [],
      book: [],
      currentOrder: {},
      loading: false,
      detailVisible: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0,
      },
    };
  },
  created() {
    this.loadOrders();
  },
  methods: {
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
    async loadOrders() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size,
          param: this.cleanSearchParams(),
        };
        const { data } = await this.$axios.post(
            this.$httpUrl + "/order/page",
            params
        );
        if (data.code === 200) {
          this.orderList = data.data || [];
          this.pagination.total = data.total || 0;
        }
      } catch (error) {
        console.error(error);
        this.$message.error("加载订单列表失败");
      } finally {
        this.loading = false;
      }
    },
    cleanSearchParams() {
      return Object.fromEntries(
          Object.entries(this.searchParams).filter(([_, v]) => v !== null && v !== "")
      );
    },
    async handleApprove(id) {
      try {
        const res = await this.$axios.post(this.$httpUrl+'/order/updateStatus', { id, status: '已发货' })
        if (res.data.code === 200) {
          this.$message.success('发货成功')
          this.loadOrders()
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async cancelOrder(id) {
      try {
        const { data } = await this.$axios.post(
            this.$httpUrl + `/order/acancel/${id}`
        );
        if (data.code === 200) {
          this.$message.success("订单已取消");
          this.loadOrders();
        }
      } catch (error) {
        this.$message.error(error.response?.data?.msg || "取消订单失败");
      }
    },
    async showOrderDetail(order) {
      this.detailVisible = true;
      try {
        const { data } = await this.$axios.get(
            this.$httpUrl + `/order/detail/${order.id}`
        );
        if (data.code === 200) {
          this.currentOrder = data.data;
        }
      } catch (error) {
        this.$message.error("获取订单详情失败");
      }
    },
    handleSizeChange(size) {
      this.pagination.size = size;
      this.loadOrders();
    },
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.loadOrders();
    },
    resetSearch() {
      this.searchParams = { id: null, userId: null, status: null };
      this.loadOrders();
    },
    handleExpand(row, expandedRows) {
      if (expandedRows.length && !row.items) {
        this.loadOrderItems(row);
      }
    },
    async loadOrderItems(row) {
      try {
        const { data } = await this.$axios.get(
            this.$httpUrl + `/order/detail/${row.id}`
        );
        if (data.code === 200) {
          this.$set(row, "items", data.data.items);
          const index = this.orderList.findIndex((item) => item.id === row.id);
          if (index > -1) {
            this.$set(this.orderList, index, {
              ...this.orderList[index],
              ...data.data,
            });
          }
        }
      } catch (error) {
        console.error("加载订单商品失败:", error);
        this.$set(row, "items", []);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.order-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fc 0%, #f1f3f9 100%);
  min-height: calc(100vh - 84px);

  // 搜索卡片
  .filter-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
    margin-bottom: 24px;
    padding: 20px 24px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    }

    .filter-content {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      align-items: center;

      // 输入框样式
      ::v-deep .el-input__inner,
      ::v-deep .el-select__tags {
        background: rgba(245, 247, 250, 0.6);
        border-radius: 8px;
        transition: all 0.2s ease;

        &:hover {
          background: rgba(245, 247, 250, 0.8);
          border-color: #c0c4cc;
        }

        &:focus-within {
          background: #fff;
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
        }
      }

      // 操作按钮组
      .operation-group {
        display: flex;
        gap: 12px;
        justify-content: flex-end;

        .operation-btn {
          padding: 10px 20px;
          border-radius: 8px;
          font-weight: 500;
          transition: all 0.3s ease;

          i {
            margin-right: 6px;
          }

          &.gradient-blue {
            background: linear-gradient(135deg, #409eff, #3375e0);
            border: none;
            color: white;

            &:hover {
              opacity: 0.9;
              transform: translateY(-1px);
              box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
            }
          }

          &.reset-btn {
            background: rgba(245, 247, 250, 0.8);
            color: #606266;
            border: 1px solid #dcdfe6;

            &:hover {
              background: rgba(245, 247, 250, 1);
              color: #409eff;
              border-color: #c6e2ff;
            }
          }
        }
      }
    }
  }

  // 表格卡片
  .table-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
    }

    // 表头样式
    ::v-deep .table-header th {
      background: linear-gradient(135deg, #f8f9fc, #f1f3f9) !important;
      color: #2c3e50;
      font-weight: 600;
      height: 52px;
    }

    // 表格行样式
    ::v-deep .el-table__row {
      transition: all 0.2s ease;

      &:hover {
        transform: translateX(4px);
        box-shadow: 4px 0 12px rgba(64, 158, 255, 0.1);
      }

      td {
        padding: 12px 0;
      }

      .yes-btn {
        color: #9b22e1;
        &:hover {
          color: darken(#9b22e1, 10%);
          background-color: rgba(112, 25, 204, 0.14);
        }
      }

    }

    // 子表格容器
    .sub-table-container {
      padding: 16px;
      background: rgba(245, 247, 250, 0.4);

      .sub-table {
        border-radius: 8px;
        overflow: hidden;
        box-shadow: inset 0 0 12px rgba(0, 0, 0, 0.04);

        .muted-text {
          color: #909399;
          font-style: italic;
        }
      }
    }

    // 分页样式
    .pagination-container {
      padding: 20px;
      background: transparent;

      ::v-deep .el-pagination {
        .btn-prev,
        .btn-next,
        .el-pager li {
          border-radius: 8px;
          margin: 0 4px;
          transition: all 0.2s ease;
        }

        .el-pager li.active {
          background: linear-gradient(135deg, #409eff, #3375e0);
          color: white;
          font-weight: 500;
        }
      }
    }
  }

  // 订单对话框
  .order-dialog {
    ::v-deep .el-dialog {
      border-radius: 16px;
      overflow: hidden;
      background: linear-gradient(145deg, #f8f9fc, #ffffff);

      .el-dialog__header {
        padding: 20px;
        background: linear-gradient(90deg, #f8f9fc, #ffffff);
        border-bottom: 1px solid #ebeef5;

        .el-dialog__title {
          color: #2c3e50;
          font-size: 18px;
          font-weight: 600;
        }
      }

      .el-descriptions__body {
        background: rgba(245, 247, 250, 0.4);
      }

      .el-descriptions__label {
        color: #5c6b77;
        font-weight: 500;
      }

      .el-divider__text {
        background: transparent;
        color: #409eff;
        font-size: 14px;
      }
    }
  }

  // 按钮样式
  .detail-btn {
    padding: 6px 12px;
    border-radius: 6px;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(64, 158, 255, 0.1);
      transform: scale(1.05);
    }
  }

  .cancel-btn {
    padding: 6px 12px;
    border-radius: 6px;
    transition: all 0.2s ease;
    color: #ea2f15;
    &:hover {
      background: rgba(245, 108, 108, 0.1);
      transform: scale(1.05);
    }
  }
}

// 全局加载样式
::v-deep .el-loading-mask {
  background: rgba(255, 255, 255, 0.8);

  .el-loading-spinner {
    i {
      color: #409eff;
    }

    .el-loading-text {
      color: #606266;
      letter-spacing: 0.5px;
    }
  }
}
</style>