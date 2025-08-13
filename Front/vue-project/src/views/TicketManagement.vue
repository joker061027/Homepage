<template>
  <div class="container">
    <!-- 餐票管理区域 -->
    <div class="ticket-section">
      <!-- 左侧：餐票使用情况 (占2份) -->
      <div class="ticket-card ticket-left">
        <div class="header-with-button">
          <h3 style="color: #0848af;">餐票使用情况</h3>
          <div>
            <!-- 占位按钮，保持与右侧表头等高 -->
            <el-button type="primary" size="small" style="visibility: hidden;">占位</el-button>
          </div>
        </div>
        <el-table
          :data="ticketUsageData"
          style="width: 100%"
          :cell-style="{padding: '10px 0'}"
          :height="TABLE_FIXED_HEIGHT"
          v-loading="usageLoading"
        >
          <el-table-column
            prop="sequence"
            label="序号"
            :min-width="flexColumnWidth('序号', ticketUsageData, 'sequence', 60)"
            align="center"
          />
          <el-table-column
            prop="useDate"
            label="使用日期"
            :min-width="flexColumnWidth('使用日期', ticketUsageData, 'useDate', 100)"
          />
          <el-table-column
            prop="ticketCode"
            label="餐票编码"
            :min-width="flexColumnWidth('餐票编码', ticketUsageData, 'ticketCode', 120)"
          />
          <el-table-column
            prop="canteen"
            label="使用食堂"
            :min-width="flexColumnWidth('使用食堂', ticketUsageData, 'canteen', 80)"
          />
          <el-table-column
            prop="stallNumber"
            label="档口"
            :min-width="flexColumnWidth('档口', ticketUsageData, 'stallNumber', 60)"
          />
          <el-table-column
            prop="stallName"
            label="档口名称"
            :min-width="flexColumnWidth('档口名称', ticketUsageData, 'stallName', 100)"
          />
        </el-table>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="usagePagination.total"
          :page-size="usagePagination.pageSize"
          :current-page="usagePagination.currentPage"
          @current-change="handleUsagePageChange"
          style="float: right; margin-top: 15px; margin-bottom: 0;"
        />
      </div>

      <!-- 右侧：餐票订单 (占1份) -->
      <div class="ticket-card ticket-right">
        <div class="header-with-button">
          <h3 style="color: #0848af;">餐票订单</h3>
          <div>
            <!-- 新增：UserId 搜索（TicketManagement）开始 -->
            <template v-if="ENABLE_USER_SEARCH">
              <el-input
                v-model="searchUserId"
                placeholder="输入用户ID"
                size="small"
                style="width: 200px; margin-right: 8px;"
                clearable
              />
              <el-button type="primary" size="small" @click="handleSearchByUserId">按用户搜索</el-button>
            </template>
            <!-- 新增：UserId 搜索（TicketManagement）结束 -->
            <!-- 新增：后端刷新数据（TicketManagement）开始 -->
            <el-button type="success" size="small" :loading="refreshing" @click="refreshServerData">刷新数据</el-button>
            <!-- 新增：后端刷新数据（TicketManagement）结束 -->
            <el-button type="primary" size="small">申请订单</el-button>
          </div>
        </div>
        <el-table
          :data="ticketOrderData"
          style="width: 100%"
          :cell-style="{padding: '10px 0'}"
          :height="TABLE_FIXED_HEIGHT"
          v-loading="orderLoading"
        >
          <el-table-column
            prop="sequence"
            label="序号"
            :min-width="flexColumnWidth('序号', ticketOrderData, 'sequence', 60)"
            align="center"
          />
          <el-table-column
            prop="applyDate"
            label="申请日期"
            :min-width="flexColumnWidth('申请日期', ticketOrderData, 'applyDate', 100)"
          />
          <el-table-column
            prop="quantity"
            label="数量"
            :min-width="flexColumnWidth('数量', ticketOrderData, 'quantity', 60)"
            align="center"
          />
          <el-table-column
            prop="used"
            label="已使用"
            :min-width="flexColumnWidth('已使用', ticketOrderData, 'used', 70)"
            align="center"
          />
          <el-table-column
            prop="status"
            label="状态"
            :min-width="flexColumnWidth('状态', ticketOrderData, 'status', 80)"
            align="center"
          >
            <template #default="scope">
              <el-tag
                :type="getStatusTagType(scope.row.status)"
                size="small"
              >
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="orderNumber"
            label="订单编号"
            :min-width="flexColumnWidth('订单编号', ticketOrderData, 'orderNumber', 120)"
          />
          <el-table-column
            label="操作"
            width="100"
            align="center"
          >
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleViewDetail(scope.row.orderNumber)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="orderPagination.total"
          :page-size="orderPagination.pageSize"
          :current-page="orderPagination.currentPage"
          @current-change="handleOrderPageChange"
          style="float: right; margin-top: 15px; margin-bottom: 0;"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <OrderDetailModal
      v-model="orderDetailVisible"
      :order-detail="currentOrderDetail"
      @withdraw="handleOrderWithdraw"
    />
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  max-width: 1540px;
  min-width: 767px;
  margin: 0 auto;
}

