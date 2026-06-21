<template>
  <div class="absolute top-4 left-4 z-10 flex flex-col gap-2 w-72">
    <Select
      :modelValue="modelValue"
      @update:modelValue="$emit('update:modelValue', $event)"
      :options="cities"
      optionLabel="name"
      placeholder="Выберите город"
      class="w-full shadow-lg bg-white/90"
    />

    <Button
      v-if="isAdmin"
      label="Добавить офис"
      icon="pi pi-plus"
      severity="help"
      class="w-full shadow-lg"
      @click="$emit('add-office')"
    />
  </div>
</template>

<script setup lang="ts">
import Select from 'primevue/select';
import Button from 'primevue/button';

defineProps<{
  modelValue: any;
  cities: any[];
  isAdmin: boolean
}>();

defineEmits(['update:modelValue', 'add-office']);
</script>
