<script setup>
defineProps({ modelValue: { type: Array, required: true }, options: { type: Array, required: true }, label: { type: String, required: true } })
const emit = defineEmits(['update:modelValue'])
function add(event, values) {
  if (event.target.value && !values.includes(event.target.value)) emit('update:modelValue', [...values, event.target.value])
  event.target.value = ''
}
</script>
<template>
  <div class="tag-picker">
    <div class="tag-list" :aria-label="label + ' 선택된 항목'">
      <span v-for="(tag, index) in modelValue" :key="tag" class="tag">{{ tag }} <button type="button" :aria-label="tag + ' 삭제'" @click="emit('update:modelValue', modelValue.filter((_, i) => i !== index))">X</button></span>
    </div>
    <select :aria-label="label + ' 추가'" @change="add($event, modelValue)"><option value="">선택</option><option v-for="option in options" :key="option" :value="option" :disabled="modelValue.includes(option)">{{ option }}{{ modelValue.includes(option) ? ' (선택됨)' : '' }}</option></select>
  </div>
</template>
<style scoped>
.tag-list { display: flex; flex-wrap: wrap; gap: 8px 16px; min-height: 50px; padding: 5px 14px; border: 1px solid #ddd; border-radius: 8px; align-items: center; }
.tag { padding: 5px 10px; border-radius: 24px; background: #e0dada; color: black; font-size: 18px; line-height: 28px; font-weight: 600; }
button { padding: 0; border: 0; background: none; color: inherit; font: inherit; }
select { width: 195px; max-width: 100%; min-height: 50px; margin-top: 14px; padding: 0 14px; border: 3px solid #c6dadd; border-radius: 10px; background: white; color: #467486; font-size: 16px; font-weight: 700; }
button:focus-visible, select:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
</style>


