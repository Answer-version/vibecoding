<template>
  <div class="orders">
    <el-table :data="orders" border style="width: 100%">
      <el-table-column prop="orderNo" label="Order No" width="160" />
      <el-table-column prop="userId" label="Customer ID" width="100" />
      <el-table-column prop="totalAmount" label="Amount" width="100">
        <template #default="{ row }">${{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="Status" width="100">
        <template #default="{ row }">
          <el-tag>{{ row.orderStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="Created" width="160" />
      <el-table-column label="Actions" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="viewOrder(row)">View</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const orders = ref([])

async function loadData() {
  const { data } = await api.getOrders()
  orders.value = data.records || []
}

function viewOrder(row) {
  console.log('View order:', row)
}

onMounted(loadData)
</script>