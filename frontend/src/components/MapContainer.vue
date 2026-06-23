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
  iconUrl, iconRetinaUrl, shadowUrl,
  iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
});

const props = defineProps<{
  offices: OfficeResponse[];
  center: { lat: number, lng: number } | null;
  driverRoute?: number[][] | null;
  searchMarker?: { lat: number, lng: number } | null;
  passengerMarker?: { lat: number, lng: number } | null;
  driverPassengers?: { id: number, lat: number, lng: number, name: string, status: string }[] | null;
}>();

const emit = defineEmits(['office-click']);

const map = shallowRef<L.Map | null>(null);
const markers: L.Marker[] = [];
const routeLayer = shallowRef<L.Polyline | null>(null);
const searchMarkerLayer = shallowRef<L.Marker | null>(null);
const passengerMarkerLayer = shallowRef<L.CircleMarker | null>(null);
const driverPassengersLayers: L.CircleMarker[] = [];

const drawMarkers = () => {
  markers.forEach(m => m.remove());
  markers.length = 0;
  if (!map.value) return;

  props.offices.forEach(office => {
    const marker = L.marker([office.location[1]!, office.location[0]!], { icon: customIcon }).addTo(map.value!);
    markers.push(marker);
    marker.on('click', () => emit('office-click', office));
  });
};

const drawRoute = (newRoute: number[][] | null | undefined) => {
  if (!map.value) return;
  if (routeLayer.value) { map.value.removeLayer(routeLayer.value); routeLayer.value = null; }
  if (newRoute && newRoute.length > 0) {
    const latLngs = newRoute.map(coord => [coord[1], coord[0]] as [number, number]);
    routeLayer.value = L.polyline(latLngs, { color: '#3b82f6', weight: 5, opacity: 0.8 }).addTo(map.value);
    map.value.fitBounds(routeLayer.value.getBounds(), { padding: [50, 50] });
  }
};

const drawPassengerMarker = (newLoc: { lat: number, lng: number } | null | undefined) => {
  if (!map.value) return;
  if (passengerMarkerLayer.value) { map.value.removeLayer(passengerMarkerLayer.value); passengerMarkerLayer.value = null; }
  if (newLoc) {
    passengerMarkerLayer.value = L.circleMarker([newLoc.lat, newLoc.lng], {
      radius: 8, color: '#d97706', fillColor: '#fcd34d', fillOpacity: 0.9, weight: 3
    }).addTo(map.value);
    passengerMarkerLayer.value.bindPopup('<b>Ваша точка посадки</b>');
  }
};

const drawDriverPassengers = (passengers: any[] | null | undefined) => {
  if (!map.value) return;

  driverPassengersLayers.forEach(layer => map.value!.removeLayer(layer));
  driverPassengersLayers.length = 0;

  if (passengers && passengers.length > 0) {
    passengers.forEach(p => {
      const isApproved = p.status === 'CONFIRMED';
      const marker = L.circleMarker([p.lat, p.lng], {
        radius: 8,
        color: isApproved ? '#059669' : '#d97706',
        fillColor: isApproved ? '#10b981' : '#fcd34d',
        fillOpacity: 0.9, weight: 3
      }).addTo(map.value!);

      marker.bindPopup(`<b>${isApproved ? 'Попутчик:' : 'Заявка от:'}</b> ${p.name}`);
      driverPassengersLayers.push(marker);
    });
  }
};

onMounted(() => {
  const startLat = props.center?.lat || 55.751244;
  const startLng = props.center?.lng || 37.618423;

  map.value = L.map('map', { zoomControl: false, attributionControl: true }).setView([startLat, startLng], 12);
  map.value.attributionControl.setPrefix('');
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '© OpenStreetMap' }).addTo(map.value);
  L.control.zoom({ position: 'bottomright' }).addTo(map.value);

  drawMarkers();
  drawRoute(props.driverRoute);
  drawPassengerMarker(props.passengerMarker);
  drawDriverPassengers(props.driverPassengers);
});

onUnmounted(() => { if (map.value) map.value.remove(); });

watch(() => props.center, (newCenter) => {
  if (newCenter && map.value && !props.searchMarker) {
    map.value.flyTo([newCenter.lat, newCenter.lng], 12, { duration: 1.5 });
  }
});

watch(() => props.offices, () => drawMarkers(), { deep: true, immediate: true });
watch(() => props.driverRoute, (newRoute) => drawRoute(newRoute), { deep: true, immediate: true });
watch(() => props.passengerMarker, (newLoc) => drawPassengerMarker(newLoc), { deep: true, immediate: true });
watch(() => props.driverPassengers, (newPassengers) => drawDriverPassengers(newPassengers), { deep: true, immediate: true });

watch(() => props.searchMarker, (newLoc) => {
  if (!map.value) return;
  if (searchMarkerLayer.value) { map.value.removeLayer(searchMarkerLayer.value); searchMarkerLayer.value = null; }
  if (newLoc) {
    searchMarkerLayer.value = L.marker([newLoc.lat, newLoc.lng], { icon: customIcon }).addTo(map.value);
    searchMarkerLayer.value.bindPopup('<b>Искомый адрес</b>').openPopup();
    map.value.flyTo([newLoc.lat, newLoc.lng], 16, { duration: 1.5 });
  }
}, { deep: true });

const getCenter = () => map.value ? map.value.getCenter() : null;
defineExpose({ getCenter });
</script>

<style scoped>
:deep(.leaflet-control-attribution) { font-size: 0.6rem !important; opacity: 0.5 !important; background: transparent !important; box-shadow: none !important; }
:deep(.leaflet-control-attribution a) { color: #555 !important; text-decoration: none; }
</style>
