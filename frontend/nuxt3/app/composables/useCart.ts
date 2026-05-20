export const useCart = () => {
  const api = useApi()
  const token = useCookie('token')
  const guestId = useCookie('guestId', { maxAge: 60 * 60 * 24 * 30 }) // 30 days

  // Generate guest ID if not exists
  if (!guestId.value) {
    guestId.value = 'guest_' + Math.random().toString(36).substring(2, 15)
  }

  const cart = useState<any>('cart', () => null)

  async function loadCart() {
    const params = token.value ? {} : { guestId: guestId.value }
    const { data } = await api.get<any>('/cart', params)
    cart.value = data.value?.data
    return data.value?.data
  }

  async function addItem(productId: number, skuId: number | null, quantity: number, usdPrice: number, productName: string) {
    const params = {
      productId,
      skuId,
      quantity,
      usdPrice,
      productName
    }
    if (!token.value) {
      params['guestId'] = guestId.value
    }
    await api.post('/cart/items', params)
    return loadCart()
  }

  async function updateItem(itemId: number, quantity: number) {
    const params: any = { quantity }
    if (!token.value) {
      params['guestId'] = guestId.value
    }
    await api.post(`/cart/items/${itemId}`, params)
    return loadCart()
  }

  async function removeItem(itemId: number) {
    const params: any = {}
    if (!token.value) {
      params['guestId'] = guestId.value
    }
    await api.del(`/cart/items/${itemId}`)
    return loadCart()
  }

  async function clearCart() {
    const params: any = {}
    if (!token.value) {
      params['guestId'] = guestId.value
    }
    await api.del('/cart/clear')
    cart.value = null
  }

  return { cart, guestId, loadCart, addItem, updateItem, removeItem, clearCart }
}