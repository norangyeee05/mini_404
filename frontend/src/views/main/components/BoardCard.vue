<script setup>
const props = defineProps({ title: String, rows: Array, to: String, moreLabel: { type: String, default: '전체보기' }, compact: Boolean, message: String, interactive: Boolean })
const emit = defineEmits(['activate'])
function activate() { if (props.interactive) emit('activate') }
function onKeydown(event) { if (props.interactive && (event.key === 'Enter' || event.key === ' ')) { event.preventDefault(); activate() } }
</script>
<template>
  <section class="board-card" :class="{ 'board-card--compact': compact, 'board-card--message': Boolean(message), 'board-card--interactive': interactive }" :role="interactive ? 'button' : undefined" :tabindex="interactive ? 0 : undefined" @click="activate" @keydown="onKeydown">
    <div class="board-heading"><h2>{{ title }}</h2><RouterLink v-if="!message" :to="to">{{ moreLabel }} <span aria-hidden="true">›</span></RouterLink></div>
    <p v-if="message" class="board-message">{{ message }}</p><div v-else class="board-scroll"><table><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.id }}</td><td><RouterLink :to="row.link || `${to}/${row.id}`">{{ row.title }}</RouterLink></td><td>{{ row.author }}</td><td>{{ row.views }}</td><td>{{ row.date }}</td></tr></tbody></table></div>
  </section>
</template>
<style scoped src="../styles/board-card.css"></style>
