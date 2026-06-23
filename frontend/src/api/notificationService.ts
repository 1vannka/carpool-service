import { fetchEventSource } from '@microsoft/fetch-event-source';
import type { SseNotification } from '@/types/notification.ts';

const BASE_URL = 'http://localhost:8080/api';

export const notificationService = {
  connect(onMessage: (notification: SseNotification) => void) {
    const token = localStorage.getItem('access_token');

    if (!token) {
      console.warn('Нет токена для подключения к SSE');
      return;
    }

    fetchEventSource(`${BASE_URL}/notifications/subscribe`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'text/event-stream',
      },
      onmessage(msg) {
        if (msg.event === 'INIT' || msg.event === 'PING') {
          console.log(`[SSE System]: ${msg.data}`);
          return;
        }

        if (msg.event === 'NOTIFICATION') {
          try {
            const notification: SseNotification = JSON.parse(msg.data);
            onMessage(notification);
          } catch (e) {
            console.error('Ошибка парсинга уведомления', e);
          }
        }
      },
      onclose() {
        console.log('[SSE] Соединение закрыто сервером');
      },
      onerror(err) {
        console.error('[SSE] Ошибка соединения', err);
      }
    });
  }
};
