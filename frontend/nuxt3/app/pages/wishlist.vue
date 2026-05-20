<template>
  <div class="wishlist-page container">
    <h1>{{ t('wishlist') }}</h1>

    <div v-if="pending" class="loading">Loading...</div>

    <div v-else-if="!isLoggedIn" class="login-prompt">
      <p>Please login to view your wishlist</p>
      <NuxtLink to="/auth/login" class="btn">Login</NuxtLink>
    </div>

    <div v-else-if="items.length === 0" class="empty-wishlist">
      <p>Your wishlist is empty</p>
      <NuxtLink to="/products" class="btn">Continue Shopping</NuxtLink>
    </div>

    <div v-else class="wishlist-content">
      <div class="wishlist-grid">
        <div v-for="item in items" :key="item.id" class="wishlist-item">
          <NuxtLink :to="`/products/${item.productId}`" class="item-link">
            <div class="item-image">
              <img :src="getProductImage(null)" :alt="item.productId">
            </div>
            <div class="item-info">
              <h3>Product #{{ item.productId }}</h3>
              <p v-if="item.note" class="note">{{ item.note }}</p>
            </div>
          </NuxtLink>
          <div class="item-actions">
            <button class="btn-add-cart" @click="addToCart(item.productId)">Add to Cart</button>
            <button class="btn-remove" @click="handleRemove(item.productId)">Remove</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const { getProductImage } = useProductImage()
const { list, remove } = useWishlist()
const { addItem } = useCart()
const { token } = useAuth()

const pending = ref(true)
const items = ref<any[]>([])
const isLoggedIn = computed(() => !!token.value)

const { data: productsData } = await useFetch('/products', {
  baseURL: useRuntimeConfig().public.apiBase,
  query: { page: 1, pageSize: 100 }
})

const allProducts = computed(() => productsData.value?.data?.records || [])

function getProductById(productId: number) {
  return allProducts.value.find((p: any) => p.id === productId)
}

async function load() {
  pending.value = true
  try {
    if (isLoggedIn.value) {
      items.value = await list()
    }
  } catch (e) {
    console.error('Failed to load wishlist', e)
  } finally {
    pending.value = false
  }
}

async function handleRemove(productId: number) {
  try {
    await remove(productId)
    await load()
  } catch (e) {
    alert('Failed to remove item')
  }
}

async function addToCart(productId: number) {
  const product = getProductById(productId)
  if (!product) {
    alert('Product not found')
    return
  }
  try {
    await addItem(productId, null, 1, product.price, product.name)
    navigateTo('/cart')
  } catch (e) {
    alert('Failed to add to cart')
  }
}

await load()
</script>

<style scoped>
.wishlist-page {
  padding: 40px 20px;
}
.loading, .login-prompt, .empty-wishlist {
  text-align: center;
  padding: 80px 20px;
}
.login-prompt p, .empty-wishlist p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}
.wishlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 30px;
}
.wishlist-item {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.2s;
}
.wishlist-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.item-link {
  text-decoration: none;
  color: inherit;
}
.item-image {
  aspect-ratio: 1;
  background: #f5f5f5;
}
.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-info {
  padding: 15px;
}
.item-info h3 {
  font-size: 16px;
  margin-bottom: 5px;
}
.note {
  color: #666;
  font-size: 14px;
}
.item-actions {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}
.btn-add-cart {
  flex: 1;
  padding: 10px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.btn-remove {
  padding: 10px;
  background: #fff;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 5px;
  cursor: pointer;
}
.btn {
  display: inline-block;
  padding: 12px 30px;
  background: #667eea;
  color: #fff;
  text-decoration: none;
  border-radius: 25px;
}
</style>