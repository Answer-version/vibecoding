<template>
  <div class="customers">
    <el-table :data="customers" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="phone" label="Phone" />
      <el-table-column prop="userType" label="Type" width="80">
        <template #default="{ row }">{{ row.userType === 1 ? 'B2C' : 'B2B' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="Status" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const customers = ref([])

onMounted(() => {
  // Load customers
})
</script>