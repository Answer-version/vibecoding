export const useReview = () => {
  const api = useApi()

  // 获取评价列表
  async function list(productId: number, page = 1, pageSize = 10) {
    const { data } = await api.get<any[]>('/reviews', { productId, page, pageSize })
    return data.value?.data || []
  }

  // 获取评价统计
  async function stats(productId: number) {
    const { data } = await api.get<any>('/reviews/stats', { productId })
    return data.value?.data || {}
  }

  // 添加评价
  async function add(productId: number, rating: number, content: string, options?: {
    orderId?: number
    title?: string
    images?: string
    isAnonymous?: boolean
  }) {
    return api.post('/reviews', { productId, rating, content, ...options })
  }

  // 删除评价
  async function remove(reviewId: number) {
    return api.del(`/reviews/${reviewId}`)
  }

  return { list, stats, add, remove }
}