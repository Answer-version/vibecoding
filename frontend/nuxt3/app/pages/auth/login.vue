<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="auth-title">{{ getTitle() }}</h1>
        <p class="auth-subtitle">{{ getSubtitle() }}</p>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-alert">
          {{ errorMessage }}
        </div>

        <!-- Tab 切换 -->
        <div class="auth-tabs">
          <button :class="{ active: loginType === 'password' }" @click="loginType = 'password'">
            {{ locale === 'zh' ? '密码登录' : 'Password' }}
          </button>
          <button :class="{ active: loginType === 'phone' }" @click="loginType = 'phone'">
            {{ locale === 'zh' ? '手机验证码' : 'Phone Code' }}
          </button>
        </div>

        <!-- 密码登录 -->
        <form v-if="loginType === 'password'" @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label>{{ locale === 'zh' ? '用户名/邮箱' : 'Username / Email' }}</label>
            <input v-model="loginForm.username" type="text" :placeholder="locale === 'zh' ? '请输入用户名或邮箱' : 'Enter username or email'" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '密码' : 'Password' }}</label>
            <input v-model="loginForm.password" type="password" :placeholder="locale === 'zh' ? '请输入密码' : 'Enter password'" required :disabled="loading" />
          </div>

          <div class="form-actions">
            <NuxtLink v-if="locale === 'zh'" to="/auth/forgot-password" class="forgot-link">{{ locale === 'zh' ? '忘记密码？' : 'Forgot Password?' }}</NuxtLink>
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? (locale === 'zh' ? '登录中...' : 'Signing in...') : (locale === 'zh' ? '登录' : 'Sign In') }}
          </button>
        </form>

        <!-- 手机验证码登录 -->
        <form v-else @submit.prevent="handlePhoneLogin" class="auth-form">
          <div class="form-group">
            <label>{{ locale === 'zh' ? '手机号' : 'Phone' }}</label>
            <input v-model="phoneForm.phone" type="tel" :placeholder="locale === 'zh' ? '请输入手机号' : 'Enter phone number'" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '验证码' : 'Verification Code' }}</label>
            <div class="code-input">
              <input v-model="phoneForm.code" type="text" :placeholder="locale === 'zh' ? '请输入验证码' : 'Enter code'" required :disabled="loading" />
              <button type="button" class="btn-code" @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? countdown + 's' : (locale === 'zh' ? '获取验证码' : 'Send Code') }}
              </button>
            </div>
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? (locale === 'zh' ? '登录中...' : 'Signing in...') : (locale === 'zh' ? '登录' : 'Sign In') }}
          </button>
        </form>

        <!-- 切换登录/注册 -->
        <div class="auth-switch">
          {{ locale === 'zh' ? '没有账号？' : "Don't have an account?" }}
          <a href="#" @click.prevent="navigateTo('/auth/register')">{{ locale === 'zh' ? '注册' : 'Sign Up' }}</a>
        </div>

        <!-- 返回首页 -->
        <div class="auth-back">
          <NuxtLink to="/">← {{ locale === 'zh' ? '返回首页' : 'Back to Home' }}</NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { locale } = useI18n()
const router = useRouter()

const loginType = ref('password') // password | phone
const loading = ref(false)
const errorMessage = ref('')
const countdown = ref(0)

const loginForm = reactive({
  username: '',
  password: ''
})

const phoneForm = reactive({
  phone: '',
  code: ''
})

function getTitle() {
  if (locale.value === 'zh') return '欢迎回来'
  return 'Welcome Back'
}

function getSubtitle() {
  if (locale.value === 'zh') return '登录后继续购物'
  return 'Sign in to continue shopping'
}

async function handleLogin() {
  errorMessage.value = ''
  loading.value = true

  try {
    const { data, error } = await useAuthApi().login(loginForm.username, loginForm.password)

    if (error.value) {
      errorMessage.value = error.value?.data?.message || (locale.value === 'zh' ? '登录失败，请重试' : 'Login failed. Please try again.')
    } else {
      router.push('/')
    }
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '登录时发生错误' : 'An error occurred')
  } finally {
    loading.value = false
  }
}

async function sendCode() {
  if (!phoneForm.phone) {
    errorMessage.value = locale.value === 'zh' ? '请输入手机号' : 'Please enter phone number'
    return
  }

  try {
    await $fetch('/auth/phone/send', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      params: { phone: phoneForm.phone, type: 'login' }
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

async function handlePhoneLogin() {
  errorMessage.value = ''
  loading.value = true

  try {
    const { data, error } = await $fetch('/auth/phone/login', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      body: { phone: phoneForm.phone, code: phoneForm.code }
    })

    if (error.value) {
      errorMessage.value = error.value?.data?.message || (locale.value === 'zh' ? '登录失败' : 'Login failed')
    } else if (data.value?.data?.token) {
      const token = useCookie('token')
      token.value = data.value.data.token
      router.push('/')
    }
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '登录时发生错误' : 'An error occurred')
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

.auth-container {
  width: 100%;
  max-width: 420px;
}

.auth-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.auth-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a1a1a;
  text-align: center;
}

.auth-subtitle {
  color: #666;
  text-align: center;
  margin-bottom: 24px;
}

.auth-tabs {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid #eee;
}

.auth-tabs button {
  flex: 1;
  padding: 12px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.auth-tabs button.active {
  color: #007bff;
  border-bottom-color: #007bff;
}

.error-alert {
  background: #fee;
  color: #c00;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.form-group input:focus {
  outline: none;
  border-color: #007bff;
}

.form-group input:disabled {
  background: #f5f5f5;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.forgot-link {
  font-size: 14px;
  color: #007bff;
  text-decoration: none;
}

.code-input {
  display: flex;
  gap: 10px;
}

.code-input input {
  flex: 1;
}

.btn-code {
  padding: 12px 16px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
}

.btn-code:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  padding: 14px 24px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-primary:disabled {
  background: #ccc;
}

.auth-switch {
  margin-top: 24px;
  text-align: center;
  color: #666;
}

.auth-switch a {
  color: #007bff;
  text-decoration: none;
  font-weight: 500;
}

.auth-back {
  margin-top: 16px;
  text-align: center;
}

.auth-back a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}
</style>
