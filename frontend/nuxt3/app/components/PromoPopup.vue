<template>
  <Teleport to="body">
    <div class="modal-overlay" v-if="show" @click="close">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="close">×</button>
        <div class="modal-body">
          <h2>{{ t('welcome') }}</h2>
          <p class="subtitle">{{ t('subtitle') }}</p>
          <div class="offer">
            <p class="offer-title">🎉 New User Offer!</p>
            <p class="offer-code">Code: <strong>WELCOME20</strong></p>
            <p class="offer-desc">Get 20% off your first order</p>
          </div>
          <button @click="goShop" class="btn-shop">{{ t('shopNow') }}</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
const { t } = useI18n()
const show = ref(false)
const dismissed = useCookie('popup_dismissed')

onMounted(() => {
  // 首次访问显示弹窗，且当天未拒绝过
  const isFirst = !dismissed.value
  const today = new Date().toDateString()
  const lastShown = useCookie('popup_shown_date').value

  if (isFirst || lastShown !== today) {
    // 延迟2秒显示
    setTimeout(() => {
      show.value = true
      useCookie('popup_shown_date').value = today
    }, 2000)
  }
})

function close() {
  show.value = false
  dismissed.value = 'true'
}

function goShop() {
  close()
  navigateTo('/products')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-content {
  background: #fff;
  border-radius: 12px;
  max-width: 450px;
  width: 90%;
  position: relative;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
}
.close-btn {
  position: absolute;
  top: 10px;
  right: 15px;
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}
.modal-body {
  padding: 40px;
  text-align: center;
}
.modal-body h2 {
  margin-bottom: 10px;
  color: #333;
}
.subtitle {
  color: #666;
  margin-bottom: 20px;
}
.offer {
  background: #f8f9ff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}
.offer-title {
  font-size: 20px;
  margin-bottom: 10px;
}
.offer-code {
  font-size: 24px;
  color: #667eea;
  margin-bottom: 5px;
}
.offer-desc {
  color: #666;
}
.btn-shop {
  width: 100%;
  padding: 15px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style>