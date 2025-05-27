<script setup lang="ts">
import {VOtpInput} from "vuetify/components";

definePageMeta({
  layout: false,
  middleware: 'login'
})

useHead({
  title: 'Login - Server Manager'
})

const showPass = ref(false)
const username = ref('')
const password = ref('')

const passwordErrorMessage = ref('');

const loading = ref(false);
const invalidLogin = ref(false);
const has2FA = ref(false);

const otpCode = ref('');
const otpInput = ref<InstanceType<typeof VOtpInput> | null>(null);

const passwordExpired = ref(false);

const showOldPassword = ref(false);
const oldPassword = ref('');

const showNewPassword = ref(false);
const newPassword = ref('');

const showConfirmPassword = ref(false);
const confirmPassword = ref('');

const passwordResetErrorMessage = ref('');
const passwordResetError = ref(false);

async function onSubmit() {
  if (loading.value) {
    return;
  }

  loading.value = true;
  invalidLogin.value = false;
  passwordErrorMessage.value = "";

  await $fetch('http://localhost:8080/user/login', {
    method: 'POST',
    body: {
      username: username.value,
      password: password.value
    },
    credentials: 'include'
  }).then(() => {
    onLogin();
  }).catch(async error => {
    if (error.data.error === "OTP_REQUIRED") {
      has2FA.value = true;

      await nextTick();
      otpInput.value?.focus();
    } else if (error.status === 401) {
      invalidLogin.value = true;
      passwordErrorMessage.value = "Invalid username or password!";
    }
  })

  loading.value = false;
}

function returnFrom2FA() {
  invalidLogin.value = false;
  passwordErrorMessage.value = "";
  has2FA.value = false;
  username.value = "";
  password.value = "";
}

async function onSubmitOTP() {
  if (otpCode.value.length < 6 || loading.value) {
    return;
  }

  loading.value = true;
  invalidLogin.value = false;

  await $fetch('http://localhost:8080/user/login', {
    method: 'POST',
    body: {
      username: username.value,
      password: password.value,
      code: otpCode.value
    },
    credentials: 'include'
  }).then(() => {
    onLogin();
  }).catch(error => {
    if (error.data.error === "OTP_INVALID" || error.status === 401) {
      invalidLogin.value = true;
    }
  })

  loading.value = false;
}

async function onLogin() {
  await $fetch("http://localhost:8080/user/has-password-expired", {
    credentials: 'include'
  }).then((res) => {
    const hasPasswordExpired = res as boolean;
    if (hasPasswordExpired) {
      passwordExpired.value = hasPasswordExpired;
    } else {
      navigateToHome();
    }
  }).catch(error => {
    console.error(error)
  })
}

async function onChangePassword() {
  passwordResetErrorMessage.value = "";
  passwordResetError.value = false;

  if (loading.value) {
    return;
  }

  if (oldPassword.value === "" || newPassword.value === "" || confirmPassword.value === "") {
    passwordResetErrorMessage.value = "Please fill in all of the fields!";
    passwordResetError.value = true;
    return;
  }

  if (newPassword.value !== confirmPassword.value) {
    passwordResetErrorMessage.value = "Passwords do not match!";
    passwordResetError.value = true;
    return;
  }

  if (oldPassword.value === newPassword.value) {
    passwordResetErrorMessage.value = "New password cannot be the same as the old password!";
    passwordResetError.value = true;
    return;
  }

  if (newPassword.value.length < 4) {
    passwordResetErrorMessage.value = "Password must be at least 4 characters long!";
    passwordResetError.value = true;
    return;
  }

  if (newPassword.value.length > 30) {
    passwordResetErrorMessage.value = "Password must be less than 30 characters long!";
    passwordResetError.value = true;
    return;
  }

  loading.value = true;
  await $fetch('http://localhost:8080/user/update-password', {
    method: 'POST',
    body: {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    },
    credentials: 'include'
  }).then(() => {
    navigateToHome();
  }).catch((error) => {
    passwordResetError.value = true;

    if (error.status === 401) {
      passwordResetErrorMessage.value = "Invalid old password!";
    }
  })

  loading.value = false;
}

function navigateToHome() {
  navigateTo("/");
}

</script>

<template>
  <div class="center-page">
    <div class="form-container">
      <h1 v-if="passwordExpired" style="margin-bottom: 20px">Change your password</h1>
      <h1 v-else-if="!has2FA" style="margin-bottom: 20px;">Login</h1>
      <h1 v-else>Enter OTP code</h1>
      <v-form validate-on="submit lazy" @submit.prevent="onChangePassword" v-if="passwordExpired">
        <v-text-field label="Old Password" variant="outlined" prepend-inner-icon="mdi-form-textbox-password"
                      :append-inner-icon="showOldPassword ? 'mdi-eye' : 'mdi-eye-off'"
                      :type="showOldPassword ? 'text' : 'password'"
                      @click:append-inner="showOldPassword = !showOldPassword"
                      v-model="oldPassword" :error="passwordResetError"></v-text-field>

        <v-text-field label="New Password" variant="outlined" prepend-inner-icon="mdi-form-textbox-password"
                      :append-inner-icon="showNewPassword ? 'mdi-eye' : 'mdi-eye-off'"
                      :type="showNewPassword ? 'text' : 'password'"
                      @click:append-inner="showNewPassword = !showNewPassword"
                      v-model="newPassword" :error="passwordResetError"></v-text-field>

        <v-text-field label="Confirm Password" variant="outlined" prepend-inner-icon="mdi-form-textbox-password"
                      :append-inner-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      @click:append-inner="showConfirmPassword = !showConfirmPassword" :error="passwordResetError"
                      v-model="confirmPassword" :error-messages="passwordResetErrorMessage"></v-text-field>
        <div class="btn-container">
          <v-btn variant="tonal" @click="navigateToHome">
            Skip for now
          </v-btn>
          <v-btn variant="tonal" type="submit" :loading="loading">
            Change Password
          </v-btn>
        </div>
      </v-form>
      <v-form validate-on="submit lazy" @submit.prevent="onSubmit" v-else-if="!has2FA">
        <v-text-field label="Username" variant="outlined" prepend-inner-icon="mdi-account-circle" v-model="username"
                      :error="invalidLogin"></v-text-field>
        <v-text-field label="Password" variant="outlined" prepend-inner-icon="mdi-form-textbox-password"
                      :append-inner-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'" :type="showPass ? 'text' : 'password'"
                      @click:append-inner="showPass = !showPass" v-model="password"
                      :error-messages="passwordErrorMessage"></v-text-field>
        <div class="btn-container">
          <div/>
          <v-btn variant="tonal" type="submit" :loading="loading">
            Login
          </v-btn>
        </div>
      </v-form>
      <v-form validate-on="submit lazy" @submit.prevent="onSubmitOTP" v-else>
        <v-otp-input placeholder="0" ref="otpInput" v-model="otpCode" :error="invalidLogin"></v-otp-input>
        <div class="btn-container">
          <v-btn variant="tonal" @click="returnFrom2FA">
            Go Back
          </v-btn>
          <v-btn variant="tonal" type="submit" :loading="loading" :disabled="otpCode.length < 6">
            Login
          </v-btn>
        </div>
      </v-form>
    </div>
  </div>
</template>

<style scoped>
  .center-page {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
  }

  .form-container {
    border: 2px solid var(--border-color);
    border-radius: 5px;
    padding: 20px;
    width: 550px;
    box-shadow: 0 0 12px 0 #0000004f;
  }

  .btn-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
  }
</style>