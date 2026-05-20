// 简单的国际化解决方案
export const useI18n = () => {
  const locale = useCookie('locale', { default: () => 'en' })

  const messages: Record<string, Record<string, string>> = {
    en: {
      home: 'Home',
      products: 'Products',
      about: 'About',
      cart: 'Cart',
      coupons: 'Coupons',
      login: 'Login',
      logout: 'Logout',
      welcome: 'Welcome to VibeCommerce',
      subtitle: 'Your trusted partner for cross-border e-commerce',
      shopNow: 'Shop Now',
      featured: 'Featured Products',
      search: 'Search products...',
      allCategories: 'All Categories',
      addToCart: 'Add to Cart',
      price: 'Price',
      details: 'Details',
      quantity: 'Quantity',
      checkout: 'Checkout',
      continueShopping: 'Continue Shopping',
      emptyCart: 'Your cart is empty',
      shippingAddress: 'Shipping Address',
      paymentMethod: 'Payment Method',
      orderSummary: 'Order Summary',
      subtotal: 'Subtotal',
      shipping: 'Shipping',
      tax: 'Tax',
      total: 'Total',
      placeOrder: 'Place Order',
      username: 'Username',
      password: 'Password',
      email: 'Email',
      register: 'Register',
      forgotPassword: 'Forgot Password?',
      noAccount: "Don't have an account?",
      hasAccount: 'Already have an account?'
    },
    zh: {
      home: '首页',
      products: '产品',
      about: '关于',
      cart: '购物车',
      coupons: '优惠券',
      login: '登录',
      logout: '退出',
      welcome: '欢迎来到VibeCommerce',
      subtitle: '您值得信赖的跨境电商伙伴',
      shopNow: '立即购物',
      featured: '精选产品',
      search: '搜索产品...',
      allCategories: '全部分类',
      addToCart: '加入购物车',
      price: '价格',
      details: '详情',
      quantity: '数量',
      checkout: '结账',
      continueShopping: '继续购物',
      emptyCart: '购物车是空的',
      shippingAddress: '收货地址',
      paymentMethod: '支付方式',
      orderSummary: '订单摘要',
      subtotal: '小计',
      shipping: '运费',
      tax: '税费',
      total: '总计',
      placeOrder: '提交订单',
      username: '用户名',
      password: '密码',
      email: '邮箱',
      register: '注册',
      forgotPassword: '忘记密码？',
      noAccount: '没有账号？',
      hasAccount: '已有账号？'
    }
  }

  const t = (key: string): string => {
    const lang = locale.value || 'en'
    return messages[lang]?.[key] || messages.en[key] || key
  }

  const setLocale = (newLocale: string) => {
    locale.value = newLocale
    useCookie('locale').value = newLocale
  }

  return {
    locale,
    t,
    setLocale
  }
}