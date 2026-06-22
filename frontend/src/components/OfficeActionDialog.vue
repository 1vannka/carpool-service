<template>
  <Drawer
    :visible="visible"
    @update:visible="$emit('update:visible', $event)"
    :header="office?.name || 'Офис'"
    :position="isMobile ? 'bottom' : 'right'"
    :modal="false"
    :dismissable="false"
    :class="isMobile ? 'rounded-t-3xl' : '!w-[320px] shadow-2xl border-l border-gray-100'"
    :style="isMobile ? { height: 'auto', maxHeight: '90dvh' } : {}"
    class="bg-white/95 backdrop-blur-md"
  >
    <div v-if="!activeTab" class="flex flex-col gap-3 py-2">
      <p class="text-sm text-gray-500 mb-4"> {{ office?.address }}</p>

      <template v-if="hasActiveTask">
        <div class="bg-amber-50 p-4 rounded-xl border border-amber-200 text-amber-800 text-sm text-center font-medium shadow-sm flex flex-col items-center gap-2">
          <i class="pi pi-exclamation-triangle text-2xl"></i>
          У вас уже есть активная заявка или поездка.<br>Отмените её в профиле, чтобы создать новую.
        </div>
      </template>

      <template v-else>
        <Button label="Ищу поездку (Пассажир)" icon="pi pi-user" outlined class="w-full" @click="activeTab = 'passenger'" />
        <Button label="Создаю поездку (Водитель)" icon="pi pi-car" severity="success" class="w-full" @click="activeTab = 'driver'" />
      </template>

      <template v-if="isAdmin">
        <Divider />
        <Button label="Редактировать офис" icon="pi pi-pencil" severity="help" class="w-full" @click="openAdminEdit" />
        <Button label="Удалить офис" icon="pi pi-trash" severity="danger" text class="w-full" @click="$emit('delete', office?.id)" />
      </template>
    </div>

    <div v-else-if="activeTab === 'admin_edit'" class="flex flex-col gap-4 pb-4 overflow-y-auto">
      <h3 class="font-bold text-purple-700 mb-1">Редактирование офиса</h3>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Название</label>
        <input type="text" v-model="editForm.name" class="p-2 border rounded outline-none focus:border-purple-500 bg-white" />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Город</label>
        <input type="text" v-model="editForm.city" class="p-2 border rounded outline-none focus:border-purple-500 bg-white" />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Адрес</label>
        <input type="text" v-model="editForm.address" class="p-2 border rounded outline-none focus:border-purple-500 bg-white" />
      </div>
      <div class="flex flex-col gap-1 mt-2">
        <label class="text-sm font-semibold">Координаты</label>
        <Button label="Указать новые на карте" icon="pi pi-map-marker" outlined severity="help" @click="$emit('request-location', 'admin_edit')" />
      </div>
      <div class="flex justify-between mt-4">
        <Button label="Отмена" text @click="activeTab = null" />
        <Button label="Сохранить" severity="help" @click="$emit('update', office?.id, editForm)" :disabled="!isAdminFormValid" />
      </div>
    </div>

    <div v-else-if="activeTab === 'passenger'" class="flex flex-col gap-4 pb-4 overflow-y-auto">
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Откуда вас забрать?</label>

        <div v-if="passengerForm.pickupLocation.length === 0" class="flex gap-2">
          <input type="text" v-model="passengerAddressQuery" placeholder="Улица и дом" class="w-full p-2 border rounded outline-none focus:border-blue-500 bg-white text-sm" @keyup.enter="searchPassengerAddress" />
          <Button icon="pi pi-search" severity="secondary" :loading="isSearchingLocation" @click="searchPassengerAddress" :disabled="!passengerAddressQuery.trim()" />
          <Button icon="pi pi-map-marker" outlined @click="$emit('request-location', 'passenger')" title="Выбрать на карте" />
        </div>
        <div v-else class="flex items-center justify-between bg-green-50 p-2 rounded border border-green-200">
          <span class="text-sm text-green-700 font-medium truncate pr-2"><i class="pi pi-check-circle mr-1"></i> {{ passengerResolvedAddress || 'Точка выбрана' }}</span>
          <Button icon="pi pi-pencil" text rounded size="small" @click="clearPassengerLocation" />
        </div>
      </div>

      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold text-gray-700">Желаемое время выезда</label>
        <input type="datetime-local" v-model="passengerForm.targetTime" :min="nowString" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" required />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold text-gray-700">Отклонение (в минутах)</label>
        <input type="number" v-model="passengerForm.toleranceTime" min="0" max="60" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" required />
      </div>
      <div class="flex justify-between mt-4">
        <Button label="Назад" text severity="secondary" @click="activeTab = null" />
        <Button label="Оформить заявку" @click="$emit('submit-passenger', passengerForm)" :disabled="!isPassengerFormValid" />
      </div>
    </div>

    <div v-else-if="activeTab === 'driver'" class="flex flex-col gap-4 pb-4 overflow-y-auto">
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Точка старта маршрута</label>

        <div v-if="driverForm.routePath.length === 0" class="flex gap-2">
          <input type="text" v-model="driverAddressQuery" placeholder="Улица и дом" class="w-full p-2 border rounded outline-none focus:border-blue-500 bg-white text-sm" @keyup.enter="searchDriverAddress" />
          <Button icon="pi pi-search" severity="secondary" :loading="isSearchingLocation" @click="searchDriverAddress" :disabled="!driverAddressQuery.trim()" />
          <Button icon="pi pi-map-marker" outlined @click="$emit('request-location', 'driver')" title="Выбрать на карте" />
        </div>
        <div v-else class="flex items-center justify-between bg-green-50 p-2 rounded border border-green-200">
          <span class="text-sm text-green-700 font-medium truncate pr-2"><i class="pi pi-check-circle mr-1"></i> {{ driverResolvedAddress || 'Старт выбран' }}</span>
          <Button icon="pi pi-pencil" text rounded size="small" @click="clearDriverLocation" />
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Время выезда</label>
          <input type="datetime-local" v-model="driverForm.departureTime" :min="nowString" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" required />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">В пути (мин)</label>
          <input type="number" v-model="driverForm.estimatedDuration" readonly placeholder="Расчет" class="p-2 border rounded outline-none bg-gray-50 text-gray-500 cursor-not-allowed" />
        </div>
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold text-gray-700">Автомобиль (Марка и Цвет)</label>
        <div class="grid grid-cols-2 gap-3">
          <input type="text" v-model="driverForm.carModel" placeholder="Kia Rio" maxlength="30" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" />
          <input type="text" v-model="driverForm.carColor" placeholder="Белый" maxlength="20" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" />
        </div>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Гос. Номер</label>
          <input type="text" v-model="driverForm.carPlate" placeholder="А777АА77" maxlength="10" class="p-2 border rounded outline-none focus:border-blue-500 bg-white uppercase" />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Свободно мест</label>
          <input type="number" v-model="driverForm.totalSeats" min="1" max="8" class="p-2 border rounded outline-none focus:border-blue-500 bg-white" />
        </div>
      </div>

      <div class="flex justify-between mt-2 pt-4 border-t border-gray-100">
        <Button label="Назад" text severity="secondary" @click="activeTab = null" />
        <Button v-if="!isRouteBuilt" label="Построить" severity="help" icon="pi pi-map" :loading="isLoadingRoute" @click="buildDriverRoute" :disabled="driverForm.routePath.length === 0" />
        <Button v-else label="Создать" severity="success" icon="pi pi-check" @click="submitDriver" :disabled="!isDriverFormValid" />
      </div>
    </div>
  </Drawer>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onUnmounted } from 'vue';
