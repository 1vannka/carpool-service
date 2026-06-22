<template>
  <div class="relative w-screen h-screen">

    <div v-if="!isSelectingLocation" class="absolute top-4 left-1/2 -translate-x-1/2 z-10 w-11/12 max-w-md transition-all">
      <div class="relative shadow-lg rounded-full overflow-hidden bg-white/90 backdrop-blur border border-gray-200 flex items-center">
        <input
          type="text"
          v-model="globalSearchQuery"
          placeholder="Поиск адреса на карте..."
          class="w-full py-3 pl-6 pr-12 outline-none bg-transparent text-gray-700 font-medium"
          @keyup.enter="handleGlobalMapSearch"
        />
        <Button
          v-if="!globalSearchMarker"
          icon="pi pi-search"
          text rounded
          class="absolute right-1 text-gray-500 hover:text-purple-600"
          :loading="isGlobalSearching"
          @click="handleGlobalMapSearch"
        />
        <Button
          v-else
          icon="pi pi-times"
          text rounded severity="danger"
          class="absolute right-1"
          @click="clearGlobalSearch"
        />
      </div>
    </div>

    <MapContainer
      ref="mapContainerRef"
      :offices="cityOffices"
      :center="selectedCity"
      :driverRoute="currentDriverRoute"
      :searchMarker="globalSearchMarker"
      :passengerMarker="activeRideRequest ? { lat: activeRideRequest.pickupLocation[1], lng: activeRideRequest.pickupLocation[0] } : null"
      @office-click="openOfficeDialog"
    />

    <CitySelector
      v-if="!isSelectingLocation"
      v-model="selectedCity"
      :cities="cities"
      :isAdmin="authStore.isAdmin"
      @add-office="startAdminCreateMode"
    />

    <div v-if="!isSelectingLocation" class="absolute top-20 md:top-4 right-4 z-10 transition-all">
      <Button
        icon="pi pi-bars"
        severity="secondary"
        rounded
        class="shadow-lg bg-white/90 backdrop-blur border-gray-200 text-gray-700 hover:text-purple-600"
        @click="isProfileSidebarVisible = true"
      />
    </div>

    <ProfileSidebar
      v-model:visible="isProfileSidebarVisible"
      :activeRideRequest="activeRideRequest"
      :rideRequestAddress="activeRideRequestAddress"
      @cancel-ride="handleCancelRideRequest"
      @logout="logout"
    />

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
      :hasActiveTask="!!activeRideRequest"
      @request-location="startLocationSelection"
      @delete="handleDeleteOffice"
      @update="handleUpdateOffice"
      @submit-passenger="submitPassengerRequest"
      @submit-driver="submitDriverRequest"
      @route-preview="(route) => currentDriverRoute = route"
      @set-map-marker="(loc) => globalSearchMarker = loc"
    />

    <Dialog v-model:visible="isAdminSelectMethodVisible" modal header="Как добавить офис?" :style="{ width: '90vw', maxWidth: '400px' }">
      <div class="flex flex-col gap-4">
        <div class="flex flex-col gap-2">
          <label class="text-sm font-semibold text-gray-700">Ввести адрес вручную</label>
          <input type="text" v-model="searchAddressQuery" :placeholder="`Например: ${selectedCity?.name || 'Москва'}, Тверская 1`" class="p-2 border rounded outline-none focus:border-purple-500" @keyup.enter="handleGeocodeSearch" />
          <Button label="Найти и продолжить" severity="help" icon="pi pi-search" :loading="isGeocoding" @click="handleGeocodeSearch" :disabled="!searchAddressQuery.trim()" />
        </div>
        <div class="relative flex items-center py-2">
          <div class="flex-grow border-t border-gray-200"></div>
          <span class="flex-shrink-0 mx-4 text-gray-400 text-xs font-semibold uppercase">или</span>
          <div class="flex-grow border-t border-gray-200"></div>
        </div>
        <div class="flex flex-col gap-2">
          <label class="text-sm font-semibold text-gray-700">Выбрать визуально</label>
          <Button label="Указать точку на карте" icon="pi pi-map-marker" outlined severity="help" @click="startLocationSelectionFromDialog" />
        </div>
      </div>
    </Dialog>

    <Dialog v-model:visible="isAdminCreateDialogVisible" modal header="Новый офис" :style="{ width: '90vw', maxWidth: '400px' }">
      <div class="flex flex-col gap-3">
        <input type="text" v-model="adminCreateForm.name" placeholder="Название (например: Главный офис)" class="p-2 border rounded outline-none focus:border-purple-500" />
        <input type="text" v-model="adminCreateForm.city" placeholder="Город" class="p-2 border rounded outline-none focus:border-purple-500" />
        <input type="text" v-model="adminCreateForm.address" placeholder="Адрес (Определяется...)" class="p-2 border rounded outline-none focus:border-purple-500" />
        <Button label="Сохранить в БД" severity="help" @click="submitNewOffice" :disabled="!adminCreateForm.name || !adminCreateForm.city || !adminCreateForm.address || adminCreateForm.location.length !== 2" />
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { officeService } from '../api/officeService';
import { rideRequestService } from '../api/rideRequestService';
import type { OfficeResponse } from '../types/office';
import type { RideRequestResponse } from '../types/ride';
import { useRouting } from '../composables/useRouting';

