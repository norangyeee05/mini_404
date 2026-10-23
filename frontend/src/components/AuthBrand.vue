<script setup>
import logo from '../assets/transport-404-logo.png'
</script>

<template>
  <h1 class="auth-brand">
    <img :src="logo" alt="404" />
    <span>인천교통공사</span>
  </h1>
</template>

<style scoped>
.auth-brand { display: flex; align-items: center; justify-content: center; gap: 7px; margin: 0 0 20px; color: #155369; font-size: 36px; font-weight: 800; line-height: 40px; white-space: nowrap; letter-spacing: -1px; }
.auth-brand img { display: block; width: 72px; height: 40px; object-fit: contain; }
@media (max-width: 420px) { .auth-brand { font-size: 29px; } .auth-brand img { width: 62px; } }
</style>
