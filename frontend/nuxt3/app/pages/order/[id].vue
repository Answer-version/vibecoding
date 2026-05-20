<template>
  <div class="order-detail-page container">
    <div class="page-header">
      <NuxtLink to="/order" class="back-link">&larr; Back to Orders</NuxtLink>
      <h1>Order #{{ order.orderNo }}</h1>
      <div class="order-status" :class="order.status?.toLowerCase()">
        {{ getStatusText(order.status) }}
      </div>
    </div>

    <div class="order-content">
      <div class="order-info-section">
        <section class="shipping-info">
          <h2>Shipping Address</h2>
          <div class="info-card">
            <p><strong>{{ order.receiverName }}</strong></p>
            <p>{{ order.receiverPhone }}</p>
            <p>{{ order.receiverAddress }}</p>
          </div>
        </section>

        <section class="payment-info">
          <h2>Payment Information</h2>
          <div class="info-card">
            <p>
              <span>Method:</span>
              {{ order.payMethod }}
            </p>
            <p v-if="order.payTime">
              <span>Paid on:</span>
              {{ formatDate(order.payTime) }}
            </p>
          </div>
        </section>
      </div>

      <section class="order-items">
        <h2>Order Items</h2>
        <div class="items-list">
          <div v-for="item in order.items" :key="item.id" class="item">
            <div class="item-info">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-sku" v-if="item.skuName">{{ item.skuName }}</span>
            </div>
            <div class="item-price">${{ item.price }} x {{ item.quantity }}</div>
            <div class="item-total">${{ item.totalAmount }}</div>
          </div>
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span>Subtotal:</span>
            <span>${{ order.totalAmount }}</span>
          </div>
          <div class="summary-row">
            <span>Discount:</span>
            <span>-${{ order.discountAmount || 0 }}</span>
          </div>
          <div class="summary-row">
            <span>Shipping:</span>
            <span>${{ shippingFee }}</span>
          </div>
          <div class="summary-row total">
            <span>Total:</span>
            <span>${{ finalAmount }}</span>
          </div>
        </div>
      </section>

      <section class="order-timeline">
        <h2>Order Timeline</h2>
        <div class="timeline">
          <div
            v-for="(event, index) in timeline"
            :key="index"
            class="timeline-event"
            :class="{ completed: event.completed }"
          >
            <div class="event-dot"></div>
            <div class="event-info">
              <span class="event-status">{{ event.status }}</span>
              <span class="event-time" v-if="event.time">{{ formatDate(event.time) }}</span>
            </div>
          </div>
        </div>
      </section>

      <div class="order-actions">
        <button
          v-if="order.status === 'PENDING'"
          class="btn-pay"
          @click="payNow"
        >
          Pay Now
        </button>
        <button
          v-if="order.status === 'PENDING'"
          class="btn-cancel"
          @click="cancelOrder"
        >
          Cancel Order
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface OrderItem {
  id: number
  productName: string
  skuName: string
  price: number
  quantity: number
  totalAmount: number
}

interface Order {
  id: number
  orderNo: string
  status: string
  totalAmount: number
  discountAmount: number
  payMethod: string
  payTime: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  createTime: string
  items: OrderItem[]
}

const route = useRoute()
const { get, post, del } = useApi()

const orderId = route.params.id as string
const order = ref<Order>({
  id: 0,
  orderNo: '',
  status: '',
  totalAmount: 0,
  discountAmount: 0,
  payMethod: '',
  payTime: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  createTime: '',
  items: []
})

const shippingFee = ref(9.99)
const finalAmount = computed(() =>
  order.value.totalAmount - (order.value.discountAmount || 0) + shippingFee.value
)

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

const timeline = computed(() => [
  { status: 'Order Placed', time: order.value.createTime, completed: true },
  { status: 'Payment Received', time: order.value.payTime, completed: order.value.status !== 'PENDING' },
  { status: 'Shipped', time: null, completed: ['SHIPPED', 'DELIVERED', 'COMPLETED'].includes(order.value.status) },
  { status: 'Delivered', time: null, completed: ['DELIVERED', 'COMPLETED'].includes(order.value.status) }
])

function getStatusText(status: string) {
  return statusMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

async function loadOrder() {
  try {
    const { data } = await get<any>(`/orders/${orderId}`)
    if (data.value?.code === 0) {
      order.value = data.value.data
    }
  } catch (e) {
    console.error('Failed to load order', e)
  }
}

async function payNow() {
  // Navigate to payment page
  navigateTo(`/payment/${orderId}`)
}

async function cancelOrder() {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    const { data } = await del<any>(`/orders/${orderId}/cancel`)
    if (data.value?.code === 0) {
      await loadOrder()
    }
  } catch (e) {
    console.error('Failed to cancel order', e)
  }
}

await loadOrder()
</script>

<style scoped>
.order-detail-page {
  padding: 40px 20px;
}
.page-header {
  margin-bottom: 40px;
}
.back-link {
  color: #667eea;
  text-decoration: none;
  margin-bottom: 10px;
  display: inline-block;
}
h1 {
  margin: 10px 0;
}
.order-status {
  display: inline-block;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  margin-top: 10px;
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
.order-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 40px;
}
.order-info-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 40px;
}
h2 {
  font-size: 18px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.info-card {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}
.info-card p {
  margin: 5px 0;
}
.items-list {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}
.item {
  display: grid;
  grid-template-columns: 1fr 150px 100px;
  gap: 20px;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}
.item-sku {
  color: #666;
  font-size: 14px;
}
.item-total {
  font-weight: bold;
  text-align: right;
}
.order-summary {
  margin-top: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.summary-row.total {
  font-size: 18px;
  font-weight: bold;
  border-top: 1px solid #ddd;
  padding-top: 15px;
  margin-top: 15px;
}
.order-timeline {
  margin-top: 40px;
}
.timeline {
  padding: 20px 0;
}
.timeline-event {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
  position: relative;
}
.timeline-event:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 35px;
  bottom: 0;
  width: 2px;
  background: #eee;
}
.event-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #ddd;
}
.timeline-event.completed .event-dot {
  background: #667eea;
}
.event-status {
  font-weight: bold;
}
.event-time {
  color: #666;
  font-size: 14px;
}
.order-actions {
  margin-top: 40px;
  display: flex;
  gap: 15px;
}
.btn-pay {
  padding: 12px 30px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.btn-cancel {
  padding: 12px 30px;
  background: #dc3545;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>