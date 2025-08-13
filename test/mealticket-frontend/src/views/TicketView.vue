<template>
  <div class="ticket-container">
    <h1>餐票生成</h1>

    <div v-if="ticket" class="ticket-card">
      <h2>餐票信息</h2>
      <p>编号: {{ ticket.code }}</p>
      <p>状态: {{ ticket.status === 0 ? '未使用' : '已使用' }}</p>
      <p>过期时间: {{ formatDate(ticket.expireTime) }}</p>

      <div class="qrcode-container">
        <qrcode-vue :value="ticket.code" :size="200" level="H" />
      </div>

      <button @click="generateNewTicket">生成新餐票</button>
    </div>

    <div v-else>
      <button @click="generateTicket">生成餐票</button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from 'axios'
import QrcodeVue from 'qrcode.vue'

export default {
  components: {
    QrcodeVue
  },
  setup() {
    const ticket = ref(null)

    const generateTicket = async () => {
      try {
        const response = await axios.post('http://localhost:8080/api/ticket/create', null, {
          params: {
            expireMinutes: 30
          }
        })
        ticket.value = response.data.data
      } catch (error) {
        console.error('生成餐票失败:', error)
        alert('生成餐票失败')
      }
    }

    const generateNewTicket = () => {
      ticket.value = null
      generateTicket()
    }

    const formatDate = (timestamp) => {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return date.toLocaleString()
    }

    return {
      ticket,
      generateTicket,
      generateNewTicket,
      formatDate
    }
  }
}
</script>

<style>
.ticket-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.ticket-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.qrcode-container {
  margin: 20px 0;
}

button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #3aa876;
}
</style>