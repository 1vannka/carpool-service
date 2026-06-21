<template>
  <div id="map" class="w-full h-full z-0"></div>
</template>

<script setup lang="ts">
import { shallowRef, watch, onMounted, onUnmounted } from 'vue';
import type { OfficeResponse } from '../types/office';

import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png';
import iconUrl from 'leaflet/dist/images/marker-icon.png';
import shadowUrl from 'leaflet/dist/images/marker-shadow.png';

const customIcon = L.icon({
  iconUrl: iconUrl,
  iconRetinaUrl: iconRetinaUrl,
  shadowUrl: shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

const props = defineProps<{
  offices: OfficeResponse[];
  center: { lat: number, lng: number } | null
}>();

const emit = defineEmits(['office-click']);

const map = shallowRef<L.Map | null>(null);
const markers: L.Marker[] = [];

onMounted(() => {
  const startLat = props.center?.lat || 55.751244;
  const startLng = props.center?.lng || 37.618423;

  map.value = L.map('map', {
    zoomControl: false,
    attributionControl: true
  }).setView([startLat, startLng], 12);

  map.value.attributionControl.setPrefix('');

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap contributors'
  }).addTo(map.value);

  L.control.zoom({ position: 'bottomright' }).addTo(map.value);

  drawMarkers();
});

onUnmounted(() => {
  if (map.value) map.value.remove();
});

watch(() => props.center, (newCenter) => {
  if (newCenter && map.value) {
    map.value.flyTo([newCenter.lat, newCenter.lng], 12, { duration: 1.5 });
  }
});

watch(() => props.offices, () => drawMarkers(), { deep: true });

const drawMarkers = () => {
  markers.forEach(m => m.remove());
  markers.length = 0;

  if (!map.value) return;

  props.offices.forEach(office => {
    const marker = L.marker([office.location[1]!, office.location[0]!], {
      icon: customIcon
    }).addTo(map.value!);

    markers.push(marker);
    marker.on('click', () => emit('office-click', office));
  });
};

const getCenter = () => {
  return map.value ? map.value.getCenter() : null;
};

defineExpose({ getCenter });
</script>

<style scoped>
:deep(.leaflet-control-attribution) {
  font-size: 0.6rem !important;
  opacity: 0.5 !important;
  background: transparent !important;
  box-shadow: none !important;
}
:deep(.leaflet-control-attribution a) {
  color: #555 !important;
  text-decoration: none;
}
</style>
