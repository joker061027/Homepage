<template>
  <div class="pagination-container">
    <div class="pagination">
      <!-- 上一页（箭头图标） -->
      <el-button 
        type="text" 
        @click="handlePageChange(currentPage - 1)" 
        :disabled="currentPage === 1"
        size="small"
      >
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      
      <!-- 页码区域 -->
      <template v-for="page in visiblePages">
        <!-- 省略号 -->
        <span 
          v-if="page === '...'" 
          class="page-ellipsis"
          :key="'ellipsis-' + page"
        >...</span>
        <!-- 页码按钮 -->
        <el-button
          v-else
          type="text"
          :class="{ 'current-page': page === currentPage }"
          @click="handlePageChange(page)"
          size="small"
          :key="page"
        >{{ page }}</el-button>
      </template>
      
      <!-- 下一页（箭头图标） -->
      <el-button 
        type="text" 
        @click="handlePageChange(currentPage + 1)" 
        :disabled="currentPage === totalPages"
        size="small"
      >
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
  </div>
</template>
<script setup>
import { defineProps, defineEmits, computed } from 'vue';
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'; // 引入Element图标
const props = defineProps({
  currentPage: { type: Number, required: true, default: 1 },
  totalPages: { type: Number, required: true, default: 1 }
});
const emit = defineEmits(['page-change']);
// 页码逻辑：适配「1-6...12」样式
const visiblePages = computed(() => {
  const pages = [];
  const total = props.totalPages;
  const current = props.currentPage;
  if (total <= 5) { // 总页数≤5，显示全部
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    if (current <= 3) { // 当前页≤3，显示「1-6...末页」
      pages.push(1, 2, 3, 4, 5, 6, '...', total);
    } else if (current >= total - 2) { // 当前页≥末页-2，显示「1...末页-5~末页」
      pages.push(1, '...', total - 5, total - 4, total - 3, total - 2, total - 1, total);
    } else { // 中间页，显示「1...当前页±1...末页」
      pages.push(1, '...', current - 1, current, current + 1, '...', total);
    }
  }
  return pages;
});
const handlePageChange = (page) => {
  if (page >= 1 && page <= props.totalPages && page !== '...') {
    emit('page-change', page);
  }
};
</script>
<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
}
/* 按钮/省略号通用样式（灰色方块） */
::v-deep .el-button--text,
.page-ellipsis {
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  margin: 0;
  background-color: #f5f7fa;
  color: #666;
  border-radius: 4px;
  transition: background-color 0.2s;
}
/* 当前页码（蓝色高亮） */
.current-page {
  background-color: #1677ff !important;
  color: #fff !important;
}
/* 按钮hover效果 */
::v-deep .el-button--text:not(.current-page):hover {
  background-color: #e6ebf5;
}
/* 禁用状态（箭头置灰） */
::v-deep .el-button--text.is-disabled {
  color: #c0c4cc;
  background-color: #f5f7fa;
  cursor: not-allowed;
}
/* 省略号单独调整（同尺寸） */
.page-ellipsis {
  user-select: none;
}
/* 图标大小适配 */
::v-deep .el-icon {
  font-size: 14px;
  vertical-align: middle;
}
</style>