import MapContainer from '../components/MapContainer.vue';
import CitySelector from '../components/CitySelector.vue';
import LocationCrosshair from '../components/LocationPicker.vue';
import OfficeDialog from '../components/OfficeActionDialog.vue';
import ProfileSidebar from '../components/ProfileSidebar.vue';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';

const authStore = useAuthStore();
const { geocodeAddress, reverseGeocode } = useRouting();

const mapContainerRef = ref<InstanceType<typeof MapContainer> | null>(null);
const officeDialogRef = ref<InstanceType<typeof OfficeDialog> | null>(null);

const allOffices = ref<OfficeResponse[]>([]);
const cities = ref<{id: string, name: string, lat: number, lng: number}[]>([]);
const selectedCity = ref<{id: string, name: string, lat: number, lng: number} | null>(null);

const currentDriverRoute = ref<number[][] | null>(null);

const globalSearchQuery = ref('');
const isGlobalSearching = ref(false);
const globalSearchMarker = ref<{ lat: number, lng: number } | null>(null);

const cityOffices = computed(() => {
  if (!selectedCity.value) return [];
  return allOffices.value.filter(o => o.city === selectedCity.value?.name);
});

watch(selectedCity, () => clearGlobalSearch());



const isSelectingLocation = ref(false);
const selectionTarget = ref<'passenger' | 'driver' | 'admin_create' | 'admin_edit' | null>(null);
const isOfficeDialogVisible = ref(false);
const isAdminCreateDialogVisible = ref(false);
const selectedOffice = ref<OfficeResponse | null>(null);
const isProfileSidebarVisible = ref(false);

const isAdminSelectMethodVisible = ref(false);
const searchAddressQuery = ref('');
const isGeocoding = ref(false);

const activeRideRequest = ref<RideRequestResponse | null>(null);
const activeRideRequestAddress = ref<string>('');

const adminCreateForm = ref({ name: '', city: '', address: '', location: [] as number[] });

const loadOffices = async () => {
  allOffices.value = await officeService.getOffices();
  const uniqueCitiesMap = new Map();
  allOffices.value.forEach(office => {
    if (!uniqueCitiesMap.has(office.city)) {
      uniqueCitiesMap.set(office.city, { id: office.city, name: office.city, lat: office.location[1]!, lng: office.location[0]! });
    }
  });
  cities.value = Array.from(uniqueCitiesMap.values());
  selectedCity.value = cities.value[0] || null;
};

const handleGlobalMapSearch = async () => {
  if (!globalSearchQuery.value.trim()) return;
  isGlobalSearching.value = true;
  try {
    let query = globalSearchQuery.value;
    if (selectedCity.value && !query.toLowerCase().includes(selectedCity.value.name.toLowerCase())) {
      query = `${selectedCity.value.name}, ${query}`;
    }
    const result = await geocodeAddress(query, selectedCity.value?.lng, selectedCity.value?.lat);
    globalSearchMarker.value = { lat: result.location[1]!, lng: result.location[0]! };
    globalSearchQuery.value = result.address;
  } catch (e) {
    alert("Адрес не найден на карте.");
  } finally {
    isGlobalSearching.value = false;
  }
};

const clearGlobalSearch = () => {
  globalSearchQuery.value = '';
  globalSearchMarker.value = null;
};

