<template>
  <el-dialog
    v-model="visible"
    width="1400px"
    :before-close="handleClose"
    center
  >
    <template #header>
      <span class="custom-title">订单管理</span>
    </template>
    <div class="order-detail">
      <!-- 订单基本信息 -->
      <div class="order-info horizontal" style="margin:0 100px;">
        <div class="info-block">
          <span class="label">订单编号:</span>
          <span class="value">{{ props.orderDetail.orderNumber }}</span>
        </div>
        <div class="info-block">
          <span class="label">申请单位:</span>
          <span class="value">{{ props.orderDetail.agenciesName }}</span>
        </div>
        <div class="info-block">
          <span class="label">申请人:</span>
          <span class="value">{{ props.orderDetail.applicantName }}</span>
        </div>
      </div>

      <!-- 订单项表格 双列排布 -->
      <div class="order-items double-table">
        
        <div class="table-row">
          <el-table
            :data="leftOrderItems"
            style="width: 48%"
            :cell-style="{padding: '8px 0'}"
          >
            <el-table-column prop="sequence" label="序号" align="center" min-width="100" />
            <el-table-column prop="typeName" label="餐票类型" align="center" min-width="100" />
            <el-table-column prop="orderAmount" label="订购数量" align="center" min-width="100" />
          </el-table>
          <el-table
            :data="rightOrderItems"
            style="width: 48%"
            :cell-style="{padding: '8px 0'}"
          >
            <el-table-column prop="sequence" label="序号" align="center" min-width="100" />
            <el-table-column prop="typeName" label="餐票类型" align="center" min-width="100" />
            <el-table-column prop="orderAmount" label="订购数量" align="center" min-width="100" />
          </el-table>
        </div>
      </div>

      <!-- 订单状态 -->
      <div class="opo" style="height: 400px; background-color: #F0F9FF ">
        <div v-if="[0, -1, 1].includes(orderDetail.status)" class="order-status center">
          <div class="status-display">
            <span
              class="status-text"
              :class="getStatusClass(orderDetail.status)"
            >
              {{ orderDetail.statusDescription }}
            </span>
          </div>
        </div>
      </div>
    </div>
  

    <!-- 操作按钮 -->
    <template #footer>
      <div class="dialog-footer center">
        <el-button 
          v-if="props.orderDetail.status === 2" 
          type="danger" 
          @click="handleWithdraw"
          :loading="withdrawLoading"
        >
          撤回订单
        </el-button>
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderDetail: {
    type: Object,
    default: () => ({
      orderNumber: '',
      agenciesName: '',
      applicantName: '',
      status: 2,
      statusDescription: '待审核',
      orderItems: []
    })
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'withdraw'])

// 响应式数据
const withdrawLoading = ref(false)

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const leftOrderItems = computed(() =>
  props.orderDetail.orderItems.filter((_, idx) => idx % 2 === 0)
)
const rightOrderItems = computed(() =>
  props.orderDetail.orderItems.filter((_, idx) => idx % 2 === 1)
)

// 方法
const handleClose = () => {
  visible.value = false
}

const handleWithdraw = async () => {
  withdrawLoading.value = true
  try {
    const apiUrl = `http://localhost:8080/api/orders/withdraw/${props.orderDetail.orderNumber}`;
    
    const response = await fetch(apiUrl, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (result.code === '200') {
      ElMessage.success('订单撤回成功')
      props.orderDetail.status = 99
      props.orderDetail.statusDescription = '已撤回'
      emit('withdraw', props.orderDetail.orderNumber)
      handleClose()
    } else {
      ElMessage.error(result.message || '撤回订单失败')
    }
  } catch (error) {
    console.error('撤回订单失败:', error)
    ElMessage.error('撤回订单失败')
  } finally {
    withdrawLoading.value = false
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 1:
      return 'status-approved'
    case 0:
      return 'status-pending'
    case -1:
      return 'status-rejected'
    case 99:
      return 'status-withdrawn'
    default:
      return 'status-unknown'
  }
}
</script>

<style scoped>


.opo{
  
  width: 1300px;
  border: 1px solid #68696a;
  margin: 0 auto;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-detail {
  padding: 20px 0;
}

.order-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #606266;
  width: 80px;
  flex-shrink: 0;
}

.value {
  color: #303133;
  margin-left: 10px;
}

.order-items {
  margin-bottom: 20px;
}

.order-items h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.order-status {
  text-align: center;
  margin-top: 20px;
}

.status-display {
  display: inline-block;
  padding: 10px 20px;
  border-radius: 4px;
  
}

.status-text {
  font-size: 16px;
  font-weight: 500;
}

.status-pending {
  color: #e6a23c;
}

.status-approved {
  color: #67c23a;
}

.status-rejected {
  color: #f56c6c;
}

.status-printed {
  color: #409eff;
}

.status-unknown {
  color: #909399;
}

.status-withdrawn {
  color: #909399;
  font-size: 40px;
  font-weight: bold;
}

.status-text.status-pending {
  color: #fd0000;
  font-size: 60px;
  
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.order-items.double-table .table-row {
  display: flex;
  justify-content: space-between;
  gap: 4%;
}
.order-items.double-table .el-table {
  min-width: 220px;
}
.order-items.double-table .el-table__header th,
.order-items.double-table .el-table__body td {
  text-align: center !important;
  min-width: 100px;
}
.order-info.horizontal {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.info-block {
  display: flex;
  align-items: center;
}
.dialog-footer.center {
  text-align: center;
}
.dialog-footer.center .el-button {
  margin-left: 10px;
}
.opo {
  height: 600px;
  width: 1300px;
  background-color: #bbd4ee;
  margin: 0 auto;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-status.center {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
::v-deep(.el-dialog__title) {
  color: #409eff !important;
}
.custom-title {
  color: #0848af;
  font-size: 20px;
  font-weight: bold;
}
</style> 