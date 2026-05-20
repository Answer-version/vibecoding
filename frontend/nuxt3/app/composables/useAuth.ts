// 认证相关API
export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  phone?: string
}

export interface AuthResponse {
  code: number
  message: string
  data: {
    token: string
    userId: number
    username: string
    email?: string
    nickname?: string
    userType?: number
  }
}

export interface UserProfile {
  id: number
  username: string
  email: string
  phone?: string
  nickname?: string
  firstName?: string
  lastName?: string
  avatar?: string
  userType: number
  status: number
}

export const useAuthApi = () => {
  const api = useApi()
  const token = useCookie('token')
  const user = useState<UserProfile | null>('user', () => null)

  // 检查是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function login(username: string, password: string) {
    const { data, error } = await api.post<AuthResponse>('/auth/login', { username, password })

    if (data.value?.data?.token) {
      token.value = data.value.data.token
      // 获取用户信息
      await fetchProfile()
    }

    return { data: data.value, error: error.value }
  }

  // 注册
  async function register(request: RegisterRequest) {
    const { data, error } = await api.post<AuthResponse>('/auth/register', request)

    if (data.value?.data?.token) {
      token.value = data.value.data.token
    }

    return { data: data.value, error: error.value }
  }

  // 刷新Token
  async function refreshToken() {
    if (!token.value) return { data: null, error: null }

    const { data, error } = await api.post<{ code: number, data: { token: string } }>('/auth/refresh', {})

    if (data.value?.data?.token) {
      token.value = data.value.data.token
    }

    return { data: data.value, error: error.value }
  }

  // 获取用户信息
  async function fetchProfile() {
    if (!token.value) return { data: null }

    const { data, error } = await api.get<UserProfile>('/user/profile')

    if (!error.value && data.value?.data) {
      user.value = data.value.data
    }

    return { data: data.value, error: error.value }
  }

  // 更新用户信息
  async function updateProfile(profile: Partial<UserProfile>) {
    const { data, error } = await api.put<{ code: number }>('/user/profile', profile)

    if (!error.value) {
      await fetchProfile()
    }

    return { data: data.value, error: error.value }
  }

  // 登出
  function logout() {
    token.value = null
    user.value = null
    navigateTo('/')
  }

  // 检查需要认证的路由
  function requireAuth() {
    if (!isLoggedIn.value) {
      navigateTo('/auth/login')
      return false
    }
    return true
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    register,
    refreshToken,
    fetchProfile,
    updateProfile,
    logout,
    requireAuth
  }
}