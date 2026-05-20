<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 侧边栏 -->
      <aside class="profile-sidebar">
        <div class="user-info">
          <div class="avatar">{{ userInitials }}</div>
          <h3>{{ user?.username || 'User' }}</h3>
          <p>{{ user?.email }}</p>
        </div>

        <nav class="sidebar-nav">
          <a
            v-for="tab in tabs"
            :key="tab.id"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            {{ tab.name }}
          </a>
        </nav>

        <div class="sidebar-footer">
          <button @click="handleLogout" class="logout-btn">Logout</button>
        </div>
      </aside>

      <!-- 主内容区 -->
      <main class="profile-main">
        <!-- 个人信息 -->
        <section v-if="activeTab === 'info'" class="tab-content">
          <h2>Personal Information</h2>

          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-row">
              <div class="form-group">
                <label>Username</label>
                <input v-model="profileForm.username" disabled />
              </div>
              <div class="form-group">
                <label>Email</label>
                <input v-model="profileForm.email" type="email" disabled />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>First Name</label>
                <input v-model="profileForm.firstName" />
              </div>
              <div class="form-group">
                <label>Last Name</label>
                <input v-model="profileForm.lastName" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Phone</label>
                <input v-model="profileForm.phone" />
              </div>
              <div class="form-group">
                <label>Nickname</label>
                <input v-model="profileForm.nickname" />
              </div>
            </div>

            <button type="submit" class="btn-primary" :disabled="saving">
              {{ saving ? 'Saving...' : 'Save Changes' }}
            </button>
          </form>
        </section>

        <!-- 订单列表 -->
        <section v-if="activeTab === 'orders'" class="tab-content">
          <h2>My Orders</h2>

          <div v-if="loadingOrders" class="loading">Loading orders...</div>

          <div v-else-if="!orders.length" class="empty-state">
            <p>You haven't placed any orders yet</p>
            <NuxtLink to="/products" class="btn-primary">Start Shopping</NuxtLink>
          </div>

          <div v-else class="order-list">
            <div v-for="order in orders" :key="order.id" class="order-card">
              <div class="order-header">
                <span class="order-no">Order #{{ order.orderNo }}</span>
                <span class="order-status" :class="`status-${order.status}`">
                  {{ getOrderStatus(order.status) }}
                </span>
              </div>
              <div class="order-details">
                <p><strong>Date:</strong> {{ formatDate(order.createTime) }}</p>
                <p><strong>Total:</strong> ${{ order.totalAmount?.toFixed(2) }}</p>
                <p><strong>Items:</strong> {{ order.itemCount || 0 }}</p>
              </div>
              <div class="order-actions">
                <NuxtLink :to="`/orders/${order.id}`" class="btn-secondary">View Details</NuxtLink>
                <button
                  v-if="order.status === 'PENDING'"
                  class="btn-danger"
                  @click="cancelOrder(order.id)"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- 收货地址 -->
        <section v-if="activeTab === 'addresses'" class="tab-content">
          <h2>Shipping Addresses</h2>

          <div class="address-list">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="address-card"
            >
              <div class="address-header">
                <span>{{ addr.firstName }} {{ addr.lastName }}</span>
                <span v-if="addr.isDefault === 1" class="default-badge">Default</span>
              </div>
              <p>{{ addr.phone }}</p>
              <p>{{ addr.address1 }}</p>
              <p>{{ addr.city }}, {{ addr.stateName }} {{ addr.zipCode }}</p>
              <p>{{ addr.countryName }}</p>
              <div class="address-actions">
                <button @click="editAddress(addr)">Edit</button>
                <button v-if="addr.isDefault !== 1" @click="setDefaultAddress(addr.id)">Set Default</button>
                <button class="btn-danger" @click="deleteAddress(addr.id)">Delete</button>
              </div>
            </div>
          </div>

          <!-- 新增/编辑地址表单 -->
          <div v-if="showAddressForm" class="address-form">
            <h3>{{ editingAddress ? 'Edit Address' : 'Add New Address' }}</h3>

            <form @submit.prevent="saveAddress">
              <div class="form-row">
                <div class="form-group">
                  <label>First Name *</label>
                  <input v-model="addressForm.firstName" required />
                </div>
                <div class="form-group">
                  <label>Last Name *</label>
                  <input v-model="addressForm.lastName" required />
                </div>
              </div>

              <div class="form-group">
                <label>Phone *</label>
                <input v-model="addressForm.phone" required />
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Country *</label>
                  <select v-model="addressForm.countryCode" required>
                    <option value="">Select</option>
                    <option value="US">United States</option>
                    <option value="CN">China</option>
                    <option value="UK">United Kingdom</option>
                    <option value="DE">Germany</option>
                    <option value="FR">France</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>State/Province</label>
                  <input v-model="addressForm.stateName" />
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>City *</label>
                  <input v-model="addressForm.city" required />
                </div>
                <div class="form-group">
                  <label>Zip Code</label>
                  <input v-model="addressForm.zipCode" />
                </div>
              </div>

              <div class="form-group">
                <label>Address *</label>
                <input v-model="addressForm.address1" required />
              </div>

              <div class="form-group">
                <label>Address Line 2</label>
                <input v-model="addressForm.address2" />
              </div>

              <div class="form-actions">
                <button type="button" class="btn-secondary" @click="showAddressForm = false">Cancel</button>
                <button type="submit" class="btn-primary">Save Address</button>
              </div>
            </form>
          </div>

          <button v-if="!showAddressForm" class="btn-primary" @click="showAddressForm = true">
            + Add New Address
          </button>
        </section>

        <!-- 安全设置 -->
        <section v-if="activeTab === 'security'" class="tab-content">
          <h2>Security Settings</h2>

          <form @submit.prevent="changePassword" class="security-form">
            <div class="form-group">
              <label>Current Password</label>
              <input v-model="passwordForm.current" type="password" required />
            </div>

            <div class="form-group">
              <label>New Password</label>
              <input v-model="passwordForm.newPassword" type="password" required minlength="6" />
            </div>

            <div class="form-group">
              <label>Confirm New Password</label>
              <input v-model="passwordForm.confirm" type="password" required />
            </div>

            <button type="submit" class="btn-primary">Change Password</button>
          </form>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
