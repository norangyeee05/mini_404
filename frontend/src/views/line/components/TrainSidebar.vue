<script setup>
import { currentMember } from '../../../services/auth'
const fullAdmin = currentMember.value?.role === 'ADMIN'
</script>
<template>
  <aside class="train-sidebar">
    <nav aria-label="철도 관리 메뉴">
      <RouterLink to="/admin/line7/trains" :class="{ selected: ['admin-trains', 'admin-train-detail'].includes($route.name) }">열차 목록</RouterLink>
      <RouterLink v-if="fullAdmin" to="/admin/line7/trains/new" :class="{ selected: ['admin-train-new', 'admin-train-edit'].includes($route.name) }">열차 등록</RouterLink>
      <RouterLink to="/admin/line7/stations" :class="{ selected: ['admin-stations', 'admin-station-detail'].includes($route.name) }">역 목록</RouterLink>
      <RouterLink v-if="fullAdmin" to="/admin/line7/stations/new" :class="{ selected: ['admin-station-new', 'admin-station-edit'].includes($route.name) }">역 등록</RouterLink>
    </nav>
  </aside>
</template>
<style scoped>
.train-sidebar { display: flex; flex-direction: column; gap: 4px; border-radius: 20px; background: #b5cce0; padding: 16px 8px; }
nav { display: flex; flex-direction: column; gap: 4px; }
a, button { display: flex; align-items: center; width: 100%; min-height: 40px; border: 0; padding: 0 16px; border-radius: 10px; background: #f8f9f9; color: #858b8d; font: inherit; font-size: 15px; line-height: 20px; text-align: left; text-decoration: none; }
.selected { background: #fff; color: #4e7e8d; font-weight: 400; box-shadow: none; }
.selected:hover { background: #fff; color: #4e7e8d; }
a:hover, button:hover { background: #fff; color: #4e7e8d; }
a:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 2px; }
.station-notice { padding: 10px; font-size: 14px; line-height: 1.6; }
@media (max-width: 760px) { .train-sidebar { padding: 16px 8px; } nav { grid-template-columns: 1fr; } }
</style>

