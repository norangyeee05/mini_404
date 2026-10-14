<script setup>
defineProps({ title: String, rows: Array, to: String, moreLabel: { type: String, default: '전체보기' }, compact: Boolean })
</script>
<template>
  <section class="board-card" :class="{ 'board-card--compact': compact }">
    <div class="board-heading"><h2>{{ title }}</h2><RouterLink :to="to">{{ moreLabel }} <span aria-hidden="true">›</span></RouterLink></div>
    <div class="board-scroll"><table><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.id }}</td><td><RouterLink :to="`${to}/${row.id}`">{{ row.title }}</RouterLink></td><td>{{ row.author }}</td><td>{{ row.views }}</td><td>{{ row.date }}</td></tr></tbody></table></div>
  </section>
</template>
<style scoped src="../styles/board-card.css"></style>

