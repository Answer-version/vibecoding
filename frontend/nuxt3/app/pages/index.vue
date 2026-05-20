<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="container">
        <h1>{{ t('welcome') }}</h1>
        <p>{{ t('subtitle') }}</p>
        <NuxtLink to="/products" class="btn">{{ t('shopNow') }}</NuxtLink>
      </div>
    </section>

    <!-- Quick Links -->
    <section class="quick-links container">
      <div class="links-grid">
        <NuxtLink to="/products" class="link-card">
          <span class="icon">🛍️</span>
          <span class="text">{{ t('products') }}</span>
        </NuxtLink>
        <NuxtLink to="/cart" class="link-card">
          <span class="icon">🛒</span>
          <span class="text">{{ t('cart') }}</span>
        </NuxtLink>
        <NuxtLink to="/about" class="link-card">
          <span class="icon">ℹ️</span>
          <span class="text">{{ t('about') }}</span>
        </NuxtLink>
        <NuxtLink to="/contact" class="link-card">
          <span class="icon">📞</span>
          <span class="text">{{ t('contact') }}</span>
        </NuxtLink>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="featured container">
      <h2>{{ t('featured') }}</h2>
      <div class="product-grid">
        <div v-for="product in productsList" :key="product.id" class="product-card">
          <NuxtLink :to="`/products/${product.id}`">
            <div class="product-image">
              <img :src="getProductImage(product)" :alt="product.name">
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">${{ product.price }}</p>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- User Section (shown when logged in) -->
    <section v-if="isLoggedIn" class="user-section container">
      <h2>My Account</h2>
      <div class="links-grid">
        <NuxtLink to="/profile" class="link-card">
          <span class="icon">👤</span>
          <span class="text">Profile</span>
        </NuxtLink>
        <NuxtLink to="/order" class="link-card">
          <span class="icon">📦</span>
          <span class="text">My Orders</span>
        </NuxtLink>
        <NuxtLink to="/wishlist" class="link-card">
          <span class="icon">❤️</span>
          <span class="text">{{ t('wishlist') }}</span>
        </NuxtLink>
        <NuxtLink to="/coupons" class="link-card">
          <span class="icon">🎫</span>
          <span class="text">{{ t('coupons') }}</span>
        </NuxtLink>
      </div>
    </section>

    <!-- Guest Section (shown when not logged in) -->
    <section v-if="!isLoggedIn" class="guest-section container">
      <h2>Account</h2>
      <div class="links-grid">
        <NuxtLink to="/auth/login" class="link-card">
          <span class="icon">🔑</span>
          <span class="text">{{ t('login') }}</span>
        </NuxtLink>
        <NuxtLink to="/auth/register" class="link-card">
          <span class="icon">📝</span>
          <span class="text">{{ t('register') }}</span>
        </NuxtLink>
      </div>
    </section>

    <!-- Policies -->
    <section class="policies container">
      <div class="policy-links">
        <NuxtLink to="/policy/privacy">Privacy Policy</NuxtLink>
        <NuxtLink to="/policy/terms">Terms of Service</NuxtLink>
        <NuxtLink to="/policy/shipping">Shipping Policy</NuxtLink>
        <NuxtLink to="/policy/returns">Return Policy</NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const { getProductImage } = useProductImage()
const token = useCookie('token')
const isLoggedIn = computed(() => !!token.value)

interface Product {
  id: number
  name: number
  price: number
  image: string
}

const config = useRuntimeConfig()

const { data: response } = await useFetch<{ code: number, data: { records: Product[] } }>('/products', {
  baseURL: config.public.apiBase
})

const productsList = computed(() => {
  if (response.value?.data?.records) {
    return response.value.data.records
  }
  return []
})
</script>

<style scoped>
.home {
  padding-bottom: 40px;
}
.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 80px 0;
  text-align: center;
}
.hero h1 {
  font-size: 48px;
  margin-bottom: 20px;
}
.hero p {
  font-size: 20px;
  margin-bottom: 30px;
}
.btn {
  display: inline-block;
  background: #fff;
  color: #667eea;
  padding: 12px 30px;
  text-decoration: none;
  border-radius: 25px;
  font-weight: bold;
  transition: transform 0.2s;
}
.btn:hover {
  transform: scale(1.05);
}
.quick-links, .user-section, .guest-section {
  padding: 40px 20px;
}
.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  text-decoration: none;
  color: #333;
  transition: all 0.2s;
}
.link-card:hover {
  background: #667eea;
  color: #fff;
  transform: translateY(-3px);
}
.link-card .icon {
  font-size: 32px;
  margin-bottom: 10px;
}
.link-card .text {
  font-size: 14px;
  font-weight: 500;
}
.featured {
  padding: 60px 20px;
}
.featured h2 {
  text-align: center;
  margin-bottom: 40px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
}
.product-card a {
  text-decoration: none;
  color: inherit;
}
.product-image {
  aspect-ratio: 1;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.product-info {
  padding: 15px 0;
}
.product-info h3 {
  font-size: 16px;
  margin-bottom: 8px;
}
.price {
  color: #e74c3c;
  font-size: 18px;
  font-weight: bold;
}
.featured h2, .user-section h2, .guest-section h2 {
  text-align: center;
  margin-bottom: 30px;
}
.policies {
  padding: 40px 20px;
  text-align: center;
}
.policy-links {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}
.policy-links a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}
.policy-links a:hover {
  color: #667eea;
}
</style>