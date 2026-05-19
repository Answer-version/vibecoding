// 产品图片辅助函数
export const useProductImage = () => {
  // 生成占位图URL (使用 placeholder.com 或本地)
  const getPlaceholderImage = (productId: number, productName: string): string => {
    // 使用 picsum.photos 生成随机但稳定的图片
    const seed = productId * 100
    return `https://picsum.photos/seed/${seed}/400/400`
  }

  // 获取产品图片
  const getProductImage = (product: any): string => {
    // 优先使用API返回的图片
    if (product.image) {
      return product.image
    }
    if (product.images && product.images.length > 0) {
      return product.images[0]
    }
    // 使用占位图
    return getPlaceholderImage(product.id, product.name)
  }

  return {
    getPlaceholderImage,
    getProductImage
  }
}