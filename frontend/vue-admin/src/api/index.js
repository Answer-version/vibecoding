import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1'
})

api.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const message = err.response?.data?.message || 'Request failed'
    ElMessage.error(message)
    return Promise.reject(err)
  }
)

export default {
  // Products
  getProducts(params) {
    return api.get('/admin/products', { params })
  },
  getProduct(id) {
    return api.get(`/admin/products/${id}`)
  },
  createProduct(data) {
    return api.post('/admin/products', data)
  },
  updateProduct(id, data) {
    return api.put(`/admin/products/${id}`, data)
  },
  deleteProduct(id) {
    return api.delete(`/admin/products/${id}`)
  },

  // Orders
  getOrders(params) {
    return api.get('/admin/orders', { params })
  },
  getOrder(id) {
    return api.get(`/admin/orders/${id}`)
  },
  updateOrderStatus(id, status) {
    return api.put(`/admin/orders/${id}/status`, { status })
  },

  // Customers
  getCustomers(params) {
    return api.get('/admin/customers', { params })
  },
  updateCustomerGroup(id, groupId) {
    return api.put(`/admin/customers/${id}/group`, { groupId })
  },

  // Articles
  getArticles(params) {
    return api.get('/admin/cms/articles', { params })
  },
  createArticle(data) {
    return api.post('/admin/cms/articles', data)
  },
  updateArticle(id, data) {
    return api.put(`/admin/cms/articles/${id}`, data)
  }
}