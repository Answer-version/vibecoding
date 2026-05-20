<template>
  <div class="static-page container">
    <h1>{{ title }}</h1>
    <div class="content" v-html="content"></div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const slug = route.params.slug as string

const pages: Record<string, { title: string; content: string }> = {
  'privacy': {
    title: 'Privacy Policy',
    content: `
      <h2>Privacy Policy</h2>
      <p>This Privacy Policy describes how we collect, use, and disclose your personal information.</p>
      <h3>Information We Collect</h3>
      <p>We collect information you provide directly, such as when you create an account, place an order, or contact us.</p>
      <h3>How We Use Your Information</h3>
      <p>We use your information to provide and improve our services, process your orders, and communicate with you.</p>
      <h3>Contact Us</h3>
      <p>If you have any questions, please contact us at support@vibecomerce.com</p>
    `
  },
  'terms': {
    title: 'Terms of Service',
    content: `
      <h2>Terms of Service</h2>
      <p>By accessing and using this website, you accept and agree to be bound by the terms and provision of this agreement.</p>
      <h3>Use License</h3>
      <p>Permission is granted to temporarily use this website for personal, non-commercial use only.</p>
      <h3>Account Registration</h3>
      <p>You agree to provide accurate and complete information when creating an account.</p>
    `
  },
  'shipping': {
    title: 'Shipping Policy',
    content: `
      <h2>Shipping Policy</h2>
      <p>We ship to over 100 countries worldwide.</p>
      <h3>Processing Time</h3>
      <p>Orders are processed within 1-2 business days.</p>
      <h3>Shipping Time</h3>
      <p>International shipping typically takes 7-21 business days.</p>
      <h3>Free Shipping</h3>
      <p>Free shipping is available on orders over $50.</p>
    `
  },
  'returns': {
    title: 'Return Policy',
    content: `
      <h2>Return Policy</h2>
      <p>We offer a 30-day return policy for most items.</p>
      <h3>Eligibility</h3>
      <p>Items must be unused and in original packaging.</p>
      <h3>Refunds</h3>
      <p>Refunds are processed within 5-10 business days after receiving the return.</p>
    `
  }
}

const page = pages[slug] || { title: 'Not Found', content: '<p>Page not found.</p>' }
const title = page.title
const content = page.content
</script>

<style scoped>
.static-page {
  padding: 60px 20px;
  max-width: 800px;
  margin: 0 auto;
}
.static-page h1 {
  margin-bottom: 30px;
}
.content :deep(h2) {
  margin: 30px 0 15px;
  color: #333;
}
.content :deep(h3) {
  margin: 20px 0 10px;
  color: #333;
}
.content :deep(p) {
  color: #666;
  line-height: 1.8;
}
</style>