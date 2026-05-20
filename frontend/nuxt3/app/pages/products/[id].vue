<template>
  <div class="product-detail container">
    <div v-if="pending" class="loading">Loading...</div>
    <div v-else-if="error" class="error">
      <p>Failed to load product. Please try again.</p>
      <button @click="refresh">Refresh</button>
    </div>
    <div v-else-if="product" class="detail">
      <div class="image">
        <img :src="getProductImage(product)" :alt="product.name">
      </div>
      <div class="info">
        <h1>{{ product.name }}</h1>
        <p class="subtitle" v-if="product.subtitle">{{ product.subtitle }}</p>
        <div class="price-row">
          <span class="price">${{ product.price?.toFixed(2) }}</span>
          <span class="original-price" v-if="product.originalPrice">${{ product.originalPrice?.toFixed(2) }}</span>
        </div>

        <div class="stock-info">
          <span v-if="product.stockQuantity > 0" class="in-stock">In Stock ({{ product.stockQuantity }})</span>
          <span v-else class="out-of-stock">Out of Stock</span>
        </div>

        <div class="description" v-if="product.description">
          <h3>Description</h3>
          <p>{{ product.description }}</p>
        </div>

        <div class="sku-select" v-if="skus && skus.length">
          <label>Options:</label>
          <select v-model="selectedSkuId">
            <option :value="null">Select option...</option>
            <option v-for="sku in skus" :key="sku.id" :value="sku.id">
              {{ sku.skuName || sku.skuCode }} - ${{ sku.price }}
            </option>
          </select>
        </div>

        <div class="quantity">
          <label>Quantity:</label>
          <div class="quantity-input">
            <button @click="quantity > 1 && quantity--">-</button>
            <input type="number" v-model="quantity" min="1" :max="product.stockQuantity">
            <button @click="quantity < product.stockQuantity && quantity++">+</button>
          </div>
        </div>

        <button class="btn-add-cart" @click="addToCart" :disabled="!product.stockQuantity">
          Add to Cart
        </button>

        <button class="btn-wishlist" @click="toggleWishlist" :class="{ active: isWishlisted }">
          {{ isWishlisted ? '♥ Saved' : '♡ Add to Wishlist' }}
        </button>

        <div class="product-meta" v-if="product.brandId || product.categoryId">
          <span v-if="product.brandId">Brand ID: {{ product.brandId }}</span>
          <span v-if="product.categoryId">Category ID: {{ product.categoryId }}</span>
        </div>

        <!-- Reviews -->
        <div class="review-section">
          <h3>Reviews ({{ reviewStats.totalCount || 0 }})</h3>
          <div v-if="reviewStats.avgRating" class="rating-summary">
            <span class="rating-num">{{ reviewStats.avgRating?.toFixed(1) }}</span>
            <span class="stars">/5</span>
          </div>
          <p v-if="!reviewStats.totalCount" class="no-reviews">No reviews yet</p>
          <button v-if="isLoggedIn" class="btn-write-review" @click="showReviewForm = !showReviewForm">
            {{ showReviewForm ? 'Cancel' : 'Write Review' }}
          </button>
          <div v-if="showReviewForm" class="review-form">
            <select v-model="newReview.rating">
              <option :value="5">5 - Excellent</option>
              <option :value="4">4 - Good</option>
              <option :value="3">3 - Average</option>
              <option :value="2">2 - Poor</option>
              <option :value="1">1 - Terrible</option>
            </select>
            <input v-model="newReview.title" placeholder="Title (optional)" />
            <textarea v-model="newReview.content" placeholder="Your review..." rows="3"></textarea>
            <button @click="submitReview">Submit</button>
          </div>
          <div class="review-list">
            <div v-for="r in reviews" :key="r.id" class="review-item">
              <span class="stars">{{ r.rating }}/5</span>
              <p v-if="r.title" class="title">{{ r.title }}</p>
              <p class="content">{{ r.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const productId = route.params.id as string
const { getProductImage } = useProductImage()
const { addItem } = useCart()
const { check: checkWishlist, toggle: toggleWishlistItem } = useWishlist()
const { list: listReviews, stats: getReviewStats, add: addReview } = useReview()
const token = useCookie('token')

const config = useRuntimeConfig()

const isWishlisted = ref(false)
const reviews = ref<any[]>([])
const reviewStats = ref<any>({})
const showReviewForm = ref(false)
const hasReviewed = ref(false)
const newReview = ref({ rating: 5, title: '', content: '', isAnonymous: false })
const isLoggedIn = computed(() => !!token.value)

// 检查是否已收藏
onMounted(async () => {
  if (token.value && product.value?.id) {
    isWishlisted.value = await checkWishlist(product.value.id)
  }
})

const { data: productData, pending, error, refresh } = await useFetch(`/products/${productId}`, {
  baseURL: config.public.apiBase,
  key: `product-${productId}`
})

const product = computed(() => productData.value?.data)

const { data: skusData } = await useFetch(`/products/${productId}/skus`, {
  baseURL: config.public.apiBase,
  key: `skus-${productId}`,
  default: () => []
})

const skus = computed(() => skusData.value?.data || [])

const selectedSkuId = ref<number | null>(null)
const quantity = ref(1)

async function addToCart() {
  if (!product.value) return

  const sku = selectedSkuId.value ? skus.value.find(s => s.id === selectedSkuId.value) : null
  const price = sku?.price || product.value.price

  try {
    await addItem(
      product.value.id,
      selectedSkuId.value,
      quantity.value,
      price,
      product.value.name
    )
    navigateTo('/cart')
  } catch (e) {
    alert('Failed to add to cart')
  }
}

async function toggleWishlist() {
  if (!token.value) {
    navigateTo('/auth/login')
    return
  }
  isWishlisted.value = await toggleWishlistItem(productId as unknown as number)
}

async function loadReviews() {
  if (!productId) return
  try {
    const pid = parseInt(productId)
    reviews.value = await listReviews(pid)
    reviewStats.value = await getReviewStats(pid)
  } catch (e) {
    console.error('Failed to load reviews', e)
  }
}

async function submitReview() {
  if (!newReview.value.content || !token.value) return
  try {
    await addReview(parseInt(productId), newReview.value.rating, newReview.value.content, {
      title: newReview.value.title,
      isAnonymous: newReview.value.isAnonymous
    })
    showReviewForm.value = false
    newReview.value = { rating: 5, title: '', content: '', isAnonymous: false }
    await loadReviews()
  } catch (e) {
    alert('Failed to submit review')
  }
}

// Load reviews on mount
onMounted(loadReviews)
</script>

<style scoped>
.product-detail {
  padding: 40px 20px;
}
.loading, .error {
  text-align: center;
  padding: 80px 20px;
}
.error button {
  margin-top: 20px;
  padding: 10px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
}
.image img {
  width: 100%;
  border-radius: 10px;
  background: #f5f5f5;
}
.info h1 {
  font-size: 32px;
  margin-bottom: 10px;
}
.subtitle {
  color: #666;
  font-size: 18px;
  margin-bottom: 20px;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 15px;
  margin-bottom: 20px;
}
.price {
  font-size: 28px;
  color: #e74c3c;
  font-weight: bold;
}
.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}
.stock-info {
  margin-bottom: 20px;
}
.in-stock {
  color: #27ae60;
}
.out-of-stock {
  color: #e74c3c;
}
.description {
  margin: 30px 0;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}
.description h3 {
  margin-bottom: 10px;
}
.description p {
  color: #666;
  line-height: 1.8;
}
.sku-select, .quantity {
  margin-bottom: 20px;
}
.sku-select label, .quantity label {
  display: block;
  margin-bottom: 10px;
  font-weight: bold;
}
.sku-select select, .quantity input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.quantity-input {
  display: flex;
  gap: 10px;
}
.quantity-input button {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 18px;
}
.quantity-input input {
  width: 80px;
  text-align: center;
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
.btn-add-cart:hover:not(:disabled) {
  background: #5568d3;
}
.btn-add-cart:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.btn-wishlist {
  margin-top: 10px;
  width: 100%;
  padding: 15px;
  background: #fff;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-wishlist:hover {
  background: #f8f9ff;
}
.btn-wishlist.active {
  background: #667eea;
  color: #fff;
}
.product-meta {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  color: #999;
  font-size: 14px;
}
.product-meta span {
  margin-right: 20px;
}
.review-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}
.review-section h3 {
  margin-bottom: 20px;
}
.rating-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}
.rating-num {
  font-size: 24px;
  font-weight: bold;
}
.stars {
  color: #f39c12;
  font-size: 18px;
}
.no-reviews {
  color: #999;
  margin-bottom: 15px;
}
.btn-write-review {
  padding: 10px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 20px;
}
.review-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}
.review-form select, .review-form input, .review-form textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.review-form button {
  padding: 10px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.review-list {
  margin-top: 20px;
}
.review-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}
.review-item .title {
  font-weight: bold;
  margin: 5px 0;
}
.review-item .content {
  color: #666;
  margin-top: 5px;
}
</style>