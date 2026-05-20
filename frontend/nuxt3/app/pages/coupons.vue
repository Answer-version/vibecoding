<template>
  <div class="my-coupons-page container">
    <h1>My Coupons</h1>

    <div v-if="pending" class="loading">Loading...</div>

    <div v-else-if="!isLoggedIn" class="login-prompt">
      <p>Please login to view your coupons</p>
      <NuxtLink to="/auth/login" class="btn">Login</NuxtLink>
    </div>

    <div v-else class="coupons-content">
      <div class="tabs">
        <button
          :class="['tab', { active: activeTab === 0 }]"
          @click="activeTab = 0; loadCoupons()"
        >
          Available ({{ availableCount }})
        </button>
        <button
          :class="['tab', { active: activeTab === 1 }]"
          @click="activeTab = 1; loadCoupons()"
        >
          Used
        </button>
        <button
          :class="['tab', { active: activeTab === 2 }]"
          @click="activeTab = 2; loadCoupons()"
        >
          Expired
        </button>
      </div>

      <div v-if="coupons.length === 0" class="empty">
        <p>No coupons in this category</p>
      </div>

      <div v-else class="coupon-list">
        <div
          v-for="uc in coupons"
          :key="uc.id"
          :class="['coupon-card', getStatusClass(uc.status)]"
        >
          <div class="coupon-info">
            <div class="coupon-name">{{ getCouponName(uc) }}</div>
            <div class="coupon-type">{{ getCouponTypeText(uc) }}</div>
            <div class="coupon-time" v-if="uc.getTime">
              Get: {{ formatDate(uc.getTime) }}
            </div>
          </div>
          <div class="coupon-status">
            {{ getStatusText(uc.status) }}
          </div>
          <button
            v-if="uc.status === 0"
            class="btn-use"
            @click="useCoupon(uc)"
          >
            Use Now
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface UserCoupon {
  id: number
  userId: number
  couponId: number
  status: number
  getTime: string
  useTime: string | null
  orderId: number | null
}

interface Coupon {
  id: number
  name: string
  nameEn: string
  type: number
  value: number
  minAmount: number
  endTime: string
}

const { get } = useApi()
const { token } = useAuth()

const pending = ref(true)
const activeTab = ref(0)
const coupons = ref<(UserCoupon & { coupon?: Coupon })[]>([])

const isLoggedIn = computed(() => !!token.value)

const availableCount = computed(() =>
  coupons.value.filter(c => c.status === 0).length
)

async function loadCoupons() {
  pending.value = true
  try {
    const statusMap = [0, 1, 2]
    const res = await get<any>('/coupons/my', { status: statusMap[activeTab.value] })
    if (res.data.value?.code === 0) {
      coupons.value = res.data.value.data || []
    }
  } catch (e) {
    console.error('Failed to load coupons', e)
  } finally {
    pending.value = false
  }
}

function getCouponName(uc: UserCoupon & { coupon?: Coupon }) {
  if (uc.coupon) {
    return uc.coupon.name
  }
  return 'Coupon'
}

function getCouponTypeText(uc: UserCoupon & { coupon?: Coupon }) {
  if (uc.coupon) {
    if (uc.coupon.type === 1) return `$${uc.coupon.value} OFF`
    if (uc.coupon.type === 2) return `${uc.coupon.value * 100}% OFF`
    if (uc.coupon.type === 3) return 'Free Shipping'
  }
  return ''
}

function getStatusClass(status: number) {
  if (status === 0) return 'available'
  if (status === 1) return 'used'
  return 'expired'
}

function getStatusText(status: number) {
  if (status === 0) return 'Available'
  if (status === 1) return 'Used'
  return 'Expired'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

function useCoupon(uc: UserCoupon & { coupon?: Coupon }) {
  navigateTo('/products')
}

await loadCoupons()
</script>

<style scoped>
.my-coupons-page {
  padding: 40px 20px;
}
.loading, .login-prompt, .empty {
  text-align: center;
  padding: 80px 20px;
}
.login-prompt p, .empty p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}
.tab {
  padding: 12px 24px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  border-radius: 5px;
  font-size: 16px;
}
.tab.active {
  background: #667eea;
  color: #fff;
}
.coupon-list {
  display: grid;
  gap: 15px;
}
.coupon-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  background: #f9f9f9;
}
.coupon-card.available {
  border-left: 4px solid #27ae60;
}
.coupon-card.used {
  border-left: 4px solid #95a5a6;
  opacity: 0.7;
}
.coupon-card.expired {
  border-left: 4px solid #e74c3c;
  opacity: 0.7;
}
.coupon-info {
  flex: 1;
}
.coupon-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}
.coupon-type {
  color: #666;
  font-size: 14px;
}
.coupon-time {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
.coupon-status {
  margin-right: 20px;
  color: #666;
}
.btn-use {
  padding: 10px 20px;
  background: #667eea;
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