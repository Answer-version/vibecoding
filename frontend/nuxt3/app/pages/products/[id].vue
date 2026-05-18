<template>
  <div class="product-detail container" v-if="product">
    <div class="detail">
      <div class="image">
        <img :src="product.image || '/placeholder.jpg'" :alt="product.name">
      </div>
      <div class="info">
        <h1>{{ product.name }}</h1>
        <p class="price">${{ product.price }}</p>
        <p class="description">{{ product.description }}</p>

        <div class="sku-select" v-if="skus.length">
          <label>Options:</label>
          <select v-model="selectedSku">
            <option v-for="sku in skus" :key="sku.id" :value="sku">{{ sku.name }}</option>
          </select>
        </div>

        <div class="quantity">
          <label>Quantity:</label>
          <input type="number" v-model="quantity" min="1" :max="selectedSku?.stock">
        </div>

        <button class="btn-add-cart" @click="addToCart">Add to Cart</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Product {
  id: number
  name: string
  price: number
  image: string
  description: string
}

interface Sku {
  id: number
  name: string
  stock: number
  price: number
}

const route = useRoute()
const productId = route.params.id as string

const config = useRuntimeConfig()
const { data: product } = await useFetch<Product>(`/products/${productId}`, {
  baseURL: config.public.apiBase
})

const { data: skus } = await useFetch<Sku[]>(`/products/${productId}/skus`, {
  baseURL: config.public.apiBase,
  default: () => []
})

const selectedSku = ref<Sku | null>(null)
const quantity = ref(1)

const { post } = useApi()

async function addToCart() {
  await post('/cart/items', {
    productId: product.value?.id,
    skuId: selectedSku.value?.id,
    quantity: quantity.value
  })
  navigateTo('/cart')
}
</script>

<style scoped>
.detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
  padding: 40px 20px;
}
.image img {
  width: 100%;
  border-radius: 10px;
}
.info h1 {
  font-size: 32px;
  margin-bottom: 20px;
}
.price {
  font-size: 28px;
  color: #e74c3c;
  font-weight: bold;
  margin-bottom: 20px;
}
.description {
  color: #666;
  line-height: 1.8;
  margin-bottom: 30px;
}
.sku-select,
.quantity {
  margin-bottom: 20px;
}
.sku-select label,
.quantity label {
  display: block;
  margin-bottom: 10px;
  font-weight: bold;
}
.sku-select select,
.quantity input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.btn-add-cart {
  width: 100%;
  padding: 15px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-add-cart:hover {
  background: #5568d3;
}
</style>