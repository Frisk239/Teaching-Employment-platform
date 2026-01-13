/**
 * 高校教学就业平台 - 主入口文件
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 导入全局样式
import './styles/global.scss'

// 创建应用实例
const app = createApp(App)

// 创建 Pinia
const pinia = createPinia()

// 注册 Element Plus
app.use(ElementPlus, {
  locale: zhCn,
})

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册插件
app.use(router)
app.use(pinia)

// 挂载应用
app.mount('#app')
