<template>
  <div class="ticket-creator">
    <h1>餐票批量创建工具</h1>
    
    <!-- 表单区域 -->
    <form @submit.prevent="createTickets" class="ticket-form">
      <div class="form-group">
        <label for="orderNumber">订单编号</label>
        <input 
          type="text" 
          id="orderNumber" 
          v-model="formData.orderNumber" 
          required
          placeholder="请输入订单编号"
        >
      </div>
      
      <div class="form-group">
        <label for="quantity">餐票数量</label>
        <input 
          type="number" 
          id="quantity" 
          v-model="formData.quantity" 
          required
          min="1"
          placeholder="请输入要创建的餐票数量"
        >
      </div>
      
      <div class="form-group">
        <label for="expirationTime">过期时间</label>
        <input 
          type="date" 
          id="expirationTime" 
          v-model="formData.expirationTime" 
          required
        >
      </div>
      
      <div class="form-group">
        <label for="staus">状态</label>
        <select id="staus" v-model="formData.staus" required>
          <option value="0">未使用</option>
          <option value="1">已取消</option>
          <option value="2">已过期</option>
          <option value="3">已使用</option>
        </select>
      </div>
      
      <div class="form-group">
        <label for="fkCreator">创建人ID</label>
        <input 
          type="text" 
          id="fkCreator" 
          v-model="formData.fkCreator" 
          required
          placeholder="请输入创建人ID"
        >
      </div>
      
      <button type="submit" class="create-btn" :disabled="isLoading">
        <span v-if="!isLoading">创建餐票</span>
        <span v-if="isLoading">创建中...</span>
      </button>
    </form>
    
    <!-- 结果提示 -->
    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>
    
    <!-- 餐票列表 滚动容器 -->
    <div v-if="createdTickets.length>0"  class="result-section" >
      <h2>创建成功的餐票</h2>
      <!-- 新增滚动容器 -->
      <div class="ticket-scroll-container">
        <div class="ticket-grid">
          <!-- 餐票卡片 -->
          <div class="ticket-card" v-for="(ticket, index) in createdTickets" :key="ticket.tickerId || index">
            <div class="ticket-body">
              <div class="ticket-info">
                <p class="ticket-main-number">{{ ticket.ticketNumber || '未知编号' }}</p>
                <p class="ticket-type"><strong>名称:</strong> {{ ticket.typename || '未指定' }}</p>
                <p><strong>地点:</strong> {{ ticket.canteenName || '未指定' }}</p>
                <p><strong>状态:</strong> {{ formatStatus(ticket.staus) }}</p>
                <p class="expiration-wrap"><strong>有效期:</strong> {{ formatDate(ticket.createDatetime) }} - {{ formatExpirationDate(ticket.expirationTime) }}</p>
              </div>
              
              <!-- 二维码区域 -->
              <div class="ticket-qrcode">
                <qrcode-vue 
                  v-if="ticket.ticketNumber"
                  :value="ticket.ticketNumber" 
                  :size="100" 
                  level="H"
                  @click="openQrDetail(ticket.ticketNumber)"
                  class="qrcode-clickable"
                ></qrcode-vue>
                <p v-else class="qrcode-placeholder">无法生成二维码（缺少餐票编号）</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else-if="showResult && createdTickets.length === 0" class="empty-result">
      <p>创建成功，但未返回餐票数据</p>
    </div>
    
    <!-- 二维码扫描详情弹窗 -->
    <teleport to="body">
      <div v-if="showQrDetail" class="qr-detail-modal">
        <div class="modal-overlay" @click="closeQrDetail"></div>
        <div class="modal-content">
          <div class="modal-header">
            <h2>餐票详情</h2>
            <button class="close-btn" @click="closeQrDetail">&times;</button>
          </div>
          
          <div class="ticket-detail" v-if="qrTicket">
          
  <div class="status-badge" :class="getStatusClass(qrTicket.staus)">
    {{ formatStatus(qrTicket.staus) }}
    <span v-if="isExpired(qrTicket.expirationTime)" class="expired-tag">已过期</span>
  </div>
  
  <div class="ticket-info-grid">
    <div class="info-item">
      <label>餐票编号</label>
      <p class="ticket-number">{{ qrTicket.ticketNumber }}</p>
    </div>
    
    <div class="info-item">
      <label>餐票名称</label>
      <p>{{ qrTicket.typename || '未知类型' }}</p>
    </div>
    
    <div class="info-item">
      <label>使用地点</label>
      <p>{{ qrTicket.canteenName || '未知食堂' }}</p>
    </div>
    
    <div class="info-item">
      <label>创建时间</label>
      <p>{{ formatDate(qrTicket.createDatetime) }}</p>
    </div>
    
    <div class="info-item">
      <label>有效期限</label>
      <p :class="isExpired(qrTicket.expirationTime) ? 'text-expired' : ''">
        {{ formatExpirationDate(qrTicket.expirationTime) }}
      </p>
    </div>

    <div class="info-item">
      <label>创建IP</label>
      <p>{{ qrTicket.createIP || '未知' }}</p>
    </div>
  </div>
  
  <!-- 操作按钮区域 -->
  <div class="verify-actions">
    <!-- 未使用状态 - 可以标记为已使用 -->
    <button 
      v-if="canMarkAsUsed(qrTicket)" 
      class="use-btn" 
      @click="markAsUsed(qrTicket.tickerId)"
    >
      标记为已使用
    </button>
    
    <!-- 已使用状态 - 可以核销 -->
    <button 
      v-if="canVerify(qrTicket)" class="verify-btn" @click="verifyTicket(qrTicket.tickerId)">
      确认核销此餐票
    </button>
  </div>
