<script setup lang="ts">

// -------------------------------------
// Username change functionality
// -------------------------------------

const username = ref('');

const { data } = await useFetch<UserInfo>("http://localhost:8080/user/info", {
  method: 'GET',
  credentials: 'include'
});

watchEffect(() => {
  if (data.value?.username && username.value === '') {
    username.value = data.value.username;
  }
});

const usernameLoading = ref(false);
const usernameError = ref('');

async function updateUsername() {
  usernameLoading.value = true;
  usernameError.value = '';

  if (username.value.trim() === '') {
    usernameError.value = "Username cannot be empty!";
    usernameLoading.value = false;
    return;
  }

  if (username.value.length < 4) {
    usernameError.value = "Username must be at least 4 characters long!";
    usernameLoading.value = false;
    return;
  }

  await $fetch('http://localhost:8080/user/update-username', {
    method: 'POST',
    body: {
      newUsername: username.value
    },
    credentials: 'include'
  }).then(() => {
    alert("Username changed successfully!"); // Change this to a toast or something
  }).catch((error) => {
    usernameError.value = error.data.message;
  }).finally(() => {
    usernameLoading.value = false;
  });
}


// -------------------------------------
// OTP functionality
// -------------------------------------

const otpActive = ref(false);

const { data: otpData } = await useFetch<boolean>("http://localhost:8080/user/has-2fa", {
  method: 'GET',
  credentials: 'include'
});

watchEffect(() => {
  if (otpData.value !== undefined) {
    otpActive.value = otpData.value ?? false;
  }
});

function otpButtonClicked() {
  otpActive.value = !otpActive.value;
}

// -------------------------------------
// Password change functionality
// -------------------------------------

const showOldPassword = ref(false);
const oldPassword = ref('');

const showNewPassword = ref(false);
const newPassword = ref('');

const showConfirmPassword = ref(false);
const confirmPassword = ref('');

const passwordResetErrorMessage = ref('');
const passwordResetError = ref(false);

const passwordLoading = ref(false);

// Copied from the login page
async function onChangePassword() {
  passwordResetErrorMessage.value = "";
  passwordResetError.value = false;

  if (passwordLoading.value) {
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

  passwordLoading.value = true;
  await $fetch('http://localhost:8080/user/update-password', {
    method: 'POST',
    body: {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    },
    credentials: 'include'
  }).then(() => {
    alert("Password changed successfully!"); // Change this to a toast or something
  }).catch((error) => {
    passwordResetError.value = true;

    if (error.status === 401) {
      passwordResetErrorMessage.value = "Invalid old password!";
    }
  }).finally(() => {
    passwordLoading.value = false;
  });
}

</script>

<template>
  <div class="content-box">
    <div class="fitted-card">
      <h2>
        Username
      </h2>
      <VDivider/>
      <div class="username-container">
        <v-text-field label="Username" variant="outlined" prepend-inner-icon="mdi-account-circle"
                      v-model="username" :error-messages="usernameError"/>
        <VBtn variant="tonal" :loading="usernameLoading" @click="updateUsername">Change Username</VBtn>
      </div>
    </div>

    <div class="fitted-card">
      <h2>
        OTP Authentication
      </h2>
      <VDivider/>
      <div class="otp-container">
        <p>Status:
          <span v-if="otpActive" class="active-opt-text">Active</span>
          <span v-else class="not-active-opt-text">Not Active</span>
        </p>
        <VBtn @click="otpButtonClicked" variant="tonal">{{ otpActive ? "Disable" : "Enable" }} OTP</VBtn>
      </div>
    </div>

    <div class="fitted-card">
      <h2>
        Password change
      </h2>
      <VDivider/>
      <div class="password-change-container">
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
        <VBtn variant="tonal" :loading="passwordLoading" @click="onChangePassword">Change Password</VBtn>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-box {
  width: 100%;
  overflow: auto;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-content: flex-start;
  align-items: flex-start;
}

/*noinspection CssUnusedSymbol*/
.full-screen-card {
  background: var(--transparent-bg-color);
  --card-margin: 15px;
  margin: var(--card-margin);
  border-radius: 5px;
  height: calc(100% - (var(--card-margin) * 2));
  box-shadow: 0 0 5px 1px var(--shadow-color);
}

.fitted-card {
  background: var(--transparent-bg-color);
  --card-margin: 15px;
  margin: var(--card-margin);
  border-radius: 5px;
  box-shadow: 0 0 5px 1px var(--shadow-color);
  min-width: 250px;
  width: calc(50% - (var(--card-margin)*2));
}

/* Media query does not support variables, so I've hardcoded the values */
/* sidebar + (margin * 4) + (cardMin * 2) */
@media (max-width: calc(250px + (15px * 4) + (250px * 2))) {
  .fitted-card {
    width: calc(100% - (var(--card-margin)*2));
  }
}

h2 {
  padding: 2px 10px;
}

.username-container {
  padding: 10px;
  margin-top: 10px;
  display: flex;
  flex-direction: column;
}

.otp-container {
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.active-opt-text {
  font-weight: 600;
  color: var(--green-alert);
}

.not-active-opt-text {
  font-weight: 600;
  color: var(--red-alert);
}

.password-change-container {
  padding: 10px;
  margin-top: 10px;
  display: flex;
  flex-direction: column;
}
</style>