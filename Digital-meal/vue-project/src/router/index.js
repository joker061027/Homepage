import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/HomeView.vue'
import TicketManagement from '../views/TicketManagement.vue'

const routes = [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/ticket-management',
      name: 'ticket-management',
      component: TicketManagement,
    },
  ]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router