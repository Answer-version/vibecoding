export const useApi = () => {
  const config = useRuntimeConfig()
  const token = useCookie('token')

  const headers = computed(() => ({
    Authorization: token.value ? `Bearer ${token.value}` : '',
    'Content-Type': 'application/json'
  }))

  async function get<T>(url: string, params?: Record<string, any>) {
    return useFetch<T>(url, {
      baseURL: config.public.apiBase,
      method: 'GET',
      headers: headers.value,
      query: params
    })
  }

  async function post<T>(url: string, body?: any) {
    return useFetch<T>(url, {
      baseURL: config.public.apiBase,
      method: 'POST',
      headers: headers.value,
      body
    })
  }

  async function put<T>(url: string, body?: any) {
    return useFetch<T>(url, {
      baseURL: config.public.apiBase,
      method: 'PUT',
      headers: headers.value,
      body
    })
  }

  async function del<T>(url: string) {
    return useFetch<T>(url, {
      baseURL: config.public.apiBase,
      method: 'DELETE',
      headers: headers.value
    })
  }

  return { get, post, put, del }
}

export const useAuth = () => {
  const api = useApi()
  const token = useCookie('token')
  const user = useState('user', () => null)

  async function login(username: string, password: string) {
    const { data } = await api.post<{ token: string }>('/auth/login', { username, password })
    if (data.value?.token) {
      token.value = data.value.token
    }
    return data
  }

  function logout() {
    token.value = null
    user.value = null
    navigateTo('/auth/login')
  }

  return { token, user, login, logout }
}