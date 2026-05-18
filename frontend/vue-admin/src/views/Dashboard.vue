<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">Total Orders</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">${{ stats.revenue }}</div>
            <div class="stat-label">Revenue</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.products }}</div>
            <div class="stat-label">Products</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.customers }}</div>
            <div class="stat-label">Customers</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card header="Recent Orders">
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="Order No" width="180" />
            <el-table-column prop="userId" label="Customer" width="100" />
            <el-table-column prop="totalAmount" label="Amount">
              <template #default="{ row }">${{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="orderStatus" label="Status" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="Top Products">
          <el-table :data="topProducts" style="width: 100%">
            <el-table-column prop="name" label="Product" />
            <el-table-column prop="saleCount" label="Sales" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const stats = ref({
  totalOrders: 128,
  revenue: 25680,
  products: 256,
  customers: 89
})

const recentOrders = ref([
  { orderNo: 'VC20260518001', userId: 1, totalAmount: 199, orderStatus: 'PAID' },
  { orderNo: 'VC20250517002', userId: 2, totalAmount: 399, orderStatus: 'SHIPPED' }
])

const topProducts = ref([
  { name: 'Product A', saleCount: 120 },
  { name: 'Product B', saleCount: 89 }
])
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}
.stat-label {
  color: #666;
  font-size: 14px;
}
</style>