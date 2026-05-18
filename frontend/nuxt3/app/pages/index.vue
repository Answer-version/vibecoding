<template>
  <div class="home">
    <section class="hero">
      <div class="container">
        <h1>Welcome to VibeCommerce</h1>
        <p>Your trusted partner for cross-border e-commerce</p>
        <NuxtLink to="/products" class="btn">Shop Now</NuxtLink>
      </div>
    </section>

    <section class="featured container">
      <h2>Featured Products</h2>
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <NuxtLink :to="`/products/${product.id}`">
            <div class="product-image">
              <img :src="product.image" :alt="product.name">
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">${{ product.price }}</p>
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
interface Product {
  id: number
  name: string
  price: number
  image: string
}

const config = useRuntimeConfig()
const { data: products } = await useFetch<Product[]>('/products', {
  baseURL: config.public.apiBase,
  default: () => []
})
</script>

<style scoped>
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
</style>