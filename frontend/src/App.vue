<script setup>
import { useRouter } from 'vue-router'
import AccessDeniedModal from './components/AccessDeniedModal.vue'
import { accessDenied } from './services/accessControl'
import { hydrateMember } from './services/auth'
import { onMounted } from 'vue'
const router = useRouter()
onMounted(() => { hydrateMember() })
window.addEventListener('api:unauthorized', () => { accessDenied.value = false; router.push('/login') })
function login() { accessDenied.value = false; router.push('/login') }
</script>
<template>
  <RouterView />
  <AccessDeniedModal v-if="accessDenied" @confirm="login" @close="accessDenied = false" />
</template>
