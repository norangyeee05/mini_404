<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: String, required: true },
  unit: { type: String, default: '' },
  note: { type: String, required: true },
})
</script>

<template>
  <article class="stat-card">
    <div class="stat-card__main">
      <h2>{{ label }}</h2>
      <p class="stat-card__value">{{ value }} <span>{{ unit }}</span></p>
    </div>
    <p class="stat-card__note">{{ note }}</p>
  </article>
</template>
