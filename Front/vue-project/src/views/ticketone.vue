<template>
  <div class="ticket-generator">
    <h1>餐票生成器</h1>
    
    <div class="form-group">
      <label>订单ID:</label>
      <input v-model="ticketForm.orderId" placeholder="例如: ORDER001" />
    </div>
    
    <div class="form-group">
      <label>餐票类型ID:</label>
      <input v-model="ticketForm.typeId" placeholder="例如: TYPE001" />
    </div>
    
    <div class="form-group">
      <label>数量:</label>
      <input v-model.number="ticketForm.quantity" type="number" min="1" placeholder="例如: 10" />
    </div>
    
    <div class="form-group">
      <label>档口ID:</label>
      <input v-model="ticketForm.stallId" placeholder="例如: STALL001" />
    </div>
    
    <div class="button-group">
      <button @click="generateTickets" :disabled="loading">生成餐票</button>
      <button @click="resetForm" :disabled="loading">取消</button>
    </div>
    
    <div v-if="loading" class="loading">处理中...</div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div v-if="tickets.length > 0" class="tickets-result">
      <h2>生成的餐票 (共 {{ tickets.length }} 张)</h2>
      <div class="tickets-grid">
        <div v-for="ticket in tickets" :key="ticket.tickerId" class="ticket-card">
          <p><strong>餐票ID:</strong> {{ ticket.tickerId }}</p>
          <p><strong>餐票编号:</strong> {{ ticket.ticketNumber }}</p>
          <p><strong>类型:</strong> {{ ticket.typename }}</p>
          <p><strong>订单ID:</strong> {{ ticket.orderId }}</p>
          <p><strong>状态:</strong> {{ ticket.staus === 0 ? '未使用' : '已使用' }}</p>
          <p><strong>创建时间:</strong> {{ formatDate(ticket.createDatetime) }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  setup() {
    // 表单数据
    const ticketForm = ref({
      orderId: 'ORDER001',
      typeId: 'TYPE001',
      quantity: 10,
      stallId: 'STALL001'
    });
    
    // 状态变量
    const tickets = ref([]);
    const loading = ref(false);
    const errorMessage = ref('');
    
    // 生成餐票
    const generateTickets = async () => {
      // 简单验证
      if (!ticketForm.value.orderId || !ticketForm.value.typeId || !ticketForm.value.quantity || !ticketForm.value.stallId) {
        errorMessage.value = '请填写所有必填字段';
        return;
      }
      
      if (ticketForm.value.quantity < 1) {
        errorMessage.value = '数量必须大于0';
        return;
      }
      
      try {
        loading.value = true;
        errorMessage.value = '';
        tickets.value = [];
        
        // 调用后端API生成餐票
        const response = await axios.post('/api/tickets/create', ticketForm.value);
        
        if (response.data.code === '200' && response.data.data) {
          tickets.value = response.data.data;
        } else {
          errorMessage.value = response.data.message || '生成餐票失败';
        }
      } catch (error) {
        console.error('生成餐票出错:', error);
        errorMessage.value = '生成餐票时发生错误，请重试';
      } finally {
        loading.value = false;
      }
    };
    
    // 重置表单
    const resetForm = () => {
      ticketForm.value = {
        orderId: 'ORDER001',
        typeId: 'TYPE001',
        quantity: 10,
        stallId: 'STALL001'
      };
      tickets.value = [];
      errorMessage.value = '';
    };
    
    // 格式化日期
    const formatDate = (datetime) => {
      if (!datetime) return '';
      // 假设后端返回的是yyyyMMddHHmmss格式
      const year = datetime.substring(0, 4);
      const month = datetime.substring(4, 6);
      const day = datetime.substring(6, 8);
      const hour = datetime.substring(8, 10);
      const minute = datetime.substring(10, 12);
      const second = datetime.substring(12, 14);
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    };
    
    return {
      ticketForm,
      tickets,
      loading,
      errorMessage,
      generateTickets,
      resetForm,
      formatDate
    };
  }
};
</script>

<style scoped>
.ticket-generator {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: inline-block;
  width: 120px;
  font-weight: bold;
}

.form-group input {
  padding: 8px;
  width: 300px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.button-group {
  margin: 20px 0;
}

.button-group button {
  padding: 10px 20px;
  margin-right: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.button-group button:first-child {
  background-color: #4CAF50;
  color: white;
}

.button-group button:last-child {
  background-color: #f44336;
  color: white;
}

.button-group button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.loading {
  color: #666;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.error-message {
  color: #dc3545;
  padding: 10px;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  margin: 10px 0;
}

.tickets-result {
  margin-top: 20px;
}

.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.ticket-card {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #f9f9f9;
}

.ticket-card p {
  margin: 5px 0;
  font-size: 14px;
}
</style>