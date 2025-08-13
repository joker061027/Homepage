<template>
  <el-card class="meal-ticket-card">
    <!-- 头部：标题 + 添加按钮 -->
    <div class="header">
      <h2>餐票类型列表</h2>
      <el-button type="primary" @click="handleAdd">添加类型</el-button>
    </div>
    
    <!-- 表格内容 -->
    <el-table :data="tableData" style="width: 100%; margin-top: 16px" v-loading="loading"
      element-loading-text="加载中..." height="333px">
      <!-- 序号列 -->
      <el-table-column prop="id" label="序号" align="center" flex="1"></el-table-column>
      
      <!-- 餐票名称列 -->
      <el-table-column prop="name" label="餐票名称" align="center" flex="1"></el-table-column>
      
      <!-- 可用食堂列（动态字体大小） -->
      <el-table-column prop="canteens" label="可用食堂" align="center" flex="1">
        <template #default="scope">
          <span 
            :class="{
              'normal-canteen': !scope.row.isManyCanteens,
              'small-canteen': scope.row.isManyCanteens
            }"
            :title="scope.row.canteens"  
          >
            {{ scope.row.canteens }}
          </span>
        </template>
      </el-table-column>
      
      <!-- 面额列 -->
      <el-table-column prop="amount" label="面额" align="center" flex="1" class-name="amount-column"></el-table-column>
      
      <!-- 默认有效期列 -->
      <el-table-column prop="validity" label="有效期" align="center" flex="1"></el-table-column>
      
      <!-- 状态列 -->
      <el-table-column prop="status" label="状态" align="center" flex="1">
        <template #default="scope">
          <el-tag 
            :type="scope.row.status === '启用' ? 'success' : 'warning'"
            @click="handleStatusToggle(scope.row)" 
            class="status-tag">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" flex="1">
        <template #default="scope">
          <el-button
            type="text"
            @click="handleEdit(scope.row)"
            class="edit-btn">
            修改
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页组件 -->
   <div class="pagination-wrapper">
  <Pagination 
    :current-page="currentPage" 
    :total-pages="totalPages"
    @page-change="handlePageChange"
  />
</div>
    
    <!-- 添加餐票弹窗 -->
    <AddTicketDialog
      :visible="dialogVisible"
      @close="dialogVisible = false"
      @refreshList="fetchTicketData"
    />
    
    <!-- 编辑餐票弹窗 -->
    <EditTicketDialog
      :visible="editDialogVisible"
      :edit-data="currentEditData"
      @close="editDialogVisible = false"
      @refreshList="fetchTicketData"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getTicketsByPage, toggleTicketStatus, getCanteens } from '../services/api3';
import { ElMessage } from 'element-plus';
import AddTicketDialog from './AddTicketDialog.vue';
import EditTicketDialog from './EditTicketDialog.vue';
import Pagination from './Pagination.vue';

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(5);
const totalPages = ref(1);
const totalItems = ref(0);

// 弹窗控制
const dialogVisible = ref(false);
const editDialogVisible = ref(false);
const currentEditData = ref(null);

// 食堂数据及映射表
const canteens = ref([]);
const canteenMap = ref({});

// 格式化表格数据（含食堂数量判断）
const formatTableData = (rawData) => {
  return rawData.map((item, index) => {
    const canteenCount = item.canteenIds?.length || 0; // 计算食堂数量
    const canteenNames = item.canteenIds && item.canteenIds.length > 0
      ? item.canteenIds.map(id => canteenMap.value[id] || '未知食堂').join('、')
      : '未关联食堂';

    return {
      id: (currentPage.value - 1) * pageSize.value + index + 1,
      name: item.typeName,
      canteens: canteenNames,
      isManyCanteens: canteenCount > 3, // 超过3个食堂标记为"多食堂"
      amount: item.value,
      validity: item.overtime ? `${item.overtime}` : '未设置',
      status: item.status === 1 ? '启用' : '禁用',
      raw: item
    };
  });
};

