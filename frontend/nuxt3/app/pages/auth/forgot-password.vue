<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="auth-title">{{ locale === 'zh' ? '忘记密码' : 'Forgot Password' }}</h1>
        <p class="auth-subtitle">{{ locale === 'zh' ? '输入手机号重置密码' : 'Enter your phone number to reset password' }}</p>

        <div v-if="errorMessage" class="error-alert">{{ errorMessage }}</div>
        <div v-if="successMessage" class="success-alert">{{ successMessage }}</div>

        <form v-if="!resetSuccess" @submit.prevent="handleSendCode" class="auth-form">
          <div class="form-group">
            <label>{{ locale === 'zh' ? '手机号' : 'Phone Number' }}</label>
            <input v-model="phone" type="tel" :placeholder="locale === 'zh' ? '请输入注册手机号' : 'Enter your registered phone number'" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '验证码' : 'Verification Code' }}</label>
            <div class="code-input">
              <input v-model="code" type="text" :placeholder="locale === 'zh' ? '请输入验证码' : 'Enter code'" required :disabled="loading" />
              <button type="button" class="btn-code" @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? countdown + 's' : (locale === 'zh' ? '获取验证码' : 'Send Code') }}
              </button>
            </div>
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '新密码' : 'New Password' }}</label>
            <input v-model="newPassword" type="password" :placeholder="locale === 'zh' ? '请输入新密码' : 'Enter new password'" minlength="6" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '确认新密码' : 'Confirm New Password' }}</label>
            <input v-model="confirmPassword" type="password" :placeholder="locale === 'zh' ? '请再次输入新密码' : 'Confirm new password'" minlength="6" required :disabled="loading" />
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? (locale === 'zh' ? '处理中...' : 'Processing...') : (locale === 'zh' ? '重置密码' : 'Reset Password') }}
          </button>
        </form>

        <div v-else class="success-box">
          <div class="success-icon">✓</div>
          <p>{{ locale === 'zh' ? '密码重置成功！' : 'Password reset successfully!' }}</p>
          <NuxtLink to="/auth/login" class="btn-primary">{{ locale === 'zh' ? '去登录' : 'Go to Login' }}</NuxtLink>
        </div>

        <div class="auth-back">
          <NuxtLink to="/auth/login">← {{ locale === 'zh' ? '返回登录' : 'Back to Login' }}</NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()
const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const countdown = ref(0)
const resetSuccess = ref(false)

const phone = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

async function sendCode() {
  if (!phone.value) {
    errorMessage.value = locale.value === 'zh' ? '请输入手机号' : 'Please enter phone number'
    return
  }

  try {
    await $fetch('/auth/password/send-code', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      params: { phone: phone.value }
    })

    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '发送失败' : 'Send failed')
  }
}

async function handleSendCode() {
  errorMessage.value = ''

  if (newPassword.value !== confirmPassword.value) {
    errorMessage.value = locale.value === 'zh' ? '两次密码不一致' : 'Passwords do not match'
    return
  }

  loading.value = true

  try {
    const { data, error } = await $fetch('/auth/password/reset', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      body: {
        phone: phone.value,
        code: code.value,
        newPassword: newPassword.value
      }
    })

    if (error.value) {
      errorMessage.value = error.value?.data?.message || (locale.value === 'zh' ? '重置失败' : 'Reset failed')
    } else {
      resetSuccess.value = true
    }
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '重置时发生错误' : 'An error occurred')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  padding: 20px;
}
.auth-container { width: 100%; max-width: 420px; }
.auth-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.auth-title { font-size: 28px; font-weight: 600; margin-bottom: 8px; text-align: center; }
.auth-subtitle { color: #666; text-align: center; margin-bottom: 24px; }
.error-alert { background: #fee; color: #c00; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; }
.success-alert { background: #efe; color: #060; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; }
.auth-form { display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 14px; font-weight: 500; color: #333; }
.form-group input { padding: 12px 16px; border: 1px solid #ddd; border-radius: 8px; font-size: 16px; }
.form-group input:focus { outline: none; border-color: #007bff; }
.code-input { display: flex; gap: 10px; }
.code-input input { flex: 1; }
.btn-code { padding: 12px 16px; background: #f5f5f5; border: 1px solid #ddd; border-radius: 8px; cursor: pointer; }
.btn-code:disabled { opacity: 0.6; }
.btn-primary { padding: 14px 24px; background: #007bff; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; text-align: center; text-decoration: none; display: block; }
.btn-primary:disabled { background: #ccc; }
.auth-back { margin-top: 24px; text-align: center; }
.auth-back a { color: #666; text-decoration: none; font-size: 14px; }
.success-box { text-align: center; padding: 20px 0; }
.success-icon { width: 60px; height: 60px; background: #4caf50; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 30px; margin: 0 auto 20px; }
.success-box p { font-size: 18px; margin-bottom: 20px; }
</style>
