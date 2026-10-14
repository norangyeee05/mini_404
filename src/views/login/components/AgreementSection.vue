<script setup>
defineProps({ id: { type: String, required: true }, title: { type: String, required: true }, modelValue: Boolean })
defineEmits(['update:modelValue'])
</script>
<template>
  <section class="agreement-section">
    <label class="agreement-label" :for="id">
      <input :id="id" type="checkbox" :checked="modelValue" @change="$emit('update:modelValue', $event.target.checked)" />
      <span>{{ title }}(필수)</span>
    </label>
    <div class="agreement-text" tabindex="0" :aria-label="`${title} 내용`"><slot /></div>
  </section>
</template>
<style scoped>
.agreement-label { display: flex; align-items: center; gap: 7px; margin-bottom: 10px; color: #858585; font-size: 20px; font-weight: 600; line-height: 28px; cursor: pointer; }
input { appearance: none; flex: 0 0 14px; width: 14px; height: 14px; margin: 0; border: 2px solid currentColor; border-radius: 0; color: #858585; cursor: pointer; }
input:checked { background: #155369; border-color: #155369; box-shadow: inset 0 0 0 2px #fff; }
input:focus-visible, .agreement-text:focus-visible { outline: 2px solid #155369; outline-offset: 4px; }
.agreement-text { height: 166px; overflow-y: scroll; padding: 22px 15px; border: 1px solid #ddd; border-radius: 8px; background: #fff; color: #858585; font-size: 20px; font-weight: 500; line-height: 30px; scrollbar-color: #d9d9d9 #fff; scrollbar-width: auto; }
.agreement-text::-webkit-scrollbar { width: 28px; }
.agreement-text::-webkit-scrollbar-track { background: #fff; margin: 18px 0; }
.agreement-text::-webkit-scrollbar-thumb { background: #d9d9d9; border: 8px solid #fff; }
.agreement-text :deep(p) { margin: 0 0 18px; }
@media (max-width: 600px) { .agreement-label { font-size: 17px; } .agreement-text { font-size: 16px; line-height: 26px; } }
</style>

