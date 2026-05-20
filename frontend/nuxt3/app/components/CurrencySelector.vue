<template>
  <div class="currency-selector">
    <select v-model="currentCurrency" @change="changeCurrency">
      <option value="USD">USD ($)</option>
      <option value="EUR">EUR (€)</option>
      <option value="GBP">GBP (£)</option>
      <option value="CNY">CNY (¥)</option>
    </select>
  </div>
</template>

<script setup lang="ts">
const currentCurrency = useCookie('currency', { default: () => 'USD' })

function changeCurrency() {
  // 可以添加价格转换API调用
  // 当前使用本地简单转换
}
</script>

<style scoped>
.currency-selector select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: #fff;
  cursor: pointer;
}
</style>