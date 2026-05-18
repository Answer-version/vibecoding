<template>
  <div class="products">
    <div class="toolbar">
      <el-button type="primary" @click="$router.push('/products/create')">
        <el-icon><Plus /></el-icon> Add Product
      </el-button>
    </div>

    <el-table :data="products" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="Image" width="80">
        <template #default="{ row }">
          <img :src="row.image" style="width: 50px; height: 50px; object-fit: cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="Product Name" />
      <el-table-column prop="productCode" label="Code" width="120" />
      <el-table-column prop="categoryName" label="Category" width="120" />
      <el-table-column prop="usdPrice" label="USD Price" width="100">
        <template #default="{ row }">${{ row.usdPrice }}</template>
      </el-table-column>
      <el-table-column prop="stockQuantity" label="Stock" width="80" />
      <el-table-column prop="status" label="Status" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? 'Active' : 'Inactive' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/products/${row.id}/edit`)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      style="margin-top: 20px; justify-content: center"
      @current-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'
import { Plus } from '@element-plus/icons-vue'

const products = ref([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

async function loadData() {
  const { data } = await api.getProducts({ page: page.value, pageSize: pageSize.value })
  products.value = data.records || []
  total.value = data.total || 0
}

async function handleDelete(id) {
  await ElMessageBox.confirm('Are you sure to delete this product?', 'Warning', { type: 'warning' })
  await api.deleteProduct(id)
  ElMessage.success('Deleted successfully')
  loadData()
}

loadData()
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>