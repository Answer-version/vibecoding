<template>
  <div class="cart-page container">
    <h1>Shopping Cart</h1>

    <div v-if="pending" class="loading">Loading cart...</div>

    <div v-else-if="!items || items.length === 0" class="empty-cart">
      <p>Your cart is empty</p>
      <NuxtLink to="/products" class="btn">Continue Shopping</NuxtLink>
    </div>

    <div v-else class="cart-content">
      <div class="cart-actions">
        <label class="select-all">
          <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" />
          <span>Select All</span>
        </label>
        <button class="btn-delete-selected" @click="deleteSelected" :disabled="selectedItems.length === 0">
          Delete Selected
        </button>
        <button class="btn-clear" @click="clearCart">Clear Cart</button>
      </div>

      <div class="cart-items">
        <div v-for="item in items" :key="item.id" class="cart-item">
          <div class="item-checkbox">
            <input type="checkbox" :value="item.id" v-model="selectedItems" />
          <div class="item-image">
            <img :src="getProductImage(item)" :alt="item.productName">
          </div>
          <div class="item-info">
            <h3>{{ item.productName || 'Product' }}</h3>
            <p class="sku" v-if="item.skuCode">{{ item.skuCode }}</p>
            <p class="price">${{ item.usdPrice?.toFixed(2) }}</p>
          </div>
          <div class="item-quantity">
            <button @click="handleUpdateQty(item.id, item.quantity - 1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="handleUpdateQty(item.id, item.quantity + 1)">+</button>
          </div>
          <div class="item-total">${{ item.usdAmount?.toFixed(2) }}</div>
          <button class="btn-remove" @click="handleRemove(item.id)">×</button>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-row">
          <span>Subtotal:</span>
          <span>${{ subtotal?.toFixed(2) || '0.00' }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping:</span>
          <span>{{ shipping > 0 ? '$' + shipping.toFixed(2) : 'Calculated at checkout' }}</span>
        </div>
        <div class="summary-row total">
          <span>Total:</span>
          <span>${{ (subtotal + shipping)?.toFixed(2) || '0.00' }}</span>
        </div>
        <button class="btn-checkout" @click="checkout">
          Proceed to Checkout
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { getProductImage } = useProductImage()
const { cart, loadCart, updateItem, removeItem } = useCart()

const pending = ref(true)
const items = ref<any[]>([])
const subtotal = ref(0)
const shipping = ref(9.99)
const selectedItems = ref<number[]>([])
const selectAll = ref(false)

async function load() {
  pending.value = true
  try {
    const data = await loadCart()
    if (data) {
      items.value = data.items || []
      subtotal.value = data.subtotal || 0
    }
  } catch (e) {
    console.error('Failed to load cart', e)
    items.value = []
  } finally {
    pending.value = false
  }
}

async function handleUpdateQty(itemId: number, quantity: number) {
  if (quantity < 1) return
  try {
    await updateItem(itemId, quantity)
    await load()
  } catch (e) {
    alert('Failed to update quantity')
  }
}

async function handleRemove(itemId: number) {
  try {
    await removeItem(itemId)
    await load()
  } catch (e) {
    alert('Failed to remove item')
  }
}

function toggleSelectAll() {
  if (selectAll.value) {
    selectedItems.value = items.value.map((item: any) => item.id)
  } else {
    selectedItems.value = []
  }
}

async function deleteSelected() {
  if (selectedItems.value.length === 0) return
  if (!confirm(`Delete ${selectedItems.value.length} selected item(s)?`)) return

  try {
    for (const itemId of selectedItems.value) {
      await removeItem(itemId)
    }
    selectedItems.value = []
    selectAll.value = false
    await load()
  } catch (e) {
    alert('Failed to delete selected items')
  }
}

async function clearCart() {
  if (!confirm('Clear all items from cart?')) return

  try {
    await useCart().clearCart()
    navigateTo('/cart')
  } catch (e) {
    alert('Failed to clear cart')
  }
}

function checkout() {
  navigateTo('/checkout')
}

await load()
</script>

<style scoped>
.cart-page {
  padding: 40px 20px;
}
.loading, .empty-cart {
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
.cart-actions {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}
.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.btn-delete-selected {
  padding: 8px 16px;
  background: #e74c3c;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.btn-delete-selected:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.btn-clear {
  padding: 8px 16px;
  background: #6c757d;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.cart-item {
  display: grid;
  grid-template-columns: 40px 80px 1fr 120px 100px 40px;
  gap: 20px;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}
.item-checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
}
.item-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}
.item-image img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 5px;
  background: #f5f5f5;
}
.item-info h3 {
  font-size: 16px;
  margin-bottom: 5px;
}
.sku {
  color: #666;
  font-size: 14px;
}
.item-price, .item-total {
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
.btn-checkout:hover {
  background: #5568d3;
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