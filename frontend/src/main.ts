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

// 导入权限指令
import { permissionDirective, roleDirective } from './utils/permission'

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

// 注册权限指令
app.directive('permission', permissionDirective)
app.directive('role', roleDirective)

// 注册插件
app.use(router)
app.use(pinia)

// 在应用挂载前恢复登录状态
import { useAuthStore } from '@/stores/auth'

// 恢复登录状态
const initAuth = async () => {
  const authStore = useAuthStore()
  await authStore.restoreAuth()
}

// 挂载应用
initAuth().then(() => {
  app.mount('#app')
})
