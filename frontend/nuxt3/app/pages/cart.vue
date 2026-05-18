<template>
  <div class="cart-page container">
    <h1>Shopping Cart</h1>

    <div v-if="items.length === 0" class="empty-cart">
      <p>Your cart is empty</p>
      <NuxtLink to="/products" class="btn">Continue Shopping</NuxtLink>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          <div class="item-info">
            <h3>{{ item.name }}</h3>
            <p class="sku" v-if="item.skuName">{{ item.skuName }}</p>
          </div>
          <div class="item-price">${{ item.price }}</div>
          <div class="item-quantity">
            <button @click="updateQty(item.id, item.quantity - 1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="updateQty(item.id, item.quantity + 1)">+</button>
          </div>
          <div class="item-total">${{ (item.price * item.quantity).toFixed(2) }}</div>
          <button class="btn-remove" @click="remove(item.id)">×</button>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-row">
          <span>Subtotal:</span>
          <span>${{ subtotal.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping:</span>
          <span>{{ shipping > 0 ? '$' + shipping.toFixed(2) : 'Calculated at checkout' }}</span>
        </div>
        <div class="summary-row total">
          <span>Total:</span>
          <span>${{ (subtotal + shipping).toFixed(2) }}</span>
        </div>
        <button class="btn-checkout" @click="checkout">Proceed to Checkout</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface CartItem {
  id: number
  productId: number
  skuId: number
  name: string
  skuName: string
  image: string
  price: number
  quantity: number
}

const { get, post, del } = useApi()
const items = ref<CartItem[]>([])
const subtotal = ref(0)
const shipping = ref(0)

const { data: cartData } = await get<any>('/cart')
if (cartData.value?.data) {
  items.value = cartData.value.data.items || []
  subtotal.value = cartData.value.data.subtotal || 0
}

async function updateQty(itemId: number, quantity: number) {
  if (quantity < 1) return
  await post(`/cart/items/${itemId}`, { quantity })
  refreshNuxtData()
}

async function remove(itemId: number) {
  await del(`/cart/items/${itemId}`)
  refreshNuxtData()
}

function checkout() {
  navigateTo('/checkout')
}
</script>

<style scoped>
.cart-page {
  padding: 40px 20px;
}
.empty-cart {
  text-align: center;
  padding: 80px 0;
}
.empty-cart p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}
.cart-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 40px;
}
.cart-item {
  display: grid;
  grid-template-columns: 80px 1fr 100px 120px 80px 40px;
  gap: 20px;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}
.item-image img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
}
.item-info h3 {
  font-size: 16px;
  margin-bottom: 5px;
}
.sku {
  color: #666;
  font-size: 14px;
}
.item-price,
.item-total {
  font-weight: bold;
}
.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}
.item-quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}
.btn-remove {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}
.cart-summary {
  background: #f9f9f9;
  padding: 30px;
  border-radius: 10px;
  height: fit-content;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}
.summary-row.total {
  font-size: 20px;
  font-weight: bold;
  border-top: 1px solid #ddd;
  padding-top: 15px;
}
.btn-checkout {
  width: 100%;
  padding: 15px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
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