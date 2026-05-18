<template>
  <div class="layout">
    <header class="header">
      <div class="container">
        <div class="header-content">
          <NuxtLink to="/" class="logo">VibeCommerce</NuxtLink>
          <nav class="nav">
            <NuxtLink to="/">Home</NuxtLink>
            <NuxtLink to="/products">Products</NuxtLink>
            <NuxtLink to="/about">About</NuxtLink>
          </nav>
          <div class="header-right">
            <select v-model="locale" @change="changeLocale">
              <option value="en">EN</option>
              <option value="zh">中文</option>
            </select>
            <NuxtLink to="/cart" class="cart-icon">🛒</NuxtLink>
          </div>
        </div>
      </div>
    </header>
    <main class="main">
      <slot />
    </main>
    <footer class="footer">
      <div class="container">
        <p>&copy; 2026 VibeCommerce. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
const locale = useCookie('locale', { default: () => 'en' })

function changeLocale() {
  useCookie('locale').value = locale.value
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.header {
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  text-decoration: none;
}
.nav {
  display: flex;
  gap: 30px;
}
.nav a {
  color: #666;
  text-decoration: none;
  transition: color 0.2s;
}
.nav a:hover {
  color: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cart-icon {
  font-size: 20px;
  text-decoration: none;
}
.main {
  flex: 1;
}
.footer {
  background: #f5f5f5;
  padding: 20px 0;
  text-align: center;
  color: #666;
}
</style>