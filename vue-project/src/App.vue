<template>
  <div class="container">
<el-menu
  :default-active="$route.path"
  class="el-menu-demo"
  mode="horizontal"
  :ellipsis="false"
  router
  @select="handleSelect"
>
  <!-- 左侧菜单 -->
  <el-menu-item index="0" class="main-title" >数字餐票</el-menu-item>

  <!-- 右侧菜单 -->
  <div style="margin-left: auto; display: flex">
     
   <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/ticket-management">餐票订单</el-menu-item>
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
    <router-view></router-view>
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
  color: #3788d9;
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




</style>
<script lang="ts" setup>
import { ref } from 'vue';

const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

</script>