</div>
          
          <div v-else class="empty-state">
            <p>未查询到餐票信息</p>
            <button class="refresh-btn" @click="fetchTicketByNumber(qrCodeValue)">
              重新加载
            </button>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import QrcodeVue from 'qrcode.vue';


// 后端基础路径 - 根据实际后端地址修改
const API_BASE_URL = 'http://localhost:8081/api';

// 表单数据
const formData = ref({
  orderNumber: '',
  quantity: 1,
  expirationTime: '',
  staus: '0',
  fkCreator: ''
});

// 状态管理
const isLoading = ref(false);
const message = ref('');
const messageType = ref('');
let createdTickets = ref([]);
const showResult = ref(false);

// 二维码详情相关状态
const showQrDetail = ref(false);
const qrTicket = ref(null);
const qrCodeValue = ref('');

// 格式化状态显示
const formatStatus = (staus) => {
  const stausMap = {
    '0': '未使用',
    '1': '已取消',
    '2': '已过期',
    '3': '已使用',
    '99': '已核销'
  };
  return stausMap[staus] || '未知状态';
};

// 获取状态样式类
const getStatusClass = (staus) => {
  const classMap = {
    '0': 'staus-active',
    '1': 'staus-canceled',
    '2': 'staus-expired',
    '3': 'staus-used',
    '99': 'staus-verified'
  };
  return classMap[staus] || 'staus-unknown';
};

// 格式化创建时间 - 适配后端yyyyMMddHHmmss格式
const formatDate = (datetime) => {
  if (!datetime) return '未知（无数据）';
  try {
    // 强制转换为字符串，避免类型问题
    const str = String(datetime);
    if (str.length === 14) {
      const year = str.slice(0, 4);
      const month = str.slice(4, 6);
      const day = str.slice(6, 8);
      const hour = str.slice(8, 10);
      const minute = str.slice(10, 12);
      const second = str.slice(12, 14);
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    } else if (str.length === 8) {
      // 处理仅日期的情况
      const year = str.slice(0, 4);
      const month = str.slice(4, 6);
      const day = str.slice(6, 8);
      return `${year}-${month}-${day}`;
    }
    return new Date(datetime).toLocaleString('zh-CN');
  } catch (error) {
    console.error('时间格式化失败:', datetime, error);
    return `格式错误: ${datetime}`;
  }
};

