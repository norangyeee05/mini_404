<script setup>
defineProps({ title: { type: String, required: true }, value: { type: [String, Number], required: true }, unit: { type: String, default: '건' }, note: { type: String, default: '' } })
</script>
<template>
  <article class="stat-card">
    <div class="stat-card__main"><h2>{{ title }}</h2><p class="stat-card__value">{{ value }} <span>{{ unit }}</span></p></div>
    <p v-if="note" class="stat-card__note">{{ note }}</p>
    <slot />
  </article>
</template>
