<script setup>
defineProps({ title: String, rows: Array, to: String, moreLabel: { type: String, default: '전체보기' }, compact: Boolean })
</script>
<template>
  <section class="board-card" :class="{ 'board-card--compact': compact }">
    <div class="board-heading"><h2>{{ title }}</h2><RouterLink :to="to">{{ moreLabel }} <span aria-hidden="true">›</span></RouterLink></div>
    <div class="board-scroll"><table><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.id }}</td><td><RouterLink :to="`${to}/${row.id}`">{{ row.title }}</RouterLink></td><td>{{ row.author }}</td><td>{{ row.views }}</td><td>{{ row.date }}</td></tr></tbody></table></div>
  </section>
</template>
<style scoped>
.board-card { min-width: 0; padding: 16px 14px 8px; border-radius: 22px; background: #f0f5f7; }
.board-heading { display: flex; align-items: center; justify-content: space-between; margin: 0 3px 17px; }
h2 { margin: 0; padding-left: 10px; border-left: 3px solid #155369; color: #155369; font-size: 24px; line-height: 30px; }
.board-heading > a { color: #858585; font-size: 12px; }.board-heading span { margin-left: 8px; font-size: 22px; }
.board-scroll { overflow-x: auto; } table { font-size: 16px; color: #858585; } thead { background: #e7eef0; } th { height: 54px; font-weight: 600; } td { height: 52px; border: 1px solid #e5edef; padding: 4px 8px; } th:nth-child(2) { width: 40%; } td:nth-child(2) { text-align: left; } a:hover { text-decoration: underline; }
.board-card--compact { padding: 12px 10px 8px; }.board-card--compact .board-heading { margin-bottom: 9px; }.board-card--compact th { height: 31px; }.board-card--compact td { height: 30px; padding: 2px 6px; border-right: 0; border-left: 0; }
@media (max-width: 600px) { table { font-size: 13px; min-width: 500px; } h2 { font-size: 21px; } }
</style>