// 格式化过期时间（兼容后端 yyyyMMdd 格式）
const formatExpirationDate = (expirationTime) => {
  if (!expirationTime) return '未知';
  try {
    if (typeof expirationTime === 'string' && expirationTime.length === 8) {
      const year = expirationTime.slice(0, 4);
      const month = expirationTime.slice(4, 6);
      const day = expirationTime.slice(6, 8);
      return `${year}-${month}-${day}`;
    } else if (expirationTime.length === 14) {
      // 处理包含时间的过期格式
      const year = expirationTime.slice(0, 4);
      const month = expirationTime.slice(4, 6);
      const day = expirationTime.slice(6, 8);
      const hour = expirationTime.slice(8, 10);
      const minute = expirationTime.slice(10, 12);
      return `${year}-${month}-${day} ${hour}:${minute}`;
    }
    return new Date(expirationTime).toLocaleString('zh-CN');
  } catch (error) {
    console.error('日期格式化失败:', expirationTime, error);
    return expirationTime;
  }
};

// 检查是否过期
const isExpired = (expirationTime) => {
  if (!expirationTime) return false;
  try {
    let expireDate;
    if (typeof expirationTime === 'string') {
      if (expirationTime.length === 8) {
        // 处理 yyyyMMdd 格式
        const year = parseInt(expirationTime.slice(0, 4));
        const month = parseInt(expirationTime.slice(4, 6)) - 1; // 月份从0开始
        const day = parseInt(expirationTime.slice(6, 8));
        expireDate = new Date(year, month, day, 23, 59, 59); // 当天结束时间
      } else if (expirationTime.length === 14) {
        // 处理 yyyyMMddHHmmss 格式
        const year = parseInt(expirationTime.slice(0, 4));
        const month = parseInt(expirationTime.slice(4, 6)) - 1;
        const day = parseInt(expirationTime.slice(6, 8));
        const hour = parseInt(expirationTime.slice(8, 10));
        const minute = parseInt(expirationTime.slice(10, 12));
        const second = parseInt(expirationTime.slice(12, 14));
        expireDate = new Date(year, month, day, hour, minute, second);
      } else {
        expireDate = new Date(expirationTime);
      }
    } else {
      expireDate = new Date(expirationTime);
    }
    return expireDate < new Date();
  } catch (e) {
    console.error('过期时间检查失败:', e);
    return true;
  }
};


const canMarkAsUsed = (ticket) => {
  if (!ticket) return false;
  return ticket.staus === '0' && !isExpired(ticket.expirationTime);
};

const canVerify = (ticket) => {
  if (!ticket) return false;
  return ticket.staus === '3' && !isExpired(ticket.expirationTime);
};

// 根据编号查询餐票详情
const fetchTicketByNumber = async (ticketNumber) => {
  if (!ticketNumber) return;
  try {
    const response = await axios.get(`${API_BASE_URL}/tickets/check`, {
      params: { ticketNumber }
    });
  
    if (response.data && Number(response.data.code) === 200) {
      if (response.data.data.valid && response.data.data.ticket) {
        qrTicket.value = response.data.data.ticket;
        console.log("获取到的餐票详情:", qrTicket.value);
      } else {
        qrTicket.value = null;
        message.value = response.data.data.message || '餐票信息无效';
        messageType.value = 'error';
      }
    } else {
      qrTicket.value = null;
      message.value = response.data?.msg || '查询失败';
      messageType.value = 'error';
    }
  } catch (error) {
    console.error('查询餐票失败:', error);
    qrTicket.value = null;
    message.value = '网络错误，无法查询餐票信息';
    messageType.value = 'error';
  }
};