// 获取餐票数据（分页）
const fetchTicketData = async () => {
  loading.value = true;
  try {
    const pageData = await getTicketsByPage(currentPage.value, pageSize.value);
    tableData.value = formatTableData(pageData.content);
    totalItems.value = pageData.totalElements;
    totalPages.value = pageData.totalPages;
  } catch (error) {
    ElMessage.error(error.message || '获取餐票数据失败');
    tableData.value = [];
    totalItems.value = 0;
    totalPages.value = 1;
  } finally {
    loading.value = false;
  }
};

// 获取食堂数据并构建映射表
const fetchCanteenData = async () => {
  try {
    const data = await getCanteens();
    canteens.value = data;
    const map = {};
    data.forEach(canteen => {
      map[canteen.canteenId] = canteen.canteenName;
    });
    canteenMap.value = map;
  } catch (error) {
    ElMessage.error(error.message || '获取食堂数据失败');
  }
};

// 初始化加载
onMounted(async () => {
  await fetchCanteenData();
  fetchTicketData();
});

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchTicketData();
};

// 打开添加弹窗
const handleAdd = () => {
  dialogVisible.value = true;
};

// 打开编辑弹窗
const handleEdit = (row) => {
  currentEditData.value = row.raw;
  editDialogVisible.value = true;
};

// 状态切换
const handleStatusToggle = async (row) => {
  try {
    const currentStatus = row.raw.status;
    const targetStatus = currentStatus === 1 ? 0 : 1;
    await toggleTicketStatus(row.raw.typeId, targetStatus);
    row.raw.status = targetStatus;
    row.status = targetStatus === 1 ? '启用' : '禁用';
    ElMessage.success(`已${targetStatus === 1 ? '启用' : '禁用'}餐票类型`);
  } catch (error) {
    ElMessage.error(error.message || '状态切换失败');
  }
};
</script>

<style scoped>
.meal-ticket-card {
  margin: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  font-size: 18px;
  font-weight: bold;
  color: #1e40af;
  margin: 0;
}

/* 表格样式 */
::v-deep .el-table {
  border: none;
  table-layout: fixed; /* 固定表格布局，确保列宽稳定 */
}

::v-deep .el-table__header-wrapper .el-table__header th{
  border-bottom: 0.5px solid #e5e7eb;
}

::v-deep .el-table__body-wrapper .el-table__row {
  border-bottom: none; 
}

::v-deep .el-table__column:nth-child(1) .el-table__cell {
  border-right: 1px solid #e5e7eb;
}

::v-deep .el-table__column:not(:nth-child(1)) .el-table__cell {
  border-right: none;
}

::v-deep .el-table__cell {
  padding: 12px 16px !important;
  text-align: center !important;
}

::v-deep .el-table__column:nth-child(1) .el-table__cell {
  padding-left: 30px !important;
}

::v-deep .el-table__column:last-child .el-table__cell {
  padding-right: 30px !important;
}

::v-deep .amount-column .cell {
  padding-right: 30px !important;
  transform: translateX(-8px);
}

::v-deep .el-table__header th > .cell {
  text-align: center !important;
}

/* 可用食堂列字体控制 */
::v-deep .normal-canteen {
  font-size: 14px; /* 默认字体 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

::v-deep .small-canteen {
  font-size: 12px; /* 食堂较多时缩小字体 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 限制可用食堂列最大宽度，配合字体调整控制表格大小 */
::v-deep .el-table__column:nth-child(3) .el-table__cell {
  max-width: 220px;
}

/* 状态标签样式 */
::v-deep .status-tag {
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 10px;
}

::v-deep .status-tag:hover {
  opacity: 0.8;
  transform: scale(1.05);
}

/* 编辑按钮样式 */
::v-deep .edit-btn {
  color: #1e40af;
}

::v-deep .edit-btn:hover {
  color: #b45309;
  text-decoration: underline;
}

/* 分页容器样式 - 固定在右下角 */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end; /* 靠右对齐 */
  margin-top: 16px; /* 与表格保持间距 */
  padding-right: 20px; /* 右侧留一些边距 */
}

/* 覆盖分页组件内部的居中样式 */
::v-deep .pagination-container {
  justify-content: flex-end;
  margin-top: 0; /* 取消原有的顶部margin，使用外层容器的margin */
}
</style>