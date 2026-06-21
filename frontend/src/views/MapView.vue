<template>
  <div class="relative w-screen h-screen">

    <MapContainer
      ref="mapContainerRef"
      :offices="cityOffices"
      :center="selectedCity"
      @office-click="openOfficeDialog"
    />

    <CitySelector
      v-if="!isSelectingLocation"
      v-model="selectedCity"
      :cities="cities"
      :isAdmin="authStore.isAdmin"
      @add-office="startAdminCreateMode"
    />

    <div v-if="!isSelectingLocation" class="absolute top-4 right-4 z-10">
      <Button icon="pi pi-sign-out" severity="danger" rounded class="shadow-lg" @click="logout" />
    </div>

    <LocationCrosshair
      :isVisible="isSelectingLocation"
      :targetMode="selectionTarget"
      @cancel="cancelSelection"
      @confirm="confirmSelection"
    />

    <OfficeDialog
      ref="officeDialogRef"
      v-model:visible="isOfficeDialogVisible"
      :office="selectedOffice"
      :isAdmin="authStore.isAdmin"
      @request-location="startLocationSelection"
      @delete="handleDeleteOffice"
      @update="handleUpdateOffice"
      @submit-passenger="submitPassengerRequest"
      @submit-driver="submitDriverRequest"
    />

    <Dialog v-model:visible="isAdminCreateDialogVisible" modal header="Новый офис" :style="{ width: '90vw', maxWidth: '400px' }">
      <div class="flex flex-col gap-3">
        <input type="text" v-model="adminCreateForm.name" placeholder="Название" class="p-2 border rounded" />
        <input type="text" v-model="adminCreateForm.city" placeholder="Город" class="p-2 border rounded" />
        <input type="text" v-model="adminCreateForm.address" placeholder="Адрес" class="p-2 border rounded" />
        <Button label="Сохранить в БД" severity="help" @click="submitNewOffice" />
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { officeService } from '../api/officeService';
import type { OfficeResponse } from '../types/office';

import MapContainer from '../components/MapContainer.vue';
import CitySelector from '../components/CitySelector.vue';
import LocationCrosshair from '../components/LocationPicker.vue';
import OfficeDialog from '../components/OfficeActionDialog.vue';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';

const authStore = useAuthStore();

const mapContainerRef = ref<InstanceType<typeof MapContainer> | null>(null);
const officeDialogRef = ref<InstanceType<typeof OfficeDialog> | null>(null);

const allOffices = ref<OfficeResponse[]>([]);
const cities = ref<{id: string, name: string, lat: number, lng: number}[]>([]);
const selectedCity = ref<{id: string, name: string, lat: number, lng: number} | null>(null);

const cityOffices = computed(() => {
  if (!selectedCity.value) return [];
  return allOffices.value.filter(o => o.city === selectedCity.value?.name);
});

const isSelectingLocation = ref(false);
const selectionTarget = ref<'passenger' | 'driver' | 'admin_create' | 'admin_edit' | null>(null);
const isOfficeDialogVisible = ref(false);
const isAdminCreateDialogVisible = ref(false);
const selectedOffice = ref<OfficeResponse | null>(null);

const adminCreateForm = ref({ name: '', city: '', address: '', location: [] as number[] });

const loadOffices = async () => {
  allOffices.value = await officeService.getOffices();
  const uniqueCitiesMap = new Map();

  allOffices.value.forEach(office => {
    if (!uniqueCitiesMap.has(office.city)) {
      uniqueCitiesMap.set(office.city, {
        id: office.city, name: office.city,
        lat: office.location[1]!, lng: office.location[0]!
      });
    }
  });

  cities.value = Array.from(uniqueCitiesMap.values());
  selectedCity.value = cities.value[0] || null;
};

const openOfficeDialog = (office: OfficeResponse) => {
  selectedOffice.value = office;
  isOfficeDialogVisible.value = true;
};

const startAdminCreateMode = () => {
  selectionTarget.value = 'admin_create';
  isSelectingLocation.value = true;
};

const startLocationSelection = (target: any) => {
  isOfficeDialogVisible.value = false;
  selectionTarget.value = target;
  isSelectingLocation.value = true;
};

const cancelSelection = () => {
  isSelectingLocation.value = false;
  if (selectionTarget.value !== 'admin_create') isOfficeDialogVisible.value = true;
  selectionTarget.value = null;
};

const confirmSelection = () => {
  const center = mapContainerRef.value?.getCenter();
  if (!center) return;

  const postGisLocation = [center.lng, center.lat];

  if (selectionTarget.value === 'admin_create') {
    adminCreateForm.value.location = postGisLocation;
    adminCreateForm.value.city = selectedCity.value?.name || '';
    isAdminCreateDialogVisible.value = true;
  } else {
    officeDialogRef.value?.updateLocation(selectionTarget.value!, center.lng, center.lat);
    isOfficeDialogVisible.value = true;
  }

  isSelectingLocation.value = false;
  selectionTarget.value = null;
};

const submitNewOffice = async () => {
  await officeService.createOffice(adminCreateForm.value);
  isAdminCreateDialogVisible.value = false;
  await loadOffices();
};

const handleDeleteOffice = async (id: number) => {
  if (confirm('Точно удалить этот офис?')) {
    await officeService.deleteOffice(id);
    isOfficeDialogVisible.value = false;
    await loadOffices();
  }
};

const handleUpdateOffice = async (id: number, data: any) => {
  await officeService.updateOffice(id, data);
  isOfficeDialogVisible.value = false;
  await loadOffices();
};

const submitPassengerRequest = (data: any) => console.log("Пассажир отправлен:", data);
const submitDriverRequest = (data: any) => console.log("Водитель отправлен:", data);

onMounted(() => loadOffices());
const logout = () => authStore.logout();
</script>

<style scoped>
:global(body) { margin: 0; padding: 0; overflow: hidden; }
</style>