interface User {
  id: number
  username: string
  email: string
  phone?: string
  nickname?: string
  firstName?: string
  lastName?: string
}

interface Address {
  id: number
  firstName: string
  lastName: string
  phone: string
  countryCode: string
  countryName?: string
  stateName?: string
  city: string
  address1: string
  address2?: string
  zipCode?: string
  isDefault: number
}

interface Order {
  id: number
  orderNo: string
  status: number
  createTime: string
  totalAmount: number
  itemCount?: number
}

const tabs = [
  { id: 'info', name: 'Personal Info', icon: '👤' },
  { id: 'orders', name: 'My Orders', icon: '📦' },
  { id: 'addresses', name: 'Addresses', icon: '📍' },
  { id: 'security', name: 'Security', icon: '🔒' }
]

const activeTab = ref('info')
const user = ref<User | null>(null)
const profileForm = reactive({
  username: '',
  email: '',
  firstName: '',
  lastName: '',
  phone: '',
  nickname: ''
})

const orders = ref<Order[]>([])
const loadingOrders = ref(false)

const addresses = ref<Address[]>([])
const showAddressForm = ref(false)
const editingAddress = ref<Address | null>(null)
const addressForm = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  countryCode: '',
  stateName: '',
  city: '',
  address1: '',
  address2: '',
  zipCode: ''
})

const passwordForm = reactive({
  current: '',
  newPassword: '',
  confirm: ''
})

const saving = ref(false)

// 计算属性
const userInitials = computed(() => {
  if (!user.value) return 'U'
  if (user.value.firstName && user.value.lastName) {
    return user.value.firstName[0] + user.value.lastName[0]
  }
  return (user.value.username || 'U')[0].toUpperCase()
})

// Methods
function getOrderStatus(status: number) {
  const statusMap: Record<number, string> = {
    0: 'Pending',
    1: 'Paid',
    2: 'Shipped',
    3: 'Delivered',
    4: 'Completed',
    5: 'Cancelled',
    6: 'Refunding',
    7: 'Refunded'
  }
  return statusMap[status] || 'Unknown'
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString()
}

async function handleLogout() {
  const token = useCookie('token')
  token.value = null
  navigateTo('/')
}

// Load data
onMounted(async () => {
  await Promise.all([loadUser(), loadOrders(), loadAddresses()])
})

async function loadUser() {
  try {
    const { data } = await $fetch('/api/user/profile', {
      headers: authHeaders.value
    })
    if (data.value) {
      user.value = data.value
      Object.assign(profileForm, data.value)
    }
  } catch (e) {
    console.error('Failed to load user', e)
  }
}

