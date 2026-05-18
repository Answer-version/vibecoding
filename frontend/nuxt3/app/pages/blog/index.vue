<template>
  <div class="blog-page container">
    <h1>Blog</h1>
    <div class="articles">
      <article v-for="article in articles" :key="article.id" class="article-card">
        <NuxtLink :to="`/blog/${article.slug}`">
          <div class="cover-image">
            <img :src="article.coverImage || '/placeholder.jpg'" :alt="article.articleTitle">
          </div>
          <div class="article-content">
            <h2>{{ article.articleTitle }}</h2>
            <p class="summary">{{ article.summary }}</p>
            <div class="meta">
              <span class="date">{{ formatDate(article.publishTime) }}</span>
              <span class="views">{{ article.viewCount }} views</span>
            </div>
          </div>
        </NuxtLink>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
const config = useRuntimeConfig()
const { data: articlesData } = await useFetch('/cms/articles', {
  baseURL: config.public.apiBase,
  query: { page: 1, pageSize: 12 }
})

const articles = computed(() => articlesData.value?.data?.records || [])

function formatDate(date: string) {
  return new Date(date).toLocaleDateString()
}
</script>

<style scoped>
.blog-page {
  padding: 40px 20px;
}
.articles {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
}
.article-card a {
  text-decoration: none;
  color: inherit;
}
.cover-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}
.article-content {
  padding: 15px 0;
}
.article-content h2 {
  font-size: 18px;
  margin-bottom: 10px;
}
.summary {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}
.meta {
  color: #999;
  font-size: 12px;
  display: flex;
  gap: 15px;
}
</style>