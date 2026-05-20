<template>
  <div class="products-page container">
    <div class="header">
      <h1>{{ t('products') }}</h1>
      <div class="filters">
        <select v-model="categoryId">
          <option :value="null">{{ t('allCategories') }}</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <div class="search-box">
          <input
            v-model="keyword"
            :placeholder="t('search')"
            @keyup.enter="search"
            @input="onSearchInput"
          />
          <button @click="search" class="search-btn">🔍</button>
        </div>
      </div>
    </div>

    <!-- 热门搜索 -->
    <div class="hot-searches" v-if="!keyword && hotSearches.length">
      <span class="label">Hot:</span>
      <button v-for="tag in hotSearches" :key="tag" @click="quickSearch(tag)" class="hot-tag">
        {{ tag }}
      </button>
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
const hotSearches = ref(['iPhone', 'Samsung', 'Sony', 'Electronics'])

const config = useRuntimeConfig()

function search() {
  navigateTo(`/products?keyword=${keyword.value}&category=${categoryId.value}`)
}

function quickSearch(tag: string) {
  keyword.value = tag
  search()
}

function onSearchInput() {
  // 可以添加搜索建议功能的API调用
}

const { data: productsData } = await useFetch('/products', {
  baseURL: config.public.apiBase,
  query: { page: 1, pageSize: 20, categoryId, keyword }
})

const products = computed(() => productsData.value?.data?.records || [])
const categories = ref([])
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
.search-box {
  display: flex;
  gap: 5px;
}
.search-box input {
  width: 200px;
}
.search-btn {
  padding: 10px 15px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.hot-searches {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.hot-searches .label {
  color: #999;
  font-size: 14px;
}
.hot-tag {
  padding: 5px 12px;
  background: #f0f0f0;
  border: none;
  border-radius: 15px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}
.hot-tag:hover {
  background: #667eea;
  color: #fff;
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
