import type { Cart, CartItem } from '~/types/cart'

export interface CartResponse {
  code: number
  data: {
    cart: Cart
    items: CartItem[]
    subtotal: number
    itemCount: number
  }
}

// 购物车项
export interface AddCartItemParams {
  productId: number
  skuId?: number
  quantity: number
  usdPrice: number
  productName: string
  skuCode?: string
  skuAttrs?: string
}

export const useCartApi = () => {
  const api = useApi()

  // 获取购物车
  async function getCart() {
    const { data, error } = await api.get<CartResponse>('/cart')
    return { data: data.value?.data, error: error.value }
  }

  // 添加商品到购物车
  async function addItem(params: AddCartItemParams) {
    const { data, error } = await api.post<{ code: number, data: Cart }>('/cart/items', null, {
      params: params
    })
    return { data: data.value?.data, error: error.value }
  }

  // 更新商品数量
  async function updateItem(itemId: number, quantity: number) {
    const { data, error } = await api.put<{ code: number, data: Cart }>(`/cart/items/${itemId}`, null, {
      quantity
    })
    return { data: data.value?.data, error: error.value }
  }

  // 删除商品
  async function removeItem(itemId: number) {
    const { data, error } = await api.del<{ code: number, data: Cart }>(`/cart/items/${itemId}`)
    return { data: data.value?.data, error: error.value }
  }

  // 清空购物车
  async function clearCart() {
    const { data, error } = await api.del<{ code: number }>('/cart/clear')
    return { data: data.value, error: error.value }
  }

  return {
    getCart,
    addItem,
    updateItem,
    removeItem,
    clearCart
  }
}

// 订单相关类型
export interface OrderItem {
  id: number
  productId: number
  skuId?: number
  productName: string
  skuCode?: string
  skuAttrs?: string
  quantity: number
  usdPrice: number
  usdAmount: number
  imageUrl?: string
}

export interface ShippingAddress {
  id?: number
  firstName: string
  lastName: string
  phone: string
  countryCode: string
  countryName?: string
  stateCode?: string
  stateName?: string
  city: string
  district?: string
  address1: string
  address2?: string
  zipCode?: string
  isDefault?: number
}

export interface CreateOrderRequest {
  items: OrderItem[]
  addressId: number
  shippingMethod: string
  paymentMethod: string
  remark?: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  status: number
  payStatus: number
  shipStatus: number
  usdAmount: number
  shippingFee: number
  taxAmount: number
  discountAmount: number
  totalAmount: number
  shipName: string
  shipPhone: string
  shipAddress: string
  shipCountry: string
  shipState: string
  shipCity: string
  shipZip: string
  shippingMethod?: string
  trackingNo?: string
  createTime: string
}

export interface OrderResponse {
  code: number
  data: Order
  message?: string
}

export const useOrderApi = () => {
  const api = useApi()

  // 创建订单
  async function createOrder(request: CreateOrderRequest) {
    const { data, error } = await api.post<OrderResponse>('/orders/checkout', request)
    return { data: data.value?.data, error: error.value }
  }

  // 获取订单列表
  async function getOrders(params?: { page?: number; pageSize?: number; status?: number }) {
    const { data, error } = await api.get<{ code: number, data: { list: Order[], page: any } }>('/orders', params)
    return { data: data.value?.data, error: error.value }
  }

  // 获取订单详情
  async function getOrder(orderId: number) {
    const { data, error } = await api.get<OrderResponse>(`/orders/${orderId}`)
    return { data: data.value?.data, error: error.value }
  }

  // 取消订单
  async function cancelOrder(orderId: number) {
    const { data, error } = await api.post<{ code: number }>(`/orders/${orderId}/cancel`, {})
    return { data: data.value, error: error.value }
  }

  // 创建支付
  async function createPayment(orderId: number, paymentMethod: string) {
    const { data, error } = await api.post<{ code: number, data: { payUrl: string } }>('/payments/create', {
      orderId,
      paymentMethod
    })
    return { data: data.value?.data, error: error.value }
  }

  return {
    createOrder,
    getOrders,
    getOrder,
    cancelOrder,
    createPayment
  }
}