async function loadOrders() {
  loadingOrders.value = true
  try {
    const { data } = await $fetch('/api/orders', {
      headers: authHeaders.value
    })
    if (data.value?.list) {
      orders.value = data.value.list
    }
  } catch (e) {
    console.error('Failed to load orders', e)
  } finally {
    loadingOrders.value = false
  }
}

async function loadAddresses() {
  try {
    const { data } = await $fetch('/api/user/addresses', {
      headers: authHeaders.value
    })
    if (data.value) {
      addresses.value = data.value
    }
  } catch (e) {
    console.error('Failed to load addresses', e)
  }
}

async function updateProfile() {
  saving.value = true
  try {
    await $fetch('/api/user/profile', {
      method: 'PUT',
      headers: authHeaders.value,
      body: profileForm
    })
    alert('Profile updated successfully')
  } catch (e: any) {
    alert(e.message || 'Failed to update profile')
  } finally {
    saving.value = false
  }
}

async function saveAddress() {
  try {
    if (editingAddress.value) {
      await $fetch(`/api/user/addresses/${editingAddress.value.id}`, {
        method: 'PUT',
        headers: authHeaders.value,
        body: addressForm
      })
    } else {
      await $fetch('/api/user/addresses', {
        method: 'POST',
        headers: authHeaders.value,
        body: addressForm
      })
    }
    showAddressForm.value = false
    await loadAddresses()
  } catch (e: any) {
    alert(e.message || 'Failed to save address')
  }
}

function editAddress(addr: Address) {
  Object.assign(addressForm, addr)
  editingAddress.value = addr
  showAddressForm.value = true
}

async function deleteAddress(id: number) {
  if (!confirm('Are you sure you want to delete this address?')) return

  try {
    await $fetch(`/api/user/addresses/${id}`, {
      method: 'DELETE',
      headers: authHeaders.value
    })
    await loadAddresses()
  } catch (e: any) {
    alert(e.message || 'Failed to delete address')
  }
}

async function setDefaultAddress(id: number) {
  // 实现设置默认地址逻辑
}

async function cancelOrder(orderId: number) {
  if (!confirm('Are you sure you want to cancel this order?')) return

  try {
    await $fetch(`/api/orders/${orderId}/cancel`, {
      method: 'POST',
      headers: authHeaders.value
    })
    await loadOrders()
  } catch (e: any) {
    alert(e.message || 'Failed to cancel order')
  }
}

function changePassword() {
  if (passwordForm.newPassword !== passwordForm.confirm) {
    alert('Passwords do not match')
    return
  }
  // 调用修改密码API
}

const authHeaders = computed(() => {
  const token = useCookie('token')
  return {
    Authorization: token.value ? `Bearer ${token.value}` : '',
    'Content-Type': 'application/json'
  }
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.profile-container {
  max-width: 1100px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

.profile-sidebar {
  background: white;
  border-radius: 12px;
  padding: 24px;
  height: fit-content;
}

.user-info {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.avatar {
  width: 80px;
  height: 80px;
  background: #007bff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  margin: 0 auto 12px;
}

.user-info h3 {
  margin: 0 0 4px;
}

.user-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.sidebar-nav {
  padding: 20px 0;
}

.sidebar-nav a {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #333;
  text-decoration: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.sidebar-nav a:hover {
  background: #f5f5f5;
}

.sidebar-nav a.active {
  background: #e6f0ff;
  color: #007bff;
}

.tab-icon {
  font-size: 18px;
}

.logout-btn {
  width: 100%;
  padding: 12px;
  background: #fee;
  color: #c00;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.profile-main {
  background: white;
  border-radius: 12px;
  padding: 24px;
}

.tab-content h2 {
  margin: 0 0 24px;
  font-size: 22px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.btn-primary {
  background: #007bff;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.btn-secondary {
  background: white;
  color: #007bff;
  border: 1px solid #007bff;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}

.btn-danger {
  background: #fee;
  color: #c00;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}

/* 订单列表 */
.order-card {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 16px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-0 { background: #fef3cd; color: #856404; }
.status-1 { background: #cce5ff; color: #004085; }
.status-2 { background: #d4edda; color: #155724; }
.status-5 { background: #f8d7da; color: #721c24; }

.order-details p {
  margin: 4px 0;
  font-size: 14px;
}

.order-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

/* 地址列表 */
.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.address-card {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
  margin-bottom: 8px;
}

.default-badge {
  background: #007bff;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.address-card p {
  margin: 4px 0;
  font-size: 14px;
}

.address-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.address-actions button {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 14px;
}

.address-form {
  margin-top: 24px;
  padding: 24px;
  background: #f9f9f9;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
}
</style>