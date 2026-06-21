<template>
  <Dialog :visible="visible" @update:visible="$emit('update:visible', $event)" modal :header="office?.name || 'Офис'" :style="{ width: '90vw', maxWidth: '450px' }">

    <div v-if="!activeTab" class="flex flex-col gap-3 py-2">
      <p class="text-sm text-gray-500 mb-2">📍 {{ office?.address }}</p>

      <Button label="Ищу поездку (Пассажир)" icon="pi pi-user" outlined class="w-full" @click="activeTab = 'passenger'" />
      <Button label="Создаю поездку (Водитель)" icon="pi pi-car" severity="success" class="w-full" @click="activeTab = 'driver'" />

      <template v-if="isAdmin">
        <Divider />
        <Button label="Редактировать офис" icon="pi pi-pencil" severity="help" class="w-full" @click="openAdminEdit" />
        <Button label="Удалить офис" icon="pi pi-trash" severity="danger" text class="w-full" @click="$emit('delete', office?.id)" />
      </template>
    </div>

    <div v-else-if="activeTab === 'admin_edit'" class="flex flex-col gap-3">
      <h3 class="font-bold text-purple-700 mb-2">Редактирование офиса</h3>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Название</label>
        <input type="text" v-model="editForm.name" class="p-2 border rounded outline-none focus:border-purple-500" />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Город</label>
        <input type="text" v-model="editForm.city" class="p-2 border rounded outline-none focus:border-purple-500" />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Адрес</label>
        <input type="text" v-model="editForm.address" class="p-2 border rounded outline-none focus:border-purple-500" />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Координаты</label>
        <Button label="Указать новые на карте" icon="pi pi-map-marker" outlined severity="help" @click="$emit('request-location', 'admin_edit')" />
      </div>
      <div class="flex justify-between mt-4">
        <Button label="Отмена" text @click="activeTab = null" />
        <Button label="Сохранить" severity="help" @click="$emit('update', office?.id, editForm)" />
      </div>
    </div>

    <div v-else-if="activeTab === 'passenger'" class="flex flex-col gap-3">
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Откуда вас забрать?</label>
        <Button v-if="passengerForm.pickupLocation.length === 0" label="Указать на карте" icon="pi pi-map-marker" outlined @click="$emit('request-location', 'passenger')" />
        <div v-else class="flex items-center justify-between bg-green-50 p-2 rounded border border-green-200">
          <span class="text-sm text-green-700"><i class="pi pi-check-circle mr-1"></i> Точка выбрана</span>
          <Button icon="pi pi-pencil" text rounded size="small" @click="$emit('request-location', 'passenger')" />
        </div>
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold text-gray-700">Желаемое время выезда</label>
        <input type="datetime-local" v-model="passengerForm.targetTime" class="p-2 border rounded outline-none focus:border-blue-500" required />
      </div>
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold text-gray-700">Отклонение (в минутах)</label>
        <input type="number" v-model="passengerForm.toleranceTime" min="0" max="60" class="p-2 border rounded outline-none focus:border-blue-500" required />
      </div>
      <div class="flex justify-between mt-4">
        <Button label="Назад" text severity="secondary" @click="activeTab = null" />
        <Button label="Оформить заявку" @click="$emit('submit-passenger', passengerForm)" :disabled="passengerForm.pickupLocation.length === 0" />
      </div>
    </div>

    <div v-else-if="activeTab === 'driver'" class="flex flex-col gap-3 overflow-y-auto max-h-[60vh] pr-2">
      <div class="flex flex-col gap-1">
        <label class="text-sm font-semibold">Точка старта маршрута</label>
        <Button v-if="driverForm.routePath.length === 0" label="Указать на карте" icon="pi pi-map-marker" outlined @click="$emit('request-location', 'driver')" />
        <div v-else class="flex items-center justify-between bg-green-50 p-2 rounded border border-green-200">
          <span class="text-sm text-green-700"><i class="pi pi-check-circle mr-1"></i> Точка старта выбрана</span>
          <Button icon="pi pi-pencil" text rounded size="small" @click="$emit('request-location', 'driver')" />
        </div>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Время выезда</label>
          <input type="datetime-local" v-model="driverForm.departureTime" class="p-2 border rounded outline-none focus:border-blue-500" required />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">В пути (мин)</label>
          <input type="number" v-model="driverForm.estimatedDuration" min="1" class="p-2 border rounded outline-none focus:border-blue-500" required />
        </div>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Автомобиль</label>
          <input type="text" v-model="driverForm.carModel" placeholder="Kia Rio" class="p-2 border rounded outline-none focus:border-blue-500" required />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Мест</label>
          <input type="number" v-model="driverForm.totalSeats" min="1" max="6" class="p-2 border rounded outline-none focus:border-blue-500" required />
        </div>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Цвет</label>
          <input type="text" v-model="driverForm.carColor" placeholder="Белый" class="p-2 border rounded outline-none focus:border-blue-500" required />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-sm font-semibold text-gray-700">Номер</label>
          <input type="text" v-model="driverForm.carPlate" placeholder="А777АА77" class="p-2 border rounded outline-none focus:border-blue-500 uppercase" required />
        </div>
      </div>
      <div class="flex justify-between mt-4">
        <Button label="Назад" text severity="secondary" @click="activeTab = null" />
        <Button label="Создать маршрут" severity="success" @click="$emit('submit-driver', driverForm)" :disabled="driverForm.routePath.length === 0" />
      </div>
    </div>

  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Divider from 'primevue/divider';
import type { OfficeResponse } from '../api/officeService';

const props = defineProps<{ visible: boolean; office: OfficeResponse | null; isAdmin: boolean }>();
const emit = defineEmits(['update:visible', 'request-location', 'delete', 'update', 'submit-passenger', 'submit-driver']);

const activeTab = ref<string | null>(null);

const editForm = ref({ name: '', city: '', address: '', location: [] as number[] });
const passengerForm = ref({ officeId: null as number | null, pickupLocation: [] as number[], targetTime: '', toleranceTime: 15 });
const driverForm = ref({ officeId: null as number | null, departureTime: '', estimatedDuration: 30, totalSeats: 3, carModel: '', carColor: 'Черный', carPlate: 'А000АА', routePath: [] as number[][] });

watch(() => props.office, (newOffice) => {
  activeTab.value = null;
  if (newOffice) {
    editForm.value = { ...newOffice };
    passengerForm.value.officeId = newOffice.id;
    driverForm.value.officeId = newOffice.id;
    passengerForm.value.pickupLocation = [];
    driverForm.value.routePath = [];
  }
});

const openAdminEdit = () => {
  if (props.office) editForm.value = { ...props.office };
  activeTab.value = 'admin_edit';
};

const updateLocation = (target: string, lon: number, lat: number) => {
  if (target === 'admin_edit') {
    editForm.value.location = [lon, lat];
  } else if (target === 'passenger') {
    passengerForm.value.pickupLocation = [lon, lat];
  } else if (target === 'driver' && props.office) {
    driverForm.value.routePath = [
      [lon, lat],
      [props.office.location[0], props.office.location[1]]
    ];
  }
};

defineExpose({ updateLocation });
</script>
