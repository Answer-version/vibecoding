<template>
  <div class="contact-page container">
    <h1>Contact Us</h1>

    <div class="contact-content">
      <div class="contact-info">
        <h2>Get in Touch</h2>
        <p>We'd love to hear from you. Here's how you can reach us:</p>

        <div class="info-item">
          <h3>📧 Email</h3>
          <p>support@vibecomerce.com</p>
        </div>

        <div class="info-item">
          <h3>📞 Phone</h3>
          <p>+1 (555) 123-4567</p>
          <p class="note">Mon-Fri, 9am-6pm EST</p>
        </div>

        <div class="info-item">
          <h3>📍 Address</h3>
          <p>123 Commerce Street</p>
          <p>New York, NY 10001</p>
        </div>
      </div>

      <div class="contact-form">
        <h2>Send a Message</h2>
        <form @submit="sendMessage">
          <input v-model="form.name" placeholder="Your Name" required />
          <input v-model="form.email" type="email" placeholder="Your Email" required />
          <input v-model="form.subject" placeholder="Subject" required />
          <textarea v-model="form.message" placeholder="Your Message" rows="5" required></textarea>
          <button type="submit">Send Message</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const form = ref({ name: '', email: '', subject: '', message: '' })

function sendMessage() {
  alert('Thank you! We will get back to you soon.')
  form.value = { name: '', email: '', subject: '', message: '' }
}
</script>

<style scoped>
.contact-page {
  padding: 60px 20px;
  max-width: 1000px;
  margin: 0 auto;
}
.contact-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  margin-top: 40px;
}
.info-item {
  margin-bottom: 25px;
}
.info-item h3 {
  margin-bottom: 5px;
  color: #333;
}
.info-item p {
  color: #666;
  margin: 0;
}
.note {
  font-size: 14px;
  color: #999;
}
.contact-form form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.contact-form input, .contact-form textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}
.contact-form button {
  padding: 12px;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}
@media (max-width: 768px) {
  .contact-content {
    grid-template-columns: 1fr;
  }
}
</style>