<template>
  <div class="products-page container">
    <div class="header">
      <h1>{{ t('products') }}</h1>
      <div class="filters">
        <select v-model="categoryId">
          <option :value="null">{{ t('allCategories') }}</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <input v-model="keyword" :placeholder="t('search')" @keyup.enter="search">
      </div>
    </div>

    <div class="product-grid">
      <div v-for="p in products" :key="p.id" class="product-card">
        <NuxtLink :to="`/products/${p.id}`">
          <div class="product-image">
            <img :src="getProductImage(p)" :alt="p.name">
          </div>
          <div class="product-info">
            <h3>{{ p.name }}</h3>
            <p class="price">${{ p.price }}</p>
          </div>
        </NuxtLink>
      </div>
    </div>

    <div v-if="loading" class="loading">Loading...</div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const { getProductImage } = useProductImage()

interface Product {
  id: number
  name: string
  price: number
  image: string
}

const route = useRoute()
const categoryId = ref(route.query.category as any)
const keyword = ref('')
const loading = ref(false)

const config = useRuntimeConfig()
const { data: productsData } = await useFetch('/products', {
  baseURL: config.public.apiBase,
  query: { page: 1, pageSize: 20, categoryId, keyword: keyword }
})

const products = computed(() => productsData.value?.data?.records || [])
const categories = ref([])

function search() {
  navigateTo(`/products?category=${categoryId.value}&keyword=${keyword.value}`)
}
</script>

<style scoped>
.products-page {
  padding: 40px 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}
.filters {
  display: flex;
  gap: 15px;
}
.filters select,
.filters input {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
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
.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}
</style>
