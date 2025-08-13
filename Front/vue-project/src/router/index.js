import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/HomeView.vue'
import TicketManagement from '../views/TicketManagement.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/ticketone', name: 'ticketone', component: () => import('../views/ticketone.vue'),},
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/homepage.vue'),
    },
  ],
})

export default router
