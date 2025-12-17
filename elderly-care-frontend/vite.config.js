import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    open: true, // 自动打开浏览器
    proxy: {
      // 可以在这里配置API代理
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 可以在这里添加全局的SCSS变量和mixins
      }
    }
  }
})