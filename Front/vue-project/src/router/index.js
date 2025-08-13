import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/homepage.vue'),
    },
  ],
})

router.beforeEach((to,from,next)=>{
    document.title=to.meta.title
    next()
  })

export default router