const openOfficeDialog = (office: OfficeResponse) => {
  selectedOffice.value = office;
  isOfficeDialogVisible.value = true;
};

const startAdminCreateMode = () => {
  searchAddressQuery.value = '';
  isAdminSelectMethodVisible.value = true;
};

const startLocationSelectionFromDialog = () => {
  isAdminSelectMethodVisible.value = false;
  selectionTarget.value = 'admin_create';
  isSelectingLocation.value = true;
};

const handleGeocodeSearch = async () => {
  if (!searchAddressQuery.value.trim()) return;
  isGeocoding.value = true;
  try {
    let query = searchAddressQuery.value;
    if (selectedCity.value && !query.toLowerCase().includes(selectedCity.value.name.toLowerCase())) {
      query = `${selectedCity.value.name}, ${query}`;
    }
    const result = await geocodeAddress(query, selectedCity.value?.lng, selectedCity.value?.lat);
    adminCreateForm.value.location = result.location;
    adminCreateForm.value.address = result.address;
    adminCreateForm.value.city = selectedCity.value?.name || '';
    adminCreateForm.value.name = '';

    globalSearchMarker.value = { lat: result.location[1]!, lng: result.location[0]! };

    isAdminSelectMethodVisible.value = false;
    isAdminCreateDialogVisible.value = true;
  } catch (e) {
    alert("Адрес не найден. Попробуйте уточнить запрос или выберите точку на карте.");
  } finally {
    isGeocoding.value = false;
  }
};

watch(isAdminCreateDialogVisible, (newVal) => {
  if (!newVal) {
    globalSearchMarker.value = null;
  }
});

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

const confirmSelection = async () => {
  const center = mapContainerRef.value?.getCenter();
  if (!center) return;
  const postGisLocation = [center.lng, center.lat];

  if (selectionTarget.value === 'admin_create') {
    adminCreateForm.value.location = postGisLocation;
    adminCreateForm.value.city = selectedCity.value?.name || '';
    adminCreateForm.value.name = '';
    adminCreateForm.value.address = 'Загрузка адреса...';
    isAdminCreateDialogVisible.value = true;
    isSelectingLocation.value = false;
    selectionTarget.value = null;
    const address = await reverseGeocode(center.lng, center.lat);
    adminCreateForm.value.address = address;
  } else {
    const address = await reverseGeocode(center.lng, center.lat);

    officeDialogRef.value?.updateLocation(selectionTarget.value!, center.lng, center.lat, address);
    isOfficeDialogVisible.value = true;
    isSelectingLocation.value = false;
    selectionTarget.value = null;
  }
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

const loadActiveRideRequest = async () => {
  activeRideRequest.value = await rideRequestService.getMyActiveRequest();
  if (activeRideRequest.value) {
    activeRideRequestAddress.value = await reverseGeocode(
      activeRideRequest.value.pickupLocation[0]!,
      activeRideRequest.value.pickupLocation[1]!
    );
  }
};

const handleCancelRideRequest = async (id: number) => {
  if (!confirm('Точно отменить заявку?')) return;
  try {
    await rideRequestService.cancelRideRequest(id);
    await loadActiveRideRequest();
    alert('Заявка успешно отменена');
  } catch (e) {
    alert('Ошибка при отмене заявки');
  }
};

const submitPassengerRequest = async (form: any) => {
  if (activeRideRequest.value) {
    alert("У вас уже есть активная заявка! Отмените её в профиле.");
    return;
  }
  try {
    const targetTimeDate = new Date(form.targetTime);

    await rideRequestService.createRideRequest({
      officeId: form.officeId!,
      pickupLocation: form.pickupLocation,
      targetTime: targetTimeDate,
      toleranceTime: form.toleranceTime
    });

    isOfficeDialogVisible.value = false;
    await loadActiveRideRequest();
    isProfileSidebarVisible.value = true;
  } catch (e: any) {
    alert(e.response?.data?.error || "Ошибка при создании заявки");
  }
};

const submitDriverRequest = (data: any) => console.log("Водитель отправлен:", data);

onMounted(async () => {
  await loadOffices();
  await loadActiveRideRequest();
});
const logout = () => authStore.logout();
</script>

<style scoped>
:global(body) { margin: 0; padding: 0; overflow: hidden; }
</style>
