<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <h1 class="auth-title">{{ locale === 'zh' ? '创建账号' : 'Create Account' }}</h1>
        <p class="auth-subtitle">{{ locale === 'zh' ? '加入我们享受更好的购物体验' : 'Join us for a better shopping experience' }}</p>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="error-alert">{{ errorMessage }}</div>

        <!-- Tab 切换 -->
        <div class="auth-tabs">
          <button :class="{ active: registerType === 'password' }" @click="registerType = 'password'">
            {{ locale === 'zh' ? '密码注册' : 'Password' }}
          </button>
          <button :class="{ active: registerType === 'phone' }" @click="registerType = 'phone'">
            {{ locale === 'zh' ? '手机注册' : 'Phone' }}
          </button>
        </div>

        <!-- 密码注册 -->
        <form v-if="registerType === 'password'" @submit.prevent="handleRegister" class="auth-form">
          <div class="form-group">
            <label>{{ locale === 'zh' ? '用户名' : 'Username' }}</label>
            <input v-model="registerForm.username" type="text" :placeholder="locale === 'zh' ? '请输入用户名' : 'Choose a username'" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '邮箱' : 'Email' }}</label>
            <input v-model="registerForm.email" type="email" :placeholder="locale === 'zh' ? '请输入邮箱' : 'Enter your email'" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '密码' : 'Password' }}</label>
            <input v-model="registerForm.password" type="password" :placeholder="locale === 'zh' ? '创建密码 (至少6位)' : 'Create a password (min 6 characters)'" minlength="6" required :disabled="loading" />
          </div>

          <div class="form-group">
            <label>{{ locale === 'zh' ? '确认密码' : 'Confirm Password' }}</label>
            <input v-model="registerForm.confirmPassword" type="password" :placeholder="locale === 'zh' ? '请再次输入密码' : 'Confirm your password'" required :disabled="loading" />
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? (locale === 'zh' ? '注册中...' : 'Creating...') : (locale === 'zh' ? '注册' : 'Create Account') }}
          </button>
        </form>

        <!-- 手机注册 -->
        <form v-else @submit.prevent="handlePhoneRegister" class="auth-form">
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

          <div class="form-group">
            <label>{{ locale === 'zh' ? '密码' : 'Password' }}</label>
            <input v-model="phoneForm.password" type="password" :placeholder="locale === 'zh' ? '创建密码 (至少6位)' : 'Create a password'" minlength="6" required :disabled="loading" />
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? (locale === 'zh' ? '注册中...' : 'Creating...') : (locale === 'zh' ? '注册' : 'Register') }}
          </button>
        </form>

        <!-- 切换登录 -->
        <div class="auth-switch">
          {{ locale === 'zh' ? '已有账号？' : 'Already have an account?' }}
          <a href="#" @click.prevent="navigateTo('/auth/login')">{{ locale === 'zh' ? '登录' : 'Sign In' }}</a>
        </div>

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

const registerType = ref('password')
const loading = ref(false)
const errorMessage = ref('')
const countdown = ref(0)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const phoneForm = reactive({
  phone: '',
  code: '',
  password: ''
})

async function sendCode() {
  if (!phoneForm.phone) {
    errorMessage.value = locale.value === 'zh' ? '请输入手机号' : 'Please enter phone number'
    return
  }

  try {
    await $fetch('/auth/phone/send', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      params: { phone: phoneForm.phone, type: 'register' }
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

async function handleRegister() {
  errorMessage.value = ''

  if (registerForm.password !== registerForm.confirmPassword) {
    errorMessage.value = locale.value === 'zh' ? '密码不一致' : 'Passwords do not match'
    return
  }

  loading.value = true
  try {
    const { data, error } = await $fetch('/auth/register', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      body: {
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password
      }
    })

    if (error.value) {
      errorMessage.value = error.value?.data?.message || (locale.value === 'zh' ? '注册失败' : 'Registration failed')
    } else if (data.value?.data?.token) {
      const token = useCookie('token')
      token.value = data.value.data.token
      router.push('/')
    }
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '注册时发生错误' : 'An error occurred')
  } finally {
    loading.value = false
  }
}

async function handlePhoneRegister() {
  errorMessage.value = ''

  loading.value = true
  try {
    const { data, error } = await $fetch('/auth/phone/register', {
      baseURL: 'http://localhost:8083',
      method: 'POST',
      body: {
        phone: phoneForm.phone,
        code: phoneForm.code,
        password: phoneForm.password
      }
    })

    if (error.value) {
      errorMessage.value = error.value?.data?.message || (locale.value === 'zh' ? '注册失败' : 'Registration failed')
    } else if (data.value?.data?.token) {
      const token = useCookie('token')
      token.value = data.value.data.token
      router.push('/')
    }
  } catch (e: any) {
    errorMessage.value = e.message || (locale.value === 'zh' ? '注册时发生错误' : 'An error occurred')
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
.auth-tabs { display: flex; margin-bottom: 24px; border-bottom: 1px solid #eee; }
.auth-tabs button { flex: 1; padding: 12px; background: none; border: none; border-bottom: 2px solid transparent; cursor: pointer; font-size: 14px; color: #666; }
.auth-tabs button.active { color: #007bff; border-bottom-color: #007bff; }
.auth-form { display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 14px; font-weight: 500; color: #333; }
.form-group input { padding: 12px 16px; border: 1px solid #ddd; border-radius: 8px; font-size: 16px; }
.form-group input:focus { outline: none; border-color: #007bff; }
.code-input { display: flex; gap: 10px; }
.code-input input { flex: 1; }
.btn-code { padding: 12px 16px; background: #f5f5f5; border: 1px solid #ddd; border-radius: 8px; cursor: pointer; }
.btn-code:disabled { opacity: 0.6; }
.btn-primary { padding: 14px 24px; background: #007bff; color: white; border: none; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; }
.btn-primary:disabled { background: #ccc; }
.auth-switch { margin-top: 24px; text-align: center; color: #666; }
.auth-switch a { color: #007bff; text-decoration: none; font-weight: 500; }
.auth-back { margin-top: 16px; text-align: center; }
.auth-back a { color: #666; text-decoration: none; font-size: 14px; }
</style>
