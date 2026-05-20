<template>
  <div class="cookie-consent" v-if="!dismissed">
    <div class="consent-content">
      <p>We use cookies to enhance your browsing experience and analyze our traffic. By clicking "Accept", you agree to our use of cookies.</p>
      <div class="consent-links">
        <NuxtLink to="/privacy">Privacy Policy</NuxtLink>
        <NuxtLink to="/terms">Terms of Service</NuxtLink>
      </div>
    </div>
    <div class="consent-buttons">
      <button @click="accept" class="btn-accept">Accept</button>
      <button @click="decline" class="btn-decline">Decline</button>
    </div>
  </div>
</template>

<script setup lang="ts">
const consent = useCookie('cookie_consent')
const dismissed = ref(false)

onMounted(() => {
  if (consent.value) {
    dismissed.value = true
  }
})

function accept() {
  consent.value = 'accepted'
  dismissed.value = true
}

function decline() {
  consent.value = 'declined'
  dismissed.value = true
}
</script>

<style scoped>
.cookie-consent {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 20px;
  box-shadow: 0 -4px 12px rgba(0,0,0,0.1);
  z-index: 9999;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}
.consent-content {
  flex: 1;
}
.consent-content p {
  margin: 0 0 10px;
  color: #666;
  font-size: 14px;
}
.consent-links {
  display: flex;
  gap: 15px;
}
.consent-links a {
  color: #667eea;
  font-size: 14px;
}
.consent-buttons {
  display: flex;
  gap: 10px;
}
.btn-accept {
  padding: 10px 20px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.btn-decline {
  padding: 10px 20px;
  background: #fff;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
}
</style>