import Button from 'primevue/button';
import Drawer from 'primevue/drawer';
import Divider from 'primevue/divider';
import type { OfficeResponse } from '../types/office';
import type { TripCreateRequest } from '../types/trip';
import { useRouting } from '../composables/useRouting';

const props = defineProps<{ visible: boolean; office: OfficeResponse | null; isAdmin: boolean; hasActiveTask: boolean }>();
const emit = defineEmits(['update:visible', 'request-location', 'delete', 'update', 'submit-passenger', 'submit-driver', 'route-preview', 'set-map-marker']);

const isMobile = ref(window.innerWidth < 768);
const checkMobile = () => isMobile.value = window.innerWidth < 768;
onMounted(() => window.addEventListener('resize', checkMobile));
onUnmounted(() => window.removeEventListener('resize', checkMobile));

const activeTab = ref<string | null>(null);
const { getRoute, geocodeAddress } = useRouting();
const isLoadingRoute = ref(false);
const isRouteBuilt = ref(false);

const editForm = ref({ name: '', city: '', address: '', location: [] as number[] });
const passengerForm = ref({ officeId: null as number | null, pickupLocation: [] as number[], targetTime: '', toleranceTime: 15 });
const driverForm = ref({ officeId: null as number | null, departureTime: '', estimatedDuration: 0, totalSeats: 3, carModel: '', carColor: 'Черный', carPlate: 'А000АА', routePath: [] as number[][] });

const passengerAddressQuery = ref('');
const driverAddressQuery = ref('');
const isSearchingLocation = ref(false);
const passengerResolvedAddress = ref('');
const driverResolvedAddress = ref('');

const nowString = computed(() => {
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  return now.toISOString().slice(0, 16);
});

const isAdminFormValid = computed(() => editForm.value.name.trim().length > 1 && editForm.value.city.trim().length > 1 && editForm.value.address.trim().length > 3 && editForm.value.location.length === 2);
const isPassengerFormValid = computed(() => passengerForm.value.pickupLocation.length === 2 && passengerForm.value.targetTime && new Date(passengerForm.value.targetTime) > new Date() && passengerForm.value.toleranceTime !== null && passengerForm.value.toleranceTime >= 0);
const isDriverFormValid = computed(() => isRouteBuilt.value && driverForm.value.departureTime && new Date(driverForm.value.departureTime) > new Date() && driverForm.value.carModel.trim().length >= 2 && driverForm.value.carColor.trim().length >= 2 && /^[А-ЯA-Z0-9-]{4,10}$/i.test(driverForm.value.carPlate.trim().toUpperCase()) && driverForm.value.totalSeats >= 1 && driverForm.value.totalSeats <= 8 && driverForm.value.estimatedDuration > 0);

