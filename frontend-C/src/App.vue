<script setup>
import { useRouter } from 'vue-router'
import AccessDeniedModal from './components/AccessDeniedModal.vue'
import { accessDenied } from './services/accessControl'
const router = useRouter()
function login() { accessDenied.value = false; router.push('/login') }
</script>
<template>
  <RouterView />
  <AccessDeniedModal v-if="accessDenied" @confirm="login" @close="accessDenied = false" />
</template>