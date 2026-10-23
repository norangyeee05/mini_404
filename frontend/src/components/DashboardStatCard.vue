<script setup>
import { RouterLink } from 'vue-router'
defineProps({
  label: { type: String, required: true },
  value: { type: String, required: true },
  unit: { type: String, default: '' },
  note: { type: String, required: true },
  to: { type: String, default: '' },
})
</script>

<template>
  <component :is="to ? RouterLink : 'article'" :to="to || undefined" :class="['stat-card', { 'stat-card--link': to }]">
    <div class="stat-card__main">
      <h2>{{ label }}</h2>
      <p class="stat-card__value">{{ value }} <span>{{ unit }}</span></p>
    </div>
    <p class="stat-card__note">{{ note }}</p>
  </component>
</template>

<style scoped>
.stat-card--link { color: inherit; text-decoration: none; }
.stat-card--link:focus-visible { outline: 3px solid #467486; outline-offset: 3px; }
</style>
