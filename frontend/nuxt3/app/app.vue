<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
    <CookieConsent />
    <PromoPopup />
  </div>
</template>

<script setup lang="ts">
import CookieConsent from '~/components/CookieConsent.vue'
import PromoPopup from '~/components/PromoPopup.vue'
</script>