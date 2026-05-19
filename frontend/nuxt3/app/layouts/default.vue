<template>
  <div class="layout">
    <header class="header">
      <div class="container">
        <div class="header-content">
          <NuxtLink to="/" class="logo">VibeCommerce</NuxtLink>
          <nav class="nav">
            <NuxtLink to="/">{{ t('home') }}</NuxtLink>
            <NuxtLink to="/products">{{ t('products') }}</NuxtLink>
          </nav>
          <div class="header-right">
            <select v-model="currentLocale" @change="changeLocale">
              <option value="en">EN</option>
              <option value="zh">中文</option>
            </select>
            <NuxtLink to="/cart" class="cart-icon">🛒</NuxtLink>
            <template v-if="isLoggedIn">
              <NuxtLink to="/profile" class="user-link">👤 {{ t('home') }}</NuxtLink>
            </template>
            <template v-else>
              <NuxtLink to="/auth/login" class="login-link">{{ t('login') }}</NuxtLink>
            </template>
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
const { locale, t, setLocale } = useI18n()
const token = useCookie('token')
const isLoggedIn = computed(() => !!token.value)

const currentLocale = ref(locale.value)

function changeLocale() {
  setLocale(currentLocale.value)
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
.header-right select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}
.cart-icon {
  font-size: 20px;
  text-decoration: none;
}
.login-link {
  color: #007bff;
  text-decoration: none;
  padding: 8px 16px;
  border: 1px solid #007bff;
  border-radius: 6px;
}
.user-link {
  color: #666;
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