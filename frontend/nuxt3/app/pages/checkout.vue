<template>
  <div class="checkout-page container">
    <h1>Checkout</h1>

    <div v-if="pending" class="loading">Loading...</div>

    <div v-else-if="!isLoggedIn" class="login-prompt">
      <p>Please login to checkout</p>
      <NuxtLink to="/auth/login" class="btn">Login</NuxtLink>
    </div>

    <div v-else-if="items.length === 0" class="empty-cart">
      <p>Your cart is empty</p>
      <NuxtLink to="/products" class="btn">Continue Shopping</NuxtLink>
    </div>

    <div v-else class="checkout-content">
      <div class="checkout-form">
        <section class="shipping-section">
          <h2>Shipping Address</h2>

          <div v-if="addresses.length > 0" class="address-list">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="address-card"
              :class="{ selected: selectedAddressId === addr.id }"
              @click="selectedAddressId = addr.id"
            >
              <div class="address-details">
                <strong>{{ addr.receiverName }}</strong>
                <p>{{ addr.phone }}</p>
                <p>{{ addr.province }} {{ addr.city }} {{ addr.district }}</p>
                <p>{{ addr.detailAddress }}</p>
              </div>
              <span v-if="addr.isDefault" class="default-badge">Default</span>
            </div>
          </div>

          <button v-else class="btn-add-address" @click="showAddressForm = true">
            + Add New Address
          </button>

          <div v-if="showAddressForm" class="address-form">
            <input v-model="newAddress.receiverName" placeholder="Receiver Name" />
            <input v-model="newAddress.phone" placeholder="Phone Number" />
            <input v-model="newAddress.province" placeholder="Province" />
            <input v-model="newAddress.city" placeholder="City" />
            <input v-model="newAddress.district" placeholder="District" />
            <input v-model="newAddress.detailAddress" placeholder="Detailed Address" />
            <label>
              <input v-model="newAddress.isDefault" type="checkbox" />
              Set as default address
            </label>
            <button @click="saveAddress">Save Address</button>
            <button @click="showAddressForm = false" class="btn-cancel">Cancel</button>
          </div>
        </section>

        <section class="payment-section">
          <h2>Payment Method</h2>
          <div class="payment-methods">
            <div
              v-for="method in paymentMethods"
              :key="method.id"
              class="payment-method"
              :class="{ selected: selectedPayment === method.id }"
              @click="selectedPayment = method.id"
            >
              <span class="method-icon">{{ method.icon }}</span>
              <span class="method-name">{{ method.name }}</span>
            </div>
          </div>
        </section>

        <section class="order-review">
          <h2>Order Review</h2>
          <div class="review-items">
            <div v-for="item in items" :key="item.id" class="review-item">
              <span>{{ item.productName }} x {{ item.quantity }}</span>
              <span>${{ item.usdAmount?.toFixed(2) }}</span>
            </div>
          </div>
        </section>

        <section class="coupon-section">
          <h2>Coupon Code</h2>
          <div class="coupon-input">
            <input
              v-model="couponCode"
              placeholder="Enter coupon code"
              :disabled="appliedCoupon"
            />
            <button @click="applyCoupon" :disabled="!couponCode || appliedCoupon">
              {{ appliedCoupon ? 'Applied' : 'Apply' }}
            </button>
            <button v-if="appliedCoupon" @click="removeCoupon" class="btn-remove-coupon">
              Remove
            </button>
          </div>
          <div v-if="couponMessage" :class="['coupon-message', couponValid ? 'success' : 'error']">
            {{ couponMessage }}
          </div>
        </section>
      </div>

      <div class="order-summary">
        <h2>Order Summary</h2>
        <div class="summary-row">
          <span>Subtotal:</span>
          <span>${{ subtotal.toFixed(2) }}</span>
        </div>
        <div class="summary-row" v-if="appliedCoupon">
          <span>Discount:</span>
          <span>-${{ discountAmount.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping:</span>
          <span>${{ effectiveShipping.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span>Tax:</span>
          <span>${{ tax.toFixed(2) }}</span>
        </div>
        <div class="summary-row total">
          <span>Total:</span>
          <span>${{ total.toFixed(2) }}</span>
        </div>
        <button class="btn-place-order" @click="placeOrder" :disabled="!canPlaceOrder">
          Place Order
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Address {
  id: number
  receiverName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: number
}

interface PaymentMethod {
  id: string
  name: string
  icon: string
}

interface CartItem {
  id: number
  productId: number
  productName: string
  quantity: number
  usdAmount: number
}

const { get, post } = useApi()
const { token } = useAuth()
const { loadCart, cart } = useCart()

const pending = ref(true)
const addresses = ref<Address[]>([])
const items = ref<CartItem[]>([])
const selectedAddressId = ref<number | null>(null)
const selectedPayment = ref('paypal')
const showAddressForm = ref(false)
const newAddress = ref({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const isLoggedIn = computed(() => !!token.value)

// 优惠券相关状态
const couponCode = ref('')
const appliedCoupon = ref<any>(null)
const couponMessage = ref('')
const couponValid = ref(false)
const discountAmount = ref(0)
const userCouponId = ref<number | null>(null)

// 计算实际运费（考虑免运费券）
const effectiveShipping = computed(() => {
  if (appliedCoupon.value?.couponType === 3) { // 免运费
    return 0
  }
  return shipping.value
})

// 计算总价（考虑优惠）
const total = computed(() => {
  let totalWithoutCoupon = subtotal.value + effectiveShipping.value + tax.value
  return Math.max(0, totalWithoutCoupon - discountAmount.value)
})

// 应用优惠券
async function applyCoupon() {
  if (!couponCode.value) return

  try {
    const res = await post<any>('/coupons/validate', null, {
      couponCode: couponCode.value,
      totalAmount: subtotal.value
    })

    if (res.data.value?.code === 0 && res.data.value.data?.valid) {
      const data = res.data.value.data
      appliedCoupon.value = data
      couponValid.value = true
      couponMessage.value = `Coupon applied: -$${data.discountAmount?.toFixed(2)}`
      discountAmount.value = data.discountAmount || 0
      userCouponId.value = data.couponId
    } else {
      couponValid.value = false
      couponMessage.value = res.data.value?.data?.message || 'Invalid coupon'
      discountAmount.value = 0
    }
  } catch (e: any) {
    couponValid.value = false
    couponMessage.value = e.message || 'Failed to apply coupon'
  }
}

// 移除优惠券
function removeCoupon() {
  appliedCoupon.value = null
  couponCode.value = ''
  couponMessage.value = ''
  discountAmount.value = 0
  userCouponId.value = null
}

const paymentMethods = ref<PaymentMethod[]>([
  { id: 'paypal', name: 'PayPal', icon: 'P' },
  { id: 'alipay', name: 'Alipay', icon: 'A' },
  { id: 'wechat', name: 'WeChat Pay', icon: 'W' },
  { id: 'credit_card', name: 'Credit Card', icon: 'C' }
])

const subtotal = computed(() => {
  return items.value.reduce((sum, item) => sum + (item.usdAmount || 0), 0)
})

const shipping = ref(9.99)
const tax = computed(() => subtotal.value * 0.08)

const canPlaceOrder = computed(() =>
  isLoggedIn.value && selectedAddressId.value !== null && items.value.length > 0
)

async function loadData() {
  pending.value = true
  try {
    const cartData = await loadCart()
    if (cartData) {
      items.value = cartData.items || []
    }

    if (isLoggedIn.value) {
      const addrRes = await get<any>('/user/addresses')
      if (addrRes.data.value?.code === 0) {
        addresses.value = addrRes.data.value.data || []
        const defaultAddr = addresses.value.find((a: Address) => a.isDefault === 1)
        if (defaultAddr) selectedAddressId.value = defaultAddr.id
        else if (addresses.value.length > 0) selectedAddressId.value = addresses.value[0].id
      }
    }
  } catch (e) {
    console.error('Failed to load checkout data', e)
  } finally {
    pending.value = false
  }
}

async function saveAddress() {
  try {
    const res = await post('/user/addresses', newAddress.value)
    if (res.data.value?.code === 0) {
      showAddressForm.value = false
      await loadData()
    }
  } catch (e) {
    console.error('Failed to save address', e)
  }
}

async function placeOrder() {
  if (!canPlaceOrder.value) return

  try {
    const res = await post('/orders/checkout', {
      addressId: selectedAddressId.value,
      payMethod: selectedPayment.value,
      couponCode: appliedCoupon.value?.couponCode || null,
      userCouponId: userCouponId.value,
      discountAmount: discountAmount.value
    })

    if (res.data.value?.code === 0) {
      removeCoupon() // Clear coupon after successful order
      navigateTo(`/order/success?orderId=${res.data.value.data.orderId}`)
    } else {
      alert(res.data.value?.message || 'Failed to place order')
    }
  } catch (e) {
    alert('Failed to place order')
  }
}

await loadData()
</script>

<style scoped>
.checkout-page {
  padding: 40px 20px;
}
.loading, .login-prompt, .empty-cart {
  text-align: center;
  padding: 80px 20px;
}
.login-prompt p, .empty-cart p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}
.checkout-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 40px;
  margin-top: 30px;
}
h2 {
  font-size: 20px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.section {
  margin-bottom: 40px;
}
.address-list {
  display: grid;
  gap: 15px;
}
.address-card {
  border: 2px solid #ddd;
  padding: 15px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}
.address-card.selected {
  border-color: #667eea;
  background: #f8f9ff;
}
.address-details p {
  margin: 5px 0;
  color: #666;
}
.default-badge {
  background: #667eea;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.btn-add-address {
  padding: 12px 24px;
  background: none;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
}
.address-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}
.address-form input {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.payment-methods {
  display: flex;
  gap: 15px;
}
.payment-method {
  border: 2px solid #ddd;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  flex: 1;
  text-align: center;
  transition: all 0.2s;
}
.payment-method.selected {
  border-color: #667eea;
  background: #f8f9ff;
}
.method-icon {
  font-size: 24px;
  display: block;
  margin-bottom: 5px;
}
.review-items {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}
.review-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border-bottom: 1px solid #eee;
}
.order-summary {
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
.btn-place-order {
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
.btn-place-order:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.btn-cancel {
  background: #f5f5f5;
  color: #333;
}
.btn {
  display: inline-block;
  padding: 12px 30px;
  background: #667eea;
  color: #fff;
  text-decoration: none;
  border-radius: 25px;
}
.coupon-section {
  margin-top: 20px;
}
.coupon-input {
  display: flex;
  gap: 10px;
}
.coupon-input input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.coupon-input button {
  padding: 12px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.coupon-input button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.btn-remove-coupon {
  background: #e74c3c !important;
}
.coupon-message {
  margin-top: 10px;
  padding: 10px;
  border-radius: 5px;
  font-size: 14px;
}
.coupon-message.success {
  background: #d4edda;
  color: #155724;
}
.coupon-message.error {
  background: #f8d7da;
  color: #721c24;
}
</style>