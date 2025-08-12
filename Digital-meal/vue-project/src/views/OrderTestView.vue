<template>
  <div class="order-test-container">
    <h1>数字餐票系统测试页面</h1>
    
    <!-- 系统状态检查 -->
    <div class="section">
      <h2>🔍 系统状态检查</h2>
      <div class="button-group">
        <button @click="checkHealth" :disabled="loading">检查系统健康状态</button>
        <button @click="getCurrentTime" :disabled="loading">获取系统时间</button>
      </div>
      <div v-if="systemStatus" class="status-info">
        <p><strong>状态:</strong> {{ systemStatus }}</p>
      </div>
    </div>

    <!-- 餐票类型查询 -->
    <div class="section">
      <h2>🎫 餐票类型管理</h2>
      <button @click="loadTicketTypes" :disabled="loading">获取所有餐票类型</button>
      <div v-if="ticketTypes.length > 0" class="ticket-types">
        <h3>可用餐票类型:</h3>
        <div class="ticket-grid">
          <div v-for="ticket in ticketTypes" :key="ticket.typeId" class="ticket-card">
            <h4>{{ ticket.typeName }}</h4>
            <p>价格: ¥{{ (ticket.value / 100).toFixed(2) }}</p>
            <p>类型ID: {{ ticket.typeId }}</p>
            <p>食堂ID: {{ ticket.canteenId }}</p>
            <p>状态: {{ ticket.status === 1 ? '可用' : '不可用' }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建订单 -->
    <div class="section">
      <h2>📝 创建订单</h2>
      <div class="form-group">
        <label>用户ID:</label>
        <input v-model="orderForm.userId" placeholder="默认: U002" />
      </div>
      <div class="form-group">
        <label>机构ID:</label>
        <input v-model="orderForm.agenciesId" placeholder="默认: A001" />
      </div>
      
      <!-- 订单项 -->
      <div class="order-items">
        <h3>订单项:</h3>
        <div v-for="(item, index) in orderForm.orderItems" :key="index" class="order-item">
          <select v-model="item.typeId" @change="updateItemInfo(index)">
            <option value="">选择餐票类型</option>
            <option v-for="ticket in ticketTypes" :key="ticket.typeId" :value="ticket.typeId">
              {{ ticket.typeName }} - ¥{{ (ticket.value / 100).toFixed(2) }}
            </option>
          </select>
          <input v-model.number="item.amount" type="number" placeholder="数量" min="1" />
          <button @click="removeOrderItem(index)" class="remove-btn">删除</button>
        </div>
        <button @click="addOrderItem" class="add-btn">添加订单项</button>
      </div>

      <div class="form-group">
        <label>
          <input type="checkbox" v-model="orderForm.autoSubmit" />
          自动提交订单
        </label>
      </div>

      <div class="button-group">
        <button @click="createOrder" :disabled="loading || orderForm.orderItems.length === 0">
          {{ orderForm.autoSubmit ? '创建并提交订单' : '创建草稿订单' }}
        </button>
        <button @click="smartStartOrder" :disabled="loading">智能开始订单</button>
      </div>
    </div>

    <!-- 草稿订单管理 -->
    <div class="section" v-if="currentDraft">
      <h2>💾 草稿订单</h2>
      <div class="draft-info">
        <p><strong>草稿ID:</strong> {{ currentDraft.draftId }}</p>
        <p><strong>用户ID:</strong> {{ currentDraft.userId }}</p>
        <p><strong>状态:</strong> {{ getDraftStatusText(currentDraft.status) }}</p>
        <p><strong>创建时间:</strong> {{ currentDraft.createDatetime }}</p>
        <div v-if="currentDraft.orderItems && currentDraft.orderItems.length > 0">
          <h4>草稿订单项:</h4>
          <ul>
            <li v-for="item in currentDraft.orderItems" :key="item.typeId">
              类型ID: {{ item.typeId }}, 数量: {{ item.amount }}
            </li>
          </ul>
        </div>
        <button @click="submitDraft" :disabled="loading">提交草稿订单</button>
      </div>
    </div>

    <!-- 订单查询 -->
    <div class="section">
      <h2>📊 订单查询</h2>
      <div class="button-group">
        <button @click="loadAllOrders" :disabled="loading">获取所有订单</button>
        <button @click="loadUserOrders" :disabled="loading">获取我的订单</button>
      </div>
      
      <div v-if="orders.length > 0" class="orders-list">
        <h3>订单列表 ({{ orders.length }} 条):</h3>
        <div v-for="order in orders" :key="order.orderId" class="order-card">
          <div class="order-header">
            <h4>订单 #{{ order.orderNumber }}</h4>
            <span class="order-status">{{ getOrderStatusText(order.status) }}</span>
          </div>
          <div class="order-details">
            <p><strong>订单ID:</strong> {{ order.orderId }}</p>
            <p><strong>用户ID:</strong> {{ order.userId }}</p>
            <p><strong>机构ID:</strong> {{ order.agenciesId }}</p>
            <p><strong>创建时间:</strong> {{ order.createDatetime }}</p>
            <p><strong>过期时间:</strong> {{ order.expirationTime }}</p>
            <p><strong>建议:</strong> {{ order.suggestion || '无' }}</p>
          </div>
          <button @click="loadOrderItems(order.orderId)" :disabled="loading">
            查看订单项
          </button>
          
          <!-- 订单项详情 -->
          <div v-if="orderItems[order.orderId]" class="order-items-detail">
            <h5>订单项详情:</h5>
            <div v-for="item in orderItems[order.orderId]" :key="item.typeId" class="item-detail">
              <p><strong>{{ item.typeName }}</strong></p>
              <p>数量: {{ item.amount }}</p>
              <p>单价: ¥{{ (item.value / 100).toFixed(2) }}</p>
              <p>小计: ¥{{ ((item.amount * item.value) / 100).toFixed(2) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 响应信息显示 -->
    <div class="section" v-if="responseMessage">
      <h2>📋 响应信息</h2>
      <div class="response-message" :class="responseType">
        <pre>{{ responseMessage }}</pre>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <p>处理中...</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'OrderTestView',
  data() {
    return {
      loading: false,
      systemStatus: '',
      ticketTypes: [],
      orders: [],
      orderItems: {},
      currentDraft: null,
      responseMessage: '',
      responseType: 'success',
      
      // 订单表单
      orderForm: {
        userId: 'U002',
        agenciesId: 'A001',
        autoSubmit: true,
        orderItems: [
          { typeId: '', amount: 1 }
        ]
      }
    }
  },
  
  mounted() {
    // 页面加载时自动检查系统状态和加载餐票类型
    this.checkHealth()
    this.loadTicketTypes()
  },
  
  methods: {
    // API基础配置
    getApiUrl(path) {
      return `http://localhost:8081/api${path}`
    },
    
    async apiCall(method, url, data = null) {
      this.loading = true
      this.responseMessage = ''
      
      try {
        const config = {
          method,
          url: this.getApiUrl(url),
          headers: {
            'Content-Type': 'application/json'
          }
        }
        
        if (data) {
          config.data = data
        }
        
        const response = await axios(config)
        this.showResponse('success', response.data)
        return response.data
      } catch (error) {
        const errorMsg = error.response?.data || error.message
        this.showResponse('error', errorMsg)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    showResponse(type, data) {
      this.responseType = type
      this.responseMessage = JSON.stringify(data, null, 2)
    },
    
    // 系统状态检查
    async checkHealth() {
      try {
        const result = await this.apiCall('GET', '/test/health')
        this.systemStatus = result.data
      } catch (error) {
        this.systemStatus = '系统连接失败'
      }
    },
    
    async getCurrentTime() {
      await this.apiCall('GET', '/test/time')
    },
    
    // 餐票类型管理
    async loadTicketTypes() {
      try {
        const result = await this.apiCall('GET', '/ticket-types')
        this.ticketTypes = result.data || []
      } catch (error) {
        console.error('加载餐票类型失败:', error)
      }
    },
    
    // 订单项管理
    addOrderItem() {
      this.orderForm.orderItems.push({ typeId: '', amount: 1 })
    },
    
    removeOrderItem(index) {
      this.orderForm.orderItems.splice(index, 1)
    },
    
    updateItemInfo(index) {
      // 当选择餐票类型时，可以在这里添加额外逻辑
      const item = this.orderForm.orderItems[index]
      const ticket = this.ticketTypes.find(t => t.typeId === item.typeId)
      if (ticket) {
        // 可以设置默认数量或其他逻辑
      }
    },
    
    // 订单创建
    async createOrder() {
      try {
        const orderData = {
          userId: this.orderForm.userId || 'U002',
          agenciesId: this.orderForm.agenciesId || 'A001',
          autoSubmit: this.orderForm.autoSubmit,
          orderItems: this.orderForm.orderItems.filter(item => item.typeId && item.amount > 0)
        }
        
        const result = await this.apiCall('POST', '/orders', orderData)
        
        if (result.data.status === 'draft') {
          this.currentDraft = result.data.draft
        } else if (result.data.status === 'submitted') {
          this.currentDraft = null
          // 刷新订单列表
          this.loadAllOrders()
        }
      } catch (error) {
        console.error('创建订单失败:', error)
      }
    },
    
    async smartStartOrder() {
      try {
        const userId = this.orderForm.userId || 'U002'
        const result = await this.apiCall('POST', `/orders/smart-start/${userId}`)
        
        if (result.data.draft) {
          this.currentDraft = result.data.draft
        }
      } catch (error) {
        console.error('智能开始订单失败:', error)
      }
    },
    
    async submitDraft() {
      if (!this.currentDraft) return
      
      try {
        const result = await this.apiCall('POST', `/orders/draft/${this.currentDraft.draftId}/submit`)
        this.currentDraft = null
        // 刷新订单列表
        this.loadAllOrders()
      } catch (error) {
        console.error('提交草稿失败:', error)
      }
    },
    
    // 订单查询
    async loadAllOrders() {
      try {
        const result = await this.apiCall('GET', '/orders')
        this.orders = result.data || []
      } catch (error) {
        console.error('加载订单失败:', error)
      }
    },
    
    async loadUserOrders() {
      // 这里可以添加按用户查询的逻辑
      await this.loadAllOrders()
    },
    
    async loadOrderItems(orderId) {
      try {
        const result = await this.apiCall('GET', `/ott/order/${orderId}`)
        this.orderItems[orderId] = result.data || []
      } catch (error) {
        console.error('加载订单项失败:', error)
      }
    },
    
    // 辅助方法
    getOrderStatusText(status) {
      const statusMap = {
        1: '有效',
        2: '已使用',
        3: '已过期',
        0: '无效'
      }
      return statusMap[status] || '未知状态'
    },
    
    getDraftStatusText(status) {
      const statusMap = {
        1: '草稿中',
        2: '已提交',
        0: '已取消'
      }
      return statusMap[status] || '未知状态'
    }
  }
}
</script>

<style scoped>
.order-test-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border-left: 4px solid #007bff;
}

.section h2 {
  color: #495057;
  margin-bottom: 15px;
  font-size: 1.3em;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background: #0056b3;
}

button:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.add-btn {
  background: #28a745;
}

.add-btn:hover:not(:disabled) {
  background: #1e7e34;
}

.remove-btn {
  background: #dc3545;
  padding: 5px 10px;
  font-size: 12px;
}

.remove-btn:hover:not(:disabled) {
  background: #c82333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #495057;
}

.form-group input, select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input[type="checkbox"] {
  width: auto;
  margin-right: 8px;
}

.status-info {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
}

.ticket-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.ticket-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.ticket-card h4 {
  margin: 0 0 10px 0;
  color: #007bff;
}

.ticket-card p {
  margin: 5px 0;
  font-size: 14px;
  color: #6c757d;
}

.order-items {
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 15px;
  margin: 15px 0;
  background: white;
}

.order-item {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.order-item select, .order-item input {
  flex: 1;
  min-width: 150px;
}

.draft-info {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  padding: 15px;
}

.orders-list {
  margin-top: 20px;
}

.order-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.order-header h4 {
  margin: 0;
  color: #2c3e50;
}

.order-status {
  background: #007bff;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.order-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.order-details p {
  margin: 5px 0;
  font-size: 14px;
}

.order-items-detail {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.item-detail {
  background: #f8f9fa;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}

.item-detail p {
  margin: 3px 0;
  font-size: 13px;
}

.response-message {
  border-radius: 6px;
  padding: 15px;
  margin-top: 10px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
}

.response-message.success {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  color: #155724;
}

.response-message.error {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}

.loading {
  text-align: center;
  padding: 20px;
  background: #e3f2fd;
  border-radius: 6px;
  margin: 20px 0;
}

.loading p {
  margin: 0;
  color: #1976d2;
  font-weight: 500;
}

@media (max-width: 768px) {
  .order-test-container {
    padding: 10px;
  }

  .button-group {
    flex-direction: column;
  }

  .order-item {
    flex-direction: column;
    align-items: stretch;
  }

  .order-details {
    grid-template-columns: 1fr;
  }

  .ticket-grid {
    grid-template-columns: 1fr;
  }
}
</style>
