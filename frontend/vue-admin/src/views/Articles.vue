<template>
  <div class="articles">
    <div class="toolbar">
      <el-button type="primary" @click="handleCreate">Add Article</el-button>
    </div>

    <el-table :data="articles" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="articleTitle" label="Title" />
      <el-table-column prop="categoryName" label="Category" width="120" />
      <el-table-column prop="viewCount" label="Views" width="80" />
      <el-table-column prop="status" label="Status" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? 'Published' : 'Draft' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row.id)">Edit</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const articles = ref([])

async function handleCreate() {
  // TODO: navigate to create page
}

function handleEdit(id) {
  // TODO: navigate to edit page
}

onMounted(() => {
  // Load articles
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>