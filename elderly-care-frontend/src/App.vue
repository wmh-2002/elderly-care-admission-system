<template>
  <router-view />
</template>

<script setup>
// 主应用组件
</script>

<style>
/* 全局样式 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden; /* 防止出现双层滚动条 */
}

/* 为所有页面设置浅色背景 */
body {
  background-color: #f5f7fa;
}
</style>
