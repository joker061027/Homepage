<template>
  <div class="container">
    <!-- 上半部分：订单区域 -->
    <div class="order-section">
      <!-- 左侧：未处理订单 (占2份) -->
      <div class="order-card order-left">
        <h3>未处理餐票订单</h3>
        <el-table :data="pendingOrders" style="width: 100%" :cell-style="{padding: '10px 0'}">
          <el-table-column prop="number" label="序号" :min-width="flexColumnWidth('序号', pendingOrders, 'number', 50)" />
          <el-table-column prop="unit" label="申请单位" :min-width="flexColumnWidth('申请单位', pendingOrders, 'unit', 180)" />
          <el-table-column prop="quantity" label="餐票数量" :min-width="flexColumnWidth('餐票数量', pendingOrders, 'quantity', 100)" />
          <el-table-column prop="date" label="申请日期" :min-width="flexColumnWidth('申请日期', pendingOrders, 'date', 180)" />
          <el-table-column 
                label="" 
                :min-width="flexColumnWidth('状态', pendingOrders, 'state', 120)" 
                align="center"
              >
                <template #default>
                  <el-button type="primary" plain>审核</el-button>
                </template>
          </el-table-column>
          
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="100" style="float: right; margin-top: 5px;"/>
      </div>

      <!-- 右侧：已处理订单 (占1份) -->
      <div class="order-card order-right">
        <h3>已处理餐票订单</h3>
        <el-table :data="processedOrders" style="width: 100%" :cell-style="{padding: '10px 0'}">
          <el-table-column prop="unit" label="申请单位" :min-width="flexColumnWidth('申请单位', processedOrders, 'unit', 180)" />
          <el-table-column prop="quantity" label="餐票数量" :min-width="flexColumnWidth('餐票数量', processedOrders, 'quantity', 100)" />
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="100" style="float: right; margin-top: 35px;"/>
      </div>
    </div>

    <!-- 下半部分：数据统计区域 -->
    <div class="stats-section">
      <!-- 左侧：档口用票情况 (占2份) -->
      <div class="stats-card stats-left">
        <h3>档口用票情况</h3>
        <el-table :data="stallStats" style="width: 100%" :cell-style="{padding: '10px 0'}">
          <el-table-column label="序号" :min-width="flexColumnWidth('序号', stallStats, 'index', 70)" />
          <el-table-column prop="canteen" label="食堂" :min-width="flexColumnWidth('食堂', stallStats, 'canteen', 120)" />
          <el-table-column prop="stall" label="档口名称" :min-width="flexColumnWidth('档口名称', stallStats, 'stall', 150)" />
          <el-table-column prop="unverified" label="未核销餐票" :min-width="flexColumnWidth('未核销餐票', stallStats, 'unverified', 120)" />
          <el-table-column prop="verified" label="已核销餐票" :min-width="flexColumnWidth('已核销餐票', stallStats, 'verified', 120)" />
          
          <el-table-column 
                label="" 
                :min-width="flexColumnWidth('状态', pendingOrders, 'state', 120)" 
                align="center"
              >
                <template #default>
                  <el-button type="primary" plain>核销</el-button>
                </template>
          </el-table-column>
          
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="100" style="float: right; margin-top: 5px;"/>

      </div>

      <!-- 右侧：各单位剩余餐票 (占1份) -->
      <div class="stats-card stats-right">
        <h3>各单位剩余餐票</h3>
        <el-table :data="remainingTickets" style="width: 100%" :cell-style="{padding: '10px 0'}">
          <el-table-column prop="unit" label="单位名称" :min-width="flexColumnWidth('单位名称', remainingTickets, 'unit', 180)" />
          <el-table-column prop="remaining" label="剩余数量" :min-width="flexColumnWidth('剩余数量', remainingTickets, 'remaining', 100)" />
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="100" style="float: right; margin-top: 35px;"/>
      </div>
    </div>
  </div>
</template>

<style scoped>



.container {
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  max-width: 1440px;
  min-width: 767px;
  margin: 0 auto;
}

.order-section, .stats-section {
  margin-top: 50px;
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

/* 订单区域比例设置 */
.order-left {
  flex: 2; /* 左侧占2份 */
}
.order-right {
  flex: 1; /* 右侧占1份 */
}

/* 统计区域比例设置 */
.stats-left {
  flex: 2; /* 左侧占2份 */
}
.stats-right {
  flex: 1; /* 右侧占1份 */
}

.order-card, .stats-card {
  background: white;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
}

.order-card:hover, .stats-card:hover {
  box-shadow: 0 6px 16px 0 rgba(0, 0, 0, 0.15);
}

.order-card h3, .stats-card h3 {
  margin-top: 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 500;
}

@media (max-width: 992px) {
  .order-section, .stats-section {
    flex-direction: column;
  }
  
  /* 移动端恢复等宽 */
  .order-left, .order-right,
  .stats-left, .stats-right {
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

<script lang="ts" setup>

import { ref, onMounted, onUnmounted } from 'vue';
import {
  Check,
  Delete,
  Edit,
  Message,
  Search,
  Star,
} from '@element-plus/icons-vue'
// 动态列宽计算函数
const flexColumnWidth = (label: string, data: any[], prop?: string, minWidth = 100) => {
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

// 模拟数据 - 未处理订单
const pendingOrders = ref([
  { number:1, unit: '财务部', quantity: 25, date: '2023-07-12',state:'审核'},
  { number:2,unit: '研发中心', quantity: 38, date: '2023-07-13' ,state:'审核'},
  { number:3,unit: '市场部', quantity: 42, date: '2023-07-13' ,state:'审核'}
]);

// 模拟数据 - 已处理订单
const processedOrders = ref([
  { unit: '行政部', quantity: 30 },
  { unit: '人力资源', quantity: 22 },
  { unit: '客服中心', quantity: 45 }
]);

// 模拟数据 - 档口用票
const stallStats = ref([
  { canteen: '一食堂', stall: '湘菜档', unverified: 15, verified: 85 },
  { canteen: '二食堂', stall: '面点档', unverified: 8, verified: 92 },
  { canteen: '三食堂', stall: '快餐档', unverified: 12, verified: 78 }
]);

// 模拟数据 - 剩余餐票
const remainingTickets = ref([
  { unit: '技术部', remaining: 120 },
  { unit: '产品部', remaining: 85 },
  { unit: '设计部', remaining: 60 }
]);

// 窗口大小变化时重新计算列宽
const handleResize = () => {
  // 强制更新表格
  pendingOrders.value = [...pendingOrders.value];
  processedOrders.value = [...processedOrders.value];
  stallStats.value = [...stallStats.value];
  remainingTickets.value = [...remainingTickets.value];
};

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>