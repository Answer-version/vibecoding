export default defineNuxtConfig({
  devtools: { enabled: true },

  modules: ['@pinia/nuxt', '@vueuse/nuxt'],

  app: {
    head: {
      title: 'VibeCommerce',
      charset: 'utf-8',
      viewport: 'width=device-width, initial-scale=1',
      meta: [
        { name: 'description', content: 'VibeCommerce - 外贸独立站' },
        { name: 'keyword', content: 'ecommerce, 外贸, 跨境电商' }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    }
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080/api/v1'
    }
  },

  routeRules: {
    '/': { prerender: true },
    '/products/**': { swr: 3600 }
  },

  nitro: {
    compressPublicAssets: true
  },

  experimental: {
    payloadExtraction: true
  },

  compatibilityDate: '2024-05-18'
})