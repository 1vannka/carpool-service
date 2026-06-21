<template>
  <div class="flex items-center justify-center min-h-screen bg-slate-50">

    <Card class="w-full max-w-md shadow-lg">
      <template #title>
        <div class="text-center text-2xl font-bold">Вход в систему</div>
      </template>

      <template #content>
        <form @submit.prevent="handleLogin" class="flex flex-col gap-4 mt-4">

          <div class="flex flex-col gap-2">
            <label for="email" class="font-semibold text-gray-700">Email</label>
            <InputText
              id="email"
              v-model="email"
              type="email"
              placeholder="Введите ваш email"
              required
            />
          </div>

          <div class="flex flex-col gap-2">
            <label for="password" class="font-semibold text-gray-700">Пароль</label>
            <Password
              id="password"
              v-model="password"
              :feedback="false"
              toggleMask
              placeholder="Введите пароль"
              required
              inputClass="w-full"
            />
          </div>

          <small v-if="errorMessage" class="text-red-500 text-center">{{ errorMessage }}</small>

          <Button
            type="submit"
            label="Войти"
            icon="pi pi-sign-in"
            :loading="isLoading"
            class="w-full mt-2"
          />
          <div class="text-center mt-2 text-sm text-gray-600">
            Нет аккаунта?
            <router-link to="/register" class="text-blue-500 hover:underline font-semibold">
              Зарегистрироваться
            </router-link>
          </div>
        </form>
      </template>
    </Card>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';

import Card from 'primevue/card';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Button from 'primevue/button';

const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const isLoading = ref(false);
const errorMessage = ref('');

const handleLogin = async () => {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    await authStore.login({ email: email.value, password: password.value });
    router.push('/');
  } catch (error: any) {
    errorMessage.value = 'Неверный email или пароль';
    console.error(error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
:deep(.p-password) {
  width: 100%;
}
</style>
