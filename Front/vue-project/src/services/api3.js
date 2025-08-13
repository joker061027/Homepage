// api.js
import axios from 'axios';

// 餐票类型相关接口
const TICKET_TYPES_URL = 'http://localhost:8080/api/ticket-types';
// 食堂相关接口（新增）
const CANTEENS_URL = 'http://localhost:8080/api/canteen';

// 获取餐票类型列表
export const getTickets = async () => {
    try {
        const response = await axios.get(TICKET_TYPES_URL);
        if (response.data && response.data.code === "200" && Array.isArray(response.data.data)) {
            return response.data.data;
        } else {
            const errorMsg = response.data?.msg || '未知错误';
            throw new Error(`获取数据失败: ${errorMsg}`);
        }
    } catch (error) {
        console.error("Error fetching tickets:", error);
        throw error;
    }
};

// 新增：获取食堂列表（从数据库动态获取）
export const getCanteens = async () => {
  try {
    const response = await axios.get(CANTEENS_URL);

    // 处理实际API返回格式
    if (Array.isArray(response.data)) {
      return response.data; // 直接返回数组
    }

    // 兼容旧格式（如果存在）
    if (response.data && response.data.code === 200 && Array.isArray(response.data.data)) {
      return response.data.data;
    }

    throw new Error('获取食堂列表失败：无数据');
  } catch (error) {
    console.error("Error fetching canteens:", error);
    throw new Error(error.message || '获取食堂列表失败：网络错误');
  }
};

// 新增餐票类型
export const createTicketType = async (ticketData) => {
  try {
    // 分离食堂ID和其他数据
    const { canteenIds, ...restData } = ticketData;

    // 创建URL参数
    const params = new URLSearchParams();
    canteenIds.forEach(id => params.append('canteenIds', id));

    // 发送请求
    const response = await axios.post(TICKET_TYPES_URL, restData, {
      params
    });

    if (response.data && response.data.code === "200") {
      return response.data.data;
    } else {
      throw new Error(response.data?.msg || "新增失败");
    }
  } catch (error) {
    throw new Error(error.response?.data?.msg || "网络异常");
  }
};


// 更新餐票类型接口调整（确保使用typeId）
export const updateTicket = async (ticketData) => {
  try {
    const { typeId, canteenIds, ...restData } = ticketData;

    const params = new URLSearchParams();
    canteenIds.forEach(id => params.append('canteenIds', id));

    const response = await axios.put(
      `${TICKET_TYPES_URL}/${typeId}`,
      restData,
      { params }
    );

    if (response.data && response.data.code === "200") {
      return response.data.data;
    } else {
      throw new Error(response.data?.msg || "更新失败");
    }
  } catch (error) {
    throw new Error(error.response?.data?.msg || "网络异常");
  }
};

// 新增分页获取餐票类型接口
export const getTicketsByPage = async (pageNum = 1, pageSize = 5) => {
    try {
        const response = await axios.get(`${TICKET_TYPES_URL}/page`, {
            params: {
                pageNum,
                pageSize
            }
        });
        if (response.data && response.data.code === "200" && response.data.data) {
            return response.data.data;
        } else {
            const errorMsg = response.data?.msg || '未知错误';
            throw new Error(`获取数据失败: ${errorMsg}`);
        }
    } catch (error) {
        console.error("Error fetching tickets by page:", error);
        throw error;
    }
};

// 状态切换接口（修正URL和参数）
export const toggleTicketStatus = async (typeId, status) => {
  try {
    // 使用正确的URL和参数格式
    const response = await axios.post(
      `${TICKET_TYPES_URL}/${typeId}/toggle-status`,
      null,  // 没有请求体
      {
        params: { status }  // 作为查询参数传递
      }
    );

    if (response.data && response.data.code === "200") {
      return response.data;
    } else {
      throw new Error(response.data?.msg || "状态切换失败");
    }
  } catch (error) {
    throw new Error(error.response?.data?.msg || "网络异常，状态切换失败");
  }
};
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
