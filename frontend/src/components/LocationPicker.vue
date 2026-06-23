<template>
  <div v-if="isVisible" class="absolute inset-0 z-20 pointer-events-none">

    <div class="absolute inset-0 flex items-center justify-center">
      <div class="relative flex items-center justify-center">
        <div class="absolute w-12 h-12 rounded-full opacity-30 animate-ping"
             :class="isParamAdmin ? 'bg-purple-500' : 'bg-blue-500'"></div>
        <i class="pi pi-map-marker text-4xl drop-shadow-md z-10 -mt-10"
           :class="isParamAdmin ? 'text-purple-600' : 'text-blue-600'"></i>
      </div>
    </div>

    <div class="absolute bottom-10 left-1/2 -translate-x-1/2 flex gap-4 bg-white/80 p-2 rounded-full shadow-lg backdrop-blur-sm pointer-events-auto">
      <Button label="Отмена" severity="secondary" rounded @click="$emit('cancel')" />
      <Button label="Подтвердить координаты" severity="success" rounded @click="$emit('confirm')" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button';
import { computed } from 'vue';

const props = defineProps<{ isVisible: boolean; targetMode: string | null }>();
defineEmits(['confirm', 'cancel']);

const isParamAdmin = computed(() => props.targetMode === 'admin_office' || props.targetMode === 'admin_edit');
</script>