.ticket-section {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

/* 餐票区域比例设置 */
.ticket-left {
  flex: 3; /* 左侧占2份 */
}
.ticket-right {
  flex: 4; /* 右侧占1份 */
}

.ticket-card {
  background: white;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
}

.ticket-card:hover {
  box-shadow: 0 6px 16px 0 rgba(0, 0, 0, 0.15);
}

.ticket-card h3 {
  margin-top: 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 500;
}

.header-with-button {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
}

.header-with-button h3 {
  margin-bottom: 0;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
  flex: 1;
}

@media (max-width: 992px) {
  .ticket-section {
    flex-direction: column;
  }

  /* 移动端恢复等宽 */
  .ticket-left, .ticket-right {
    flex: 1;
  }
}

/* 列宽测量元素样式 */
.text-measure {
  position: absolute;
  visibility: hidden;
  height: auto;
  width: auto;
  white-space: nowrap;
  padding: 0 8px;
  font-size: 14px;
  font-family: inherit;
}
</style>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import OrderDetailModal from '@/components/OrderDetailModal.vue';

// ========================
// FEATURE TOGGLE（可随时关闭/开启）- UserId 搜索（TicketManagement）
// 设为 false 即可在不改其他代码的情况下彻底禁用搜索功能
// ========================
const ENABLE_USER_SEARCH = false;

// 统一分页大小
const PAGE_SIZE = 13;
// 固定表格显示高度（使分页条位置固定）
const TABLE_HEADER_HEIGHT = 48; // 表头高度（近似值）
const TABLE_ROW_HEIGHT = 48;    // 单行高度（近似值）
const TABLE_FIXED_HEIGHT = TABLE_HEADER_HEIGHT + PAGE_SIZE * TABLE_ROW_HEIGHT;

// 动态列宽计算函数
const flexColumnWidth = (label, data, prop, minWidth = 100) => {
  if (!data || data.length === 0) return `${minWidth}px`;

  // 创建测量元素
  const measureElement = document.createElement('span');
  measureElement.className = 'text-measure';
  document.body.appendChild(measureElement);

  try {
    // 计算表头宽度
    measureElement.innerText = label;
    let maxWidth = measureElement.offsetWidth;

    // 计算数据列最大宽度
    if (prop) {
      data.forEach(item => {
        if (item[prop] !== undefined) {
          measureElement.innerText = item[prop].toString();
          const width = measureElement.offsetWidth;
          if (width > maxWidth) maxWidth = width;
        }
      });
    }

    // 确保不小于最小宽度
    maxWidth = Math.max(maxWidth + 32, minWidth);
    return `${maxWidth}px`;
  } finally {
    // 清理测量元素
    document.body.removeChild(measureElement);
  }
};

// 加载状态
const usageLoading = ref(false);
const orderLoading = ref(false);
// 新增：后端刷新loading（TicketManagement）
const refreshing = ref(false);

// 新增：UserId 搜索（TicketManagement）开始（集中区）
const searchUserId = ref('');
// 新增：UserId 搜索触发器
const handleSearchByUserId = () => {
  if (!ENABLE_USER_SEARCH) return;
  orderPagination.value.currentPage = 1;
  fetchTicketOrderData(1, searchUserId.value);
};
// 新增：UserId 搜索（TicketManagement）结束（集中区）

// 分页配置
const usagePagination = ref({
  currentPage: 1,
  pageSize: PAGE_SIZE,
  total: 0
});

const orderPagination = ref({
  currentPage: 1,
  pageSize: PAGE_SIZE,
  total: 0
});

// 餐票使用情况数据
const ticketUsageData = ref([]);

// 餐票订单数据
const ticketOrderData = ref([]);

// 订单详情弹窗相关
const orderDetailVisible = ref(false);
const currentOrderDetail = ref({
  orderNumber: '',
  agenciesName: '',
  applicantName: '',
  status: 2,
  statusDescription: '待审核',
  orderItems: []
});

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case '待审核':
      return 'warning';
    case '已批准':
      return 'success';
    case '已拒绝':
      return 'danger';
    case '已打印':
      return 'info';
    case '已撤销':
      return 'info';
    default:
      return '';
  }
};

// 将后端状态码转换为前端显示文本
const getStatusText = (statusCode) => {
  switch (statusCode) {
    case 1:
      return '已批准';
    case 0:
      return '待审核';
    case -1:
      return '已拒绝';
    case 4:
      return '已打印';
    case 99:
      return  '已撤销';
    default:
      return '未知状态';
  }
};

