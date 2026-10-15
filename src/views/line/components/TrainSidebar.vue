<template>
  <aside class="train-sidebar">
    <nav aria-label="철도 관리 메뉴">
      <RouterLink to="/admin/line7/trains" :class="{ selected: $route.name === 'admin-trains' }">열차 목록</RouterLink>
      <RouterLink to="/admin/line7/trains/new">열차 등록</RouterLink>
      <RouterLink to="/admin/line7/stations">역 목록</RouterLink>
      <RouterLink to="/admin/line7/stations/new">역 등록</RouterLink>
    </nav>
    
  </aside>
</template>
<style scoped>
.train-sidebar { border-radius: 20px; background: #b5cce0; padding: 26px 8px; }
nav { display: grid; gap: 5px; } a, button { display: block; width: 100%; border: 0; padding: 10px 16px; border-radius: 9px; background: white; color: #858585; font-size: 16px; line-height: 20px; text-align: left; }
.selected { background: #f7f7f7; } a:hover, button:hover { color: #155369; } a:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 2px; }
.station-notice { padding: 10px; font-size: 14px; line-height: 1.6; }
@media (max-width: 760px) { .train-sidebar { padding: 12px 8px; } nav { grid-template-columns: repeat(2, 1fr); } }
</style>

