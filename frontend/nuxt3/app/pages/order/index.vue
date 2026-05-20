<template>
  <div class="orders-page container">
    <h1>My Orders</h1>

    <div v-if="orders.length === 0" class="empty-orders">
      <p>You don't have any orders yet</p>
      <NuxtLink to="/products" class="btn">Start Shopping</NuxtLink>
    </div>

    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <NuxtLink :to="`/order/${order.id}`" class="order-header">
          <div class="order-info">
            <span class="order-no">Order #{{ order.orderNo }}</span>
            <span class="order-date">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="order-status" :class="order.status.toLowerCase()">
            {{ getStatusText(order.status) }}
          </div>
        </NuxtLink>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span>{{ item.productName }} x {{ item.quantity }}</span>
            <span>${{ item.totalAmount }}</span>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            <span>Total:</span>
            <span>${{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <NuxtLink :to="`/order/${order.id}`" class="btn-view">View Details</NuxtLink>
            <button
              v-if="order.status === 'PENDING'"
              class="btn-cancel"
              @click="cancelOrder(order.id)"
            >
              Cancel Order
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Order {
  id: number
  orderNo: string
  status: string
  totalAmount: number
  createTime: string
  items: OrderItem[]
}

interface OrderItem {
  id: number
  productName: string
  quantity: number
  totalAmount: number
}

const { get, del } = useApi()
const orders = ref<Order[]>([])

const statusMap: Record<string, string> = {
  PENDING: 'Pending Payment',
  PAID: 'Paid',
  SHIPPED: 'Shipped',
  DELIVERED: 'Delivered',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  REFUNDING: 'Refunding',
  REFUNDED: 'Refunded'
}

function getStatusText(status: string) {
  return statusMap[status] || status
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString()
}

async function loadOrders() {
  try {
    const { data } = await get<any>('/orders')
    if (data.value?.code === 0) {
      orders.value = data.value.data || []
    }
  } catch (e) {
    console.error('Failed to load orders', e)
  }
}

async function cancelOrder(orderId: number) {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    const { data } = await del<any>(`/orders/${orderId}/cancel`)
    if (data.value?.code === 0) {
      await loadOrders()
    }
  } catch (e) {
    console.error('Failed to cancel order', e)
  }
}

await loadOrders()
</script>

<style scoped>
.orders-page {
  padding: 40px 20px;
}
.empty-orders {
  text-align: center;
  padding: 80px 0;
}
.empty-orders p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 30px;
}
.order-card {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
  text-decoration: none;
  color: inherit;
}
.order-no {
  font-weight: bold;
  margin-right: 15px;
}
.order-date {
  color: #666;
}
.order-status {
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
}
.order-status.pending {
  background: #fff3cd;
  color: #856404;
}
.order-status.paid {
  background: #d4edda;
  color: #155724;
}
.order-status.shipped {
  background: #cce5ff;
  color: #004085;
}
.order-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}
.order-items {
  padding: 20px;
}
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #eee;
}
.order-total {
  font-size: 18px;
  font-weight: bold;
}
.order-actions {
  display: flex;
  gap: 10px;
}
.btn-view {
  padding: 8px 20px;
  background: #667eea;
  color: #fff;
  text-decoration: none;
  border-radius: 5px;
}
.btn-cancel {
  padding: 8px 20px;
  background: #dc3545;
  color: #fff;
  border: none;
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