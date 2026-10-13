<script setup>
import hero from '../assets/notice-hero.png'
defineProps({ title: String, description: String, detail: Boolean, stage: String })
</script>
<template><section class="page-banner"><img :src="hero" alt="인천 도시철도 열차" /><div class="banner-copy"><p>HOME &gt; {{ title }}<span v-if="stage || detail"> &gt; {{ stage || '상세' }}</span></p><h1>{{ title }}</h1><p>{{ description }}</p></div></section></template>
<style scoped>
.page-banner { position: relative; height: 316px; overflow: hidden; border-radius: 20px; }.page-banner img { display: block; width: 100%; height: 100%; object-fit: cover; }.banner-copy { position: absolute; top: 40px; left: 18px; right: 18px; color: #eef3f5; text-shadow: 0 1px 3px #555; }.banner-copy p { margin: 0; font-size: 20px; line-height: 28px; }.banner-copy h1 { margin: 16px 0 2px; font-size: 32px; line-height: 40px; }
@media (max-width: 760px) { .page-banner { height: 240px; }.banner-copy p { font-size: 16px; }.banner-copy h1 { font-size: 28px; } }
</style>
