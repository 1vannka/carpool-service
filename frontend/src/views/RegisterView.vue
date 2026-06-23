<template>
  <div class="flex items-center justify-center min-h-screen bg-slate-50">
    <Card class="w-full max-w-md shadow-lg">
      <template #title>
        <div class="text-center text-2xl font-bold">Регистрация</div>
      </template>

      <template #content>
        <form @submit.prevent="handleRegister" class="flex flex-col gap-4 mt-4">

          <div class="flex gap-4">
            <div class="flex flex-col gap-2 w-1/2">
              <label for="firstName" class="font-semibold text-gray-700">Имя</label>
              <InputText id="firstName" v-model="firstName" placeholder="Иван" required class="w-full" />
            </div>

            <div class="flex flex-col gap-2 w-1/2">
              <label for="lastName" class="font-semibold text-gray-700">Фамилия</label>
              <InputText id="lastName" v-model="lastName" placeholder="Иванов" required class="w-full" />
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <label for="email" class="font-semibold text-gray-700">Email</label>
            <InputText id="email" v-model="email" type="email" placeholder="user@company.com" required />
          </div>

          <div class="flex flex-col gap-2">
            <label for="password" class="font-semibold text-gray-700">Пароль</label>
            <Password id="password" v-model="password" toggleMask placeholder="Минимум 8 символов" required inputClass="w-full" />
            <small v-if="passwordError" class="text-red-500">{{ passwordError }}</small>
          </div>

          <small v-if="errorMessage" class="text-red-500 text-center">{{ errorMessage }}</small>
          <small v-if="successMessage" class="text-green-600 text-center font-semibold">{{ successMessage }}</small>

          <Button type="submit" label="Создать аккаунт" :loading="isLoading" class="w-full mt-2" />

          <div class="text-center mt-2 text-sm text-gray-600">
            Уже есть аккаунт?
            <router-link to="/login" class="text-blue-500 hover:underline font-semibold">
              Войти
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
import httpClient from '../api/httpClient';
import Card from 'primevue/card';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Button from 'primevue/button';

const router = useRouter();

const firstName = ref('');
const lastName = ref('');
const email = ref('');
const password = ref('');

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const passwordError = ref('');

const handleRegister = async () => {
  if (password.value.length < 8) {
    passwordError.value = 'Пароль должен содержать минимум 8 символов';
    return;
  }

  passwordError.value = '';
  errorMessage.value = '';
  isLoading.value = true;

  try {
    await httpClient.post('/auth/register', {
      email: email.value,
      password: password.value,
      firstName: firstName.value,
      lastName: lastName.value
    });

    successMessage.value = 'Регистрация успешна';

    setTimeout(() => {
      router.push('/login');
    }, 1500);

  } catch (error: any) {
    console.error('Ошибка регистрации:', error);
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage.value = error.response.data.message;
    } else {
      errorMessage.value = 'Произошла ошибка при регистрации. Попробуйте позже.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
:deep(.p-password) { width: 100%; }
</style>