// 标记为已使用
const markAsUsed = async (tickerId) => {
  if (!tickerId || !confirm('确认要将此餐票标记为已使用吗？')) return;
  try {
    const response = await axios.post(`${API_BASE_URL}/tickets/use`, null, {
      params: {
        tickerId: tickerId,
        stallId: 'STALL001',  // 实际应用中应从登录状态或表单获取
        usedId: 'USER001'     // 实际应用中应从登录状态获取
      }
    });
    
    if (response.data && Number(response.data.code) === 200) {
      message.value = '餐票已标记为已使用';
      messageType.value = 'success';
      fetchTicketByNumber(qrTicket.value.ticketNumber);
      const index = createdTickets.value.findIndex(t => t.tickerId === tickerId);
      if (index !== -1) {
        createdTickets.value[index].staus = '3';
      }
    } else {
      message.value = response.data?.msg || '操作失败，餐票可能已被使用或不存在';
      messageType.value = 'error';
    }
  } catch (error) {
    console.error('标记餐票为已使用失败:', error);
    message.value = error.response?.data?.msg || '操作失败，请重试';
    messageType.value = 'error';
  }
};


// 核销餐票
const verifyTicket = async (tickerId) => {
  if (!tickerId || !confirm('确认要核销此餐票吗？')) return;
  try {
    const response = await axios.post(`${API_BASE_URL}/tickets/${tickerId}/verify`, {
      checkId: 'USER005'   // 实际应用中应从登录状态获取
    });
    
    // 处理响应
    if (response.data) {
      message.value = '餐票核销成功';
      messageType.value = 'success';
      // 重新获取最新状态
      fetchTicketByNumber(qrTicket.value.ticketNumber);
      // 更新列表中的状态为99（已核销）
      const index = createdTickets.value.findIndex(t => t.tickerId === tickerId);
      if (index !== -1) {
        createdTickets.value[index].staus = '99';
      }
    } else {
      message.value = '核销失败，餐票可能已被使用或不存在';
      messageType.value = 'error';
    }
  } catch (error) {
    console.error('核销餐票失败:', error);
    message.value = error.response?.data?.msg || '核销失败，请重试';
    messageType.value = 'error';
  }
};

// 打开/关闭详情弹窗
const openQrDetail = (ticketNumber) => {
  qrCodeValue.value = ticketNumber;
  showQrDetail.value = true;
  fetchTicketByNumber(ticketNumber);
};

const closeQrDetail = () => {
  showQrDetail.value = false;
  qrTicket.value = null;
  qrCodeValue.value = '';
};

// 创建餐票方法
const createTickets = async () => {
  message.value = '';
  createdTickets.value = [];
  isLoading.value = true;
  showResult.value = false;

  try {
    // 日期转换 - 转换为后端需要的yyyyMMdd格式
    const formattedDate = formData.value.expirationTime 
      ? formData.value.expirationTime.replace(/-/g, '') 
      : '';

    // 请求数据
    const requestData = {
      orderNumber: formData.value.orderNumber,
      quantity: formData.value.quantity,
      expirationTime: formattedDate,
      staus: formData.value.staus,
      fkCreator: formData.value.fkCreator
    };

    // 发送请求
    const response = await axios.post(
      `${API_BASE_URL}/tickets/create`,
      requestData
    );

    // 解析后端返回的Result对象
    console.log('后端完整响应:', response.data); 

    if (response.data && Number(response.data.code) === 200) {
      messageType.value = 'success';
      message.value = response.data.msg || '餐票创建成功';
      // 直接赋值后端返回的data数组
      createdTickets.value = response.data.data || []; 
      showResult.value = true;
    } else {
      messageType.value = 'error';
      message.value = response.data?.msg || '餐票创建失败（后端返回异常）';
      showResult.value = true;
    }
  } 
  

  
  catch (error) {
    messageType.value = 'error';
    message.value = `请求失败: ${error.response?.data?.msg || error.message || '未知错误'}`;
    console.error('创建餐票出错:', error);
    showResult.value = true;
  } finally {
    isLoading.value = false;
  }
};

