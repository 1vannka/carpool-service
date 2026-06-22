<template>
  <Drawer
    :visible="visible"
    @update:visible="$emit('update:visible', $event)"
    position="right"
    class="!w-full md:!w-[350px] bg-white shadow-2xl"
  >
    <template #header>
      <div class="flex items-center gap-2">
        <div class="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center text-purple-600 font-bold">
          {{ profile?.firstName?.charAt(0) || 'U' }}
        </div>
        <span class="font-bold text-lg">Мой профиль</span>
      </div>
    </template>

    <div v-if="isLoading" class="flex justify-center p-8">
      <i class="pi pi-spin pi-spinner text-3xl text-purple-500"></i>
    </div>

    <div v-else-if="profile" class="flex flex-col h-full">
      <div class="flex flex-col gap-4 flex-grow overflow-y-auto pr-2 mt-2">

        <div class="flex flex-col gap-1">
          <label class="text-xs text-gray-500 font-semibold uppercase">Email</label>
          <div class="p-2 bg-gray-50 rounded text-gray-700 font-medium">{{ profile.email }}</div>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs text-gray-500 font-semibold uppercase">Имя</label>
          <div class="p-2 bg-gray-50 rounded text-gray-700 font-medium">{{ profile.firstName }}</div>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs text-gray-500 font-semibold uppercase">Фамилия</label>
          <div class="p-2 bg-gray-50 rounded text-gray-700 font-medium">{{ profile.lastName }}</div>
        </div>

        <Divider align="center"><span class="text-xs text-gray-400">Социальные сети</span></Divider>

        <div class="flex flex-col gap-1">
          <label class="text-xs text-gray-500 font-semibold uppercase">Telegram</label>
          <input
            v-if="isEditing"
            type="text"
            v-model="editForm.telegramAlias"
            placeholder="@username"
            class="p-2 border rounded focus:border-purple-500 outline-none transition-all"
          />
          <div v-else class="p-2 border border-transparent text-gray-700">
            {{ profile.telegramAlias || 'Не указан' }}
          </div>
        </div>

        <div class="flex flex-col gap-1">
          <label class="text-xs text-gray-500 font-semibold uppercase">ВКонтакте</label>
          <input
            v-if="isEditing"
            type="text"
            v-model="editForm.vkAlias"
            placeholder="id123456"
            class="p-2 border rounded focus:border-purple-500 outline-none transition-all"
          />
          <div v-else class="p-2 border border-transparent text-gray-700">
            {{ profile.vkAlias || 'Не указан' }}
          </div>
        </div>

        <div class="mt-2">
          <div v-if="isEditing" class="flex gap-2">
            <Button label="Сохранить" severity="success" class="flex-1" @click="saveProfile" :loading="isSaving" />
            <Button label="Отмена" severity="secondary" outlined class="flex-1" @click="cancelEdit" />
          </div>
          <Button v-else label="Редактировать" icon="pi pi-pencil" outlined class="w-full" @click="startEdit" />
        </div>
      </div>

      <div v-if="activeRideRequest" class="bg-amber-50 border border-amber-200 p-4 rounded-xl flex flex-col gap-2 mb-4 shrink-0 shadow-sm">
        <div class="flex items-center gap-2 text-amber-700 font-bold">
          <i class="pi pi-directions"></i> Ищу машину
        </div>
        <div class="text-sm text-gray-700 font-medium"> {{ rideRequestAddress || 'Загрузка адреса...' }}</div>
        <div class="text-sm text-gray-700">
          🕒 {{ new Date(activeRideRequest.targetTime).toLocaleString([], { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit' }) }}
          <span class="text-xs text-gray-500">(±{{ activeRideRequest.toleranceTime }} мин)</span>
        </div>
        <Button label="Отменить заявку" severity="danger" outlined size="small" class="mt-2 w-full bg-white" @click="$emit('cancel-ride', activeRideRequest.id)" />
      </div>

      <div class="pt-4 mt-4 border-t border-gray-100">
        <Button label="Выйти из аккаунта" icon="pi pi-sign-out" severity="danger" text class="w-full" @click="$emit('logout')" />
      </div>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import Drawer from 'primevue/drawer';
import Button from 'primevue/button';
import Divider from 'primevue/divider';
import { profileService} from '../api/profileService';
import type {UserProfileResponse} from '../types/user'
import type { RideRequestResponse } from '../types/ride.ts';

const props = defineProps<{
  visible: boolean;
  activeRideRequest: RideRequestResponse | null;
  rideRequestAddress: string;
}>();
const emit = defineEmits(['update:visible', 'logout', 'cancel-ride']);

const profile = ref<UserProfileResponse | null>(null);
const isLoading = ref(false);
const isEditing = ref(false);
const isSaving = ref(false);

const editForm = ref({ telegramAlias: '', vkAlias: '' });

const fetchProfile = async () => {
  isLoading.value = true;
  try {
    profile.value = await profileService.getProfile();
  } catch (e) {
    console.error("Ошибка загрузки профиля", e);
  } finally {
    isLoading.value = false;
  }
};

watch(() => props.visible, (newVal) => {
  if (newVal) {
    isEditing.value = false;
    fetchProfile();
  }
});

const startEdit = () => {
  editForm.value.telegramAlias = profile.value?.telegramAlias || '';
  editForm.value.vkAlias = profile.value?.vkAlias || '';
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
};

const saveProfile = async () => {
  isSaving.value = true;
  try {
    await profileService.updateProfile({
      telegramAlias: editForm.value.telegramAlias?.trim() || null,
      vkAlias: editForm.value.vkAlias?.trim() || null
    });

    await fetchProfile();

    isEditing.value = false;
  } catch (e) {
    alert("Не удалось сохранить профиль");
  } finally {
    isSaving.value = false;
  }
};
</script>