watch(() => props.office, (newOffice) => {
  activeTab.value = null;
  isRouteBuilt.value = false;
  emit('route-preview', null);

  if (newOffice) {
    editForm.value = { ...newOffice };
    passengerForm.value.officeId = newOffice.id;
    driverForm.value.officeId = newOffice.id;

    clearPassengerLocation();
    clearDriverLocation();
  }
});

watch(() => props.visible, (newVal) => {
  if (!newVal) {
    emit('route-preview', null);
    emit('set-map-marker', null);
  }
});

watch(activeTab, (newTab) => {
  if (!newTab) {
    emit('route-preview', null);
    emit('set-map-marker', null);
  }
});
const clearPassengerLocation = () => {
  passengerForm.value.pickupLocation = [];
  passengerAddressQuery.value = '';
  passengerResolvedAddress.value = '';
  emit('set-map-marker', null);
};

const clearDriverLocation = () => {
  driverForm.value.routePath = [];
  driverAddressQuery.value = '';
  driverResolvedAddress.value = '';
  isRouteBuilt.value = false;
  emit('route-preview', null);
  emit('set-map-marker', null);
};

const searchPassengerAddress = async () => {
  if (!passengerAddressQuery.value.trim() || !props.office) return;
  isSearchingLocation.value = true;
  try {
    const query = `${props.office.city}, ${passengerAddressQuery.value}`;
    const result = await geocodeAddress(query, props.office.location[0], props.office.location[1]);
    passengerForm.value.pickupLocation = result.location;
    passengerResolvedAddress.value = result.address;

    emit('set-map-marker', { lat: result.location[1]!, lng: result.location[0]! });
  } catch (e) { alert("Адрес не найден. Уточните запрос."); } finally { isSearchingLocation.value = false; }
};

const searchDriverAddress = async () => {
  if (!driverAddressQuery.value.trim() || !props.office) return;
  isSearchingLocation.value = true;
  try {
    const query = `${props.office.city}, ${driverAddressQuery.value}`;
    const result = await geocodeAddress(query, props.office.location[0], props.office.location[1]);
    driverForm.value.routePath = [result.location, [props.office.location[0]!, props.office.location[1]!]];
    driverResolvedAddress.value = result.address;
    isRouteBuilt.value = false;
    emit('route-preview', null);

    emit('set-map-marker', { lat: result.location[1]!, lng: result.location[0]! });
  } catch (e) { alert("Адрес не найден. Уточните запрос."); } finally { isSearchingLocation.value = false; }
};

const openAdminEdit = () => {
  if (props.office) editForm.value = { ...props.office };
  activeTab.value = 'admin_edit';
};

const updateLocation = (target: string, lon: number, lat: number, address?: string) => {
  if (target === 'admin_edit') {
    editForm.value.location = [lon, lat];
  } else if (target === 'passenger') {
    passengerForm.value.pickupLocation = [lon, lat];
    passengerResolvedAddress.value = address || "Точка на карте";
  } else if (target === 'driver' && props.office) {
    isRouteBuilt.value = false;
    emit('route-preview', null);
    driverForm.value.routePath = [[lon, lat], [props.office.location[0]!, props.office.location[1]!]];
    driverResolvedAddress.value = address || "Точка на карте";
  }
};

const buildDriverRoute = async () => {
  if (driverForm.value.routePath.length < 2) return;
  isLoadingRoute.value = true;
  try {
    const routeData = await getRoute(driverForm.value.routePath[0]!, driverForm.value.routePath[1]!);
    driverForm.value.routePath = routeData.routePath;
    driverForm.value.estimatedDuration = routeData.durationMinutes;
    isRouteBuilt.value = true;
    emit('route-preview', routeData.routePath);
  } catch (e) {
    alert("Не удалось построить маршрут. Возможно, точка вне дороги.");
  } finally {
    isLoadingRoute.value = false;
  }
};

const submitDriver = () => {
  const dto: TripCreateRequest = {
    ...driverForm.value,
    carPlate: driverForm.value.carPlate.toUpperCase().replace(/\s/g, ''),
    departureTime: driverForm.value.departureTime ? new Date(driverForm.value.departureTime) : null
  };
  emit('submit-driver', dto);
};

defineExpose({ updateLocation });
</script>

<style scoped>
:deep(.p-drawer-mask) { background-color: transparent !important; pointer-events: none !important; }
:deep(.p-drawer) { pointer-events: auto !important; }
:deep(.p-drawer-content)::-webkit-scrollbar { width: 4px; }
:deep(.p-drawer-content)::-webkit-scrollbar-thumb { background-color: #cbd5e1; border-radius: 4px; }
</style>