// 初始化日期为7天后
const initDate = () => {
  const date = new Date();
  date.setDate(date.getDate() + 7);
  formData.value.expirationTime = date.toISOString().split('T')[0];
};
initDate();

</script>


<style scoped>
.ticket-creator {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

.ticket-form {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
}

input, select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.create-btn {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  transition: background-color 0.3s;
}

.create-btn:hover:not(:disabled) {
  background-color: #359e75;
}

.create-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.message {
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: bold;
}

.message.success {
  background-color: #dff0d8;
  color: #3c763d;
  border: 1px solid #d6e9c6;
}

.message.error {
  background-color: #f2dede;
  color: #a94442;
  border: 1px solid #ebccd1;
}


h2 {
  color: #333;
  margin-bottom: 15px;
  text-align: center;
}

.ticket-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
.result-section {
  padding: 20px;
  background: #fff;
}

/* 仅添加的滚动容器样式 */
.ticket-scroll-container {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

.ticket-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}
.ticket-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.ticket-body {
  padding: 12px;
  display: flex;
  flex-direction: row;  
  align-items: flex-start; 
  gap: 10px; /* 缩小间距 */
  justify-content: space-between; 
}

.ticket-info {
  flex: 1; /* 让信息区占满剩余空间 */
}

.ticket-main-number {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
}

/* 加粗餐票类型 */
.ticket-type {
  font-size: 15px;
  font-weight: bold;
  margin: 6px 0;
}

.ticket-info p {
  margin: 4px 0; /* 缩小行间距 */
  color: #555;
  font-size: 14px;
  line-height: 1.4;
}

/* 让有效期行自然延伸 */
.expiration-wrap {
  white-space: normal; 
  word-wrap: break-word;
}

.ticket-info strong {
  color: #333;
  min-width: 50px;
  display: inline-block;
}


.ticket-qrcode {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 5px; /* 缩小内边距 */
  justify-content: flex-start; /* 顶部对齐 */
}

.qrcode-clickable {
  cursor: pointer;
  transition: transform 0.2s;
}

.qrcode-clickable:hover {
  transform: scale(1.05);
}

.qrcode-placeholder {
  color: #999;
  font-size: 12px;
  text-align: center;
  padding: 15px;
  border: 1px dashed #ddd;
  border-radius: 4px;
}

.empty-result {
  text-align: center;
  color: #999;
  margin-top: 20px;
  padding: 30px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

/* 二维码详情弹窗样式 */
.qr-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background-color: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0 10px;
}

.ticket-detail {
  padding: 20px;
}

.status-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  position: relative;
}

.staus-active {
  background-color: #42b983;
  color: white;
}

.staus-canceled {
  background-color: #f39c12;
  color: white;
}

.staus-expired {
  background-color: #e74c3c;
  color: white;
}

.staus-verified {
  background-color: #9b59b6;
  color: white;
}

.staus-used {
  background-color: #3498db;
  color: white;
}
.staus-unknown {
  background-color: #95a5a6;
  color: white;
}

.expired-tag {
  margin-left: 10px;
  background-color: rgba(255, 255, 255, 0.3);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.ticket-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.info-item label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
  display: block;
}

.info-item p {
  font-size: 16px;
  margin: 0;
  word-break: break-all;
}

.text-expired {
  color: #e74c3c;
  font-weight: bold;
}

.use-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 10px;
}

.use-btn:hover {
  background-color: #2980b9;
}

.verify-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.verify-btn:hover {
  background-color: #c0392b;
}

.verify-actions {
  padding-top: 16px;
  border-top: 1px solid #eee;
  text-align: center;
}

.verify-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.verify-btn:hover {
  background-color: #c0392b;
}

.refresh-btn {
  margin-top: 16px;
  padding: 8px 16px;
  border: 1px solid #42b983;
  background-color: white;
  color: #42b983;
  border-radius: 4px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .ticket-grid {
    grid-template-columns: 1fr;
  }
  
  .ticket-body {
    flex-direction: column;
    align-items: center;
  }

  .ticket-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>