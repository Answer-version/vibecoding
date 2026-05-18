export const useCart = () => {
  const api = useApi()
  const cart = useState<any>('cart', () => null)

  async function loadCart() {
    const { data } = await api.get<any>('/cart')
    cart.value = data.value?.data
    return data.value?.data
  }

  async function addItem(productId: number, skuId: number, quantity: number) {
    await api.post('/cart/items', { productId, skuId, quantity })
    return loadCart()
  }

  async function updateItem(itemId: number, quantity: number) {
    await api.put(`/cart/items/${itemId}`, { quantity })
    return loadCart()
  }

  async function removeItem(itemId: number) {
    await api.del(`/cart/items/${itemId}`)
    return loadCart()
  }

  async function clearCart() {
    await api.del('/cart/clear')
    cart.value = null
  }

  return { cart, loadCart, addItem, updateItem, removeItem, clearCart }
}