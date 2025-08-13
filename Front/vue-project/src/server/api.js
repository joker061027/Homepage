import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8081/api/', // 指向Spring Boot后端
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error('请求失败:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);


export default {
  getOrders() {
    return apiClient.get('/orders');
  },
  createOrders(orders) {
    return apiClient.post('/orders', orders);
  },
  getStall() {
    return apiClient.get('/stall');
  },
  createStall(stall) {
    return apiClient.post('/stall', stall);
  },
  getOtt() {
    return apiClient.get('/ott');
  },
  createOtt(ott) {
    return apiClient.post('/ott', ott);
  },
  getAgencies() {
    return apiClient.get('/agencies');
  },
  createAgencies(agencies) {
    return apiClient.post('/agencies', agencies);
  },
  getTickets() {
    return apiClient.get('/tickets');
  },
  createTickets(ticket) {
    return apiClient.post('/tickets', ticket);
  },
  getUsers() {
    return apiClient.get('/users');
  },
  createUsers(users) {
    return apiClient.post('/users', users);
  },

  // 其他API方法...
};