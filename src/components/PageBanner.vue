<script setup>
import hero from '../assets/notice-hero.png'
defineProps({ title: String, description: String, detail: Boolean, stage: String, breadcrumbTitle: String })
</script>
<template>
  <section class="page-banner" :aria-label="title + ' 안내'">
    <img :src="hero" alt="인천 도시철도 열차" />
    <div class="banner-copy">
      <div class="banner-breadcrumb">HOME <span>›</span> {{ breadcrumbTitle || title }}<template v-if="stage || detail"><span>›</span> {{ stage || '상세' }}</template></div>
      <h1>{{ title }}</h1>
      <p>{{ description }}</p>
    </div>
  </section>
</template>
<style scoped>
.page-banner { position: relative; min-height: 316px; display: flex; flex-direction: column; align-items: flex-start; padding: 42px 20px; border-radius: 18px; overflow: hidden; color: #f5f8f9; font-family: Arial, 'Noto Sans KR', sans-serif; }
.page-banner img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; object-position: center 53%; }
.page-banner::after { position: absolute; inset: 0; content: ''; pointer-events: none; background: linear-gradient(90deg, rgba(44,57,75,.66), rgba(44,57,75,.28) 46%, rgba(44,57,75,.04) 82%); }
.banner-copy { position: relative; z-index: 1; color: inherit; }
.banner-breadcrumb { margin-bottom: 18px; font-size: 20px; font-weight: 500; line-height: 28px; }
.banner-breadcrumb span { padding: 0 5px; }
.banner-copy h1 { margin: 0 0 2px; padding: 0; border: 0; color: inherit; font-size: 34px; font-weight: 700; line-height: 41px; }
.banner-copy p { margin: 0; font-size: 20px; font-weight: 400; line-height: 28px; white-space: pre-line; }
@media (max-width: 1020px) { .page-banner { min-height: 260px; padding: 34px 18px; } }
@media (max-width: 640px) { .page-banner { min-height: 200px; padding: 26px 15px; border-radius: 13px; }.page-banner img { object-position: 60% center; }.banner-breadcrumb { margin-bottom: 13px; font-size: 13px; line-height: 18px; }.banner-copy h1 { font-size: 24px; line-height: 30px; }.banner-copy p { max-width: 270px; font-size: 13px; line-height: 18px; } }
</style>
