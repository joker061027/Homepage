<template>
  <div class="container">
    <el-menu
  :default-active="activeIndex"
  class="el-menu-demo"
  mode="horizontal"
  :ellipsis="false"
  @select="handleSelect"
>
  <!-- 左侧菜单 -->
  <el-menu-item index="0" class="main-title">数字餐票</el-menu-item>

  <!-- 右侧菜单 -->
  <div style="margin-left: auto; display: flex">
    <el-menu-item index="1">首页</el-menu-item>
    <el-menu-item index="2">餐票订单</el-menu-item>
    <el-menu-item index="3">餐票核销</el-menu-item>
    <el-menu-item index="4">系统管理</el-menu-item>
    <el-sub-menu index="5">
      <template #title>后勤管理员</template>
      <el-menu-item index="5-1">个人信息</el-menu-item>
      <el-menu-item index="5-2">修改密码</el-menu-item>
      <el-menu-item index="5-3">退出登录</el-menu-item>
    </el-sub-menu>
  </div>
</el-menu>
    <!-- 主标题 -->
    

    <!-- 上半部分：订单区域 -->
    <div class="order-section">
      <!-- 左侧：未处理订单 -->
      <div class="order-card">
        <h3>未处理餐票订单</h3>
        <el-table :data="pendingOrders" style="width: 100%">
          <el-table-column prop="" label="序号" width="100" />
          <el-table-column prop="unit" label="申请单位" width="180" />
          <el-table-column prop="quantity" label="餐票数量" width="100" />
          <el-table-column prop="date" label="申请日期" />
          <el-table-column prop="" label="状态" width="120" />
          
        </el-table>
      </div>

      <!-- 右侧：已处理订单 -->
      <div class="order-card">
        <h3>已处理餐票订单</h3>
        <el-table :data="processedOrders" style="width: 100%">
          <el-table-column prop="unit" label="申请单位" />
          <el-table-column prop="quantity" label="餐票数量" width="100" />
        </el-table>
      </div>
    </div>

    <!-- 下半部分：数据统计区域 -->
    <div class="stats-section">
      <!-- 左侧：档口用票情况 -->
      <div class="stats-card">
        <h3>档口用票情况</h3>
        <el-table :data="stallStats" style="width: 100%">
          <el-table-column prop="" label="序号" width="70" />
          <el-table-column prop="unit" label="食堂" />
          <el-table-column prop="stall" label="档口名称" />
          <el-table-column prop="unverified" label="未核销餐票" width="100" />
          <el-table-column prop="verified" label="已核销餐票" width="100" />
           <el-table-column prop="" label="状态" width="70" />
        </el-table>
      </div>

      <!-- 右侧：各单位剩余餐票 -->
      <div class="stats-card">
        <h3>各单位剩余餐票</h3>
        <el-table :data="remainingTickets" style="width: 100%">
          <el-table-column prop="unit" label="单位名称" />
          <el-table-column prop="remaining" label="剩余数量" width="100" />
        </el-table>
      </div>
    </div>
  </div>
</template>
<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #f8f9fa;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  height: 60px;
}

.main-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.nav-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-menu li {
  margin-left: 20px;
}

.nav-menu li a {
  text-decoration: none;
  color: #606266;
  font-size: 16px;
  transition: color 0.3s;
  padding: 8px 12px;
  border-radius: 4px;
}

.nav-menu li a:hover {
  color: #409eff;
  background-color: #ecf5ff;
}

.nav-menu li a.router-link-exact-active {
  color: #409eff;
  background-color: #ecf5ff;
  font-weight: 500;
}
.container {
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  max-width: 1440px;
  min-width: 767px;
  margin: 0 auto;
  
}


.nav-menu {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e6e6e6;
 

}

.main-title {
  /* text-align: center; */
  /* margin: 20px 0 30px;
  font-size: 24px;
  font-weight: 600; */
  
  color: #3788d9;
}

.order-section, .stats-section {
  margin-top: 50px;
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.order-card, .stats-card {
  flex: 1;
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
}
</style>
<script lang="ts" setup>
import { ref } from 'vue';

const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
// 模拟数据 - 未处理订单
const pendingOrders = ref([
  { unit: '财务部', quantity: 25, date: '2023-07-12' },
  { unit: '研发中心', quantity: 38, date: '2023-07-13' },
  { unit: '市场部', quantity: 42, date: '2023-07-13' }
]);

// 模拟数据 - 已处理订单
const processedOrders = ref([
  { unit: '行政部', quantity: 30 },
  { unit: '人力资源', quantity: 22 },
  { unit: '客服中心', quantity: 45 }
]);

// 模拟数据 - 档口用票
const stallStats = ref([
  { stall: '一食堂湘菜档', unverified: 15, verified: 85 },
  { stall: '二食堂面点档', unverified: 8, verified: 92 },
  { stall: '三食堂快餐档', unverified: 12, verified: 78 }
]);

// 模拟数据 - 剩余餐票
const remainingTickets = ref([
  { unit: '技术部', remaining: 120 },
  { unit: '产品部', remaining: 85 },
  { unit: '设计部', remaining: 60 }
]);
</script>

