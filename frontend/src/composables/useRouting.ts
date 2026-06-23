import axios from 'axios';

export function useRouting() {
  const ORS_API_KEY = import.meta.env.VITE_ORS_API_KEY;
  const DIRECTIONS_URL = 'https://api.openrouteservice.org/v2/directions/driving-car';
  const GEOCODE_SEARCH_URL = 'https://api.openrouteservice.org/geocode/search';
  const GEOCODE_REVERSE_URL = 'https://api.openrouteservice.org/geocode/reverse';

  const getRoute = async (startLonLat: number[], endLonLat: number[]) => {
    try {
      const response = await axios.get(DIRECTIONS_URL, {
        params: {
          api_key: ORS_API_KEY,
          start: `${startLonLat[0]},${startLonLat[1]}`,
          end: `${endLonLat[0]},${endLonLat[1]}`
        }
      });
      const data = response.data.features[0];
      const geometry: number[][] = data.geometry.coordinates;
      const durationMinutes = Math.round(data.properties.summary.duration / 60);

      return { routePath: geometry, durationMinutes };
    } catch (error) {
      console.error("Ошибка маршрутизации ORS:", error);
      throw error;
    }
  };

  const geocodeAddress = async (query: string, cityLon?: number, cityLat?: number) => {
    try {
      const params: any = { api_key: ORS_API_KEY, text: query, size: 1 };

      if (cityLon !== undefined && cityLat !== undefined) {
        params['focus.point.lon'] = cityLon;
        params['focus.point.lat'] = cityLat;
      }

      const response = await axios.get(GEOCODE_SEARCH_URL, { params });
      if (response.data.features && response.data.features.length > 0) {
        const feature = response.data.features[0];
        return {
          location: feature.geometry.coordinates as number[],
          address: formatAddress(feature.properties)
        };
      }
      throw new Error("Адрес не найден");
    } catch (error) {
      console.error("Ошибка геокодирования ORS:", error);
      throw error;
    }
  };

  const reverseGeocode = async (lon: number, lat: number) => {
    try {
      const response = await axios.get(GEOCODE_REVERSE_URL, {
        params: {
          api_key: ORS_API_KEY,
          'point.lon': lon,
          'point.lat': lat,
          size: 3,
          layers: 'address,street'
        }
      });

      if (response.data.features && response.data.features.length > 0) {
        const bestFeature = response.data.features.find((f: any) => f.properties.street && f.properties.housenumber)
          || response.data.features[0];

        return formatAddress(bestFeature.properties);
      }
      return "Неизвестный адрес";
    } catch (error) {
      console.error("Ошибка обратного геокодирования ORS:", error);
      return "Неизвестный адрес";
    }
  };

  const formatAddress = (properties: any): string => {
    if (properties.street && properties.housenumber) {
      return `${properties.street}, ${properties.housenumber}`;
    } else if (properties.street) {
      return properties.street;
    } else if (properties.label) {
      return properties.label.split(',')[0];
    }
    return properties.name || "Неизвестный адрес";
  };

  return { getRoute, geocodeAddress, reverseGeocode };
}