// 获取餐票使用情况数据
const fetchTicketUsageData = async (page = 1) => {
  usageLoading.value = true;
  try {
    // 这里应该调用实际的API接口
    // const response = await api.getTicketUsage({ page, pageSize: 10 });

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));

    console.log(`获取第${page}页餐票使用情况数据`);

    // 模拟数据
    const mockData = [
      { sequence: 1, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '一食堂', stallNumber: '1档号', stallName: '红烧牛肉面' },
      { sequence: 2, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '二食堂', stallNumber: '1档号', stallName: '叉烧豆干' },
      { sequence: 3, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '二食堂', stallNumber: '1档号', stallName: '自助快餐' },
      { sequence: 4, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '三食堂', stallNumber: '2档号', stallName: '好吃不贵饺子' },
      { sequence: 5, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '三食堂', stallNumber: '2档号', stallName: '黄焖鸡红面' },
      { sequence: 6, useDate: '2025.7.8', ticketCode: '75f9c98e6e554f54', canteen: '三食堂', stallNumber: '2档号', stallName: '逸义豆花面' }
    ];

    ticketUsageData.value = mockData;
    usagePagination.value.total = 120; // 模拟总数

  } catch (error) {
    console.error('获取餐票使用情况失败:', error);
  } finally {
    usageLoading.value = false;
  }
};

// 获取餐票订单数据
// 新增：支持按用户ID搜索（TicketManagement）开始（集中区）
const fetchTicketOrderData = async (page = 1, userId = '') => {
  orderLoading.value = true;

  try {
    const base = `http://localhost:8080/api/orders`;
    const apiUrl = (ENABLE_USER_SEARCH && userId && userId.trim())
      ? `${base}/home-tickets/user-page?userId=${encodeURIComponent(userId.trim())}&page=${page - 1}&size=${PAGE_SIZE}`
      : `${base}/home-tickets/page?page=${page - 1}&size=${PAGE_SIZE}`;

    const response = await fetch(apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (result.code === '200' && result.data) {
      const pageData = result.data;

      if (pageData.content && pageData.content.length > 0) {
        const transformedData = pageData.content.map((item) => ({
          sequence: item.sequence,
          applyDate: item.createDatetime,
          quantity: item.totalQuantity,
          used: item.usedQuantity,
          status: getStatusText(item.status),
          orderNumber: item.orderNumber
        }));

        ticketOrderData.value = transformedData;
        orderPagination.value.total = pageData.totalElements;
        orderPagination.value.currentPage = pageData.page + 1;
      } else {
        ticketOrderData.value = [];
        orderPagination.value.total = 0;
      }
    } else {
      ticketOrderData.value = [];
      orderPagination.value.total = 0;
    }

  } catch (error) {
    ticketOrderData.value = [];
    orderPagination.value.total = 0;
  } finally {
    orderLoading.value = false;
  }
};
// 新增：支持按用户ID搜索（TicketManagement）结束（集中区）

// 新增：后端刷新数据（TicketManagement）开始（集中区）
const refreshServerData = async () => {
  try {
    refreshing.value = true;
    // 优先使用后端已有测试数据接口（会生成一些订单）
    const res = await fetch('http://localhost:8080/api/orders/create-test-data', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }
    });
    // 成功/失败都继续拉取最新分页数据
  } catch (e) {
    // 忽略错误，仍然刷新列表
  } finally {
    await fetchTicketOrderData(1, ENABLE_USER_SEARCH ? searchUserId.value : '');
    refreshing.value = false;
  }
};
// 新增：后端刷新数据（TicketManagement）结束（集中区）

// 处理餐票使用情况分页变化
const handleUsagePageChange = (page) => {
  usagePagination.value.currentPage = page;
  fetchTicketUsageData(page);
};

// 处理餐票订单分页变化
const handleOrderPageChange = (page) => {
  orderPagination.value.currentPage = page;
  // 新增：分页时保留用户搜索条件（TicketManagement）
  fetchTicketOrderData(page, ENABLE_USER_SEARCH ? searchUserId.value : '');
};

// 处理查看订单详情
const handleViewDetail = async (orderNumber) => {
  try {
    const apiUrl = `http://localhost:8080/api/orders/detail/${orderNumber}`;
    
    const response = await fetch(apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (result.code === '200' && result.data) {
      currentOrderDetail.value = result.data;
      orderDetailVisible.value = true;
    } else {
      console.error('获取订单详情失败:', result.message);
    }
  } catch (error) {
    console.error('获取订单详情失败:', error);
  }
};

// 处理订单撤回
const handleOrderWithdraw = (orderNumber) => {
  console.log('撤回订单:', orderNumber);
  // 这里可以添加撤回订单的逻辑
  // 撤回成功后可以刷新订单列表
  fetchTicketOrderData(orderPagination.value.currentPage);
};

// 窗口大小变化时重新计算列宽
const handleResize = () => {
  // 强制更新表格
  ticketUsageData.value = [...ticketUsageData.value];
  ticketOrderData.value = [...ticketOrderData.value];
};

// 组件挂载时获取数据
onMounted(() => {
  fetchTicketUsageData();
  // 新增：默认加载全部（不带用户筛选）（TicketManagement）
  fetchTicketOrderData();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>