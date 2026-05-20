export const useWishlist = () => {
  const api = useApi()
  const token = useCookie('token')

  // 检查是否已收藏
  async function check(productId: number): Promise<boolean> {
    if (!token.value) return false
    const { data } = await api.get<boolean>(`/wishlist/check/${productId}`)
    return data.value?.data || false
  }

  // 添加收藏
  async function add(productId: number, note?: string) {
    return api.post('/wishlist', { productId, note })
  }

  // 取消收藏
  async function remove(productId: number) {
    return api.del(`/wishlist/${productId}`)
  }

  // 获取收藏列表
  async function list() {
    const { data } = await api.get<any[]>('/wishlist')
    return data.value?.data || []
  }

  // 切换收藏状态
  async function toggle(productId: number, note?: string): Promise<boolean> {
    const isWishlisted = await check(productId)
    if (isWishlisted) {
      await remove(productId)
      return false
    } else {
      await add(productId, note)
      return true
    }
  }

  return { check, add, remove, list, toggle }
}