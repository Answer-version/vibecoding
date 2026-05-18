<template>
  <el-form :model="form" label-width="120px" style="max-width: 800px">
    <el-card header="Basic Info">
      <el-form-item label="Product Name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="Product Code">
        <el-input v-model="form.productCode" />
      </el-form-item>
      <el-form-item label="Category">
        <el-select v-model="form.categoryId" placeholder="Select">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="Brand">
        <el-select v-model="form.brandId" placeholder="Select">
          <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
      </el-form-item>
    </el-card>

    <el-card header="Price & Stock" style="margin-top: 20px">
      <el-form-item label="USD Price">
        <el-input-number v-model="form.usdPrice" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="Cost Price">
        <el-input-number v-model="form.costPrice" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="Stock">
        <el-input-number v-model="form.stockQuantity" :min="0" />
      </el-form-item>
      <el-form-item label="Status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">Active</el-radio>
          <el-radio :label="0">Inactive</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-card>

    <el-form-item style="margin-top: 20px">
      <el-button type="primary" @click="handleSave">Save</el-button>
      <el-button @click="$router.back()">Cancel</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const route = useRoute()
const isEdit = route.params.id ? true : false

const form = reactive({
  name: '',
  productCode: '',
  categoryId: null,
  brandId: null,
  usdPrice: 0,
  costPrice: 0,
  stockQuantity: 0,
  status: 1
})

const categories = ref([])
const brands = ref([])

async function handleSave() {
  try {
    if (isEdit) {
      await api.updateProduct(route.params.id, form)
    } else {
      await api.createProduct(form)
    }
    ElMessage.success('Saved successfully')
    router.push('/products')
  } catch (e) {
    ElMessage.error('Save failed')
  }
}

onMounted(async () => {
  if (isEdit) {
    const { data } = await api.getProduct(route.params.id)
    Object.assign(form, data)
  }
})
</script>