import './assets/main.css';

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; 
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

const app = createApp(App);

app.use(router);

// 配置中文语言包
app.use(ElementPlus, {
  locale: zhCn
});

app.mount('#app');
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.use(ElementPlus,{
    locale: zhCn,
})
app.mount('#app')
