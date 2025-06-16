<script setup lang="ts">
import type {DataTableHeader} from "vuetify/framework";
import Toast from "~/components/Toast.vue";

const { data } = await useFetch<ManagementUserInfo[]>("http://localhost:8080/management/get-users", {
  method: 'GET',
  credentials: 'include'
});

const { data: roles } = await useFetch<UserRole[]>("http://localhost:8080/management/roles/get", {
  method: 'GET',
  credentials: 'include'
});

const headers : DataTableHeader[] = [
  { title: 'Username', key: 'username' },
  { title: 'Password Expired', key: 'passwordExpired' },
  { title: 'Has 2FA', key: 'has2fa' },
  { title: 'Is Admin', key: 'admin' },
  { title: 'Current Role', key: 'roleName' },
  { title: 'Actions', key: 'actions', align: 'end', sortable: false, width: '60px' }
]

const toastOpened = ref(false);
const toastType = ref<"error" | "success" | "warning" | "info" | undefined>("success");
const toastContents = ref("User created successfully!");

const addUserDialog = ref(false);
const addUserLoading = ref(false);
const username = ref("");
const password = ref("");
const passwordExpired = ref(true);
const isAdmin = ref(false);
const selectedRole = ref<UserRole | null>(null);
const selectedRoleError = ref("");

async function addUser() {
  selectedRoleError.value = "";

  const registerData : RegisterUser = {
    username: username.value,
    initPassword: password.value,
    passwordExpired: passwordExpired.value,
    admin: isAdmin.value
  };

  if (!isAdmin.value) {
    if (selectedRole.value) {
      registerData.roleId = selectedRole.value.id;
    } else {
      selectedRoleError.value = "Please select a role.";
      return;
    }
  }

  addUserLoading.value = true;

  $fetch("http://localhost:8080/management/register-user", {
    method: 'POST',
    body: registerData,
    credentials: 'include'
  }).then((response) => {
    toastContents.value = `User ${username.value} created successfully!`;
    toastType.value = "success";
    toastOpened.value = true;

    addUserDialog.value = false;
    addUserLoading.value = false;
    console.log(response);

    data.value?.push({
      id: response as number,
      username: username.value,
      passwordExpired: passwordExpired.value,
      has2fa: false,
      admin: isAdmin.value,
      roleName: isAdmin.value ? "No Role" : selectedRole.value?.name ?? "No Role",
      currentUser: false
    });

    // Revert form fields
    username.value = "";
    password.value = "";
    passwordExpired.value = true;
    isAdmin.value = false;
    selectedRole.value = null;
  }).catch((error) => {
    toastContents.value = error.data.message;
    toastType.value = "error";
    toastOpened.value = true;
    addUserLoading.value = false;
  });
}

function editUser(editId: number) {
  console.log("Not implemented! Edit profile ID " + editId);
}

async function deleteUser(deleteId: number) {
  await $fetch ("http://localhost:8080/management/delete-user", {
    method: 'DELETE',
    body: { userId: deleteId },
    credentials: 'include'
  }).then(() => {
    toastContents.value = `User with ID ${deleteId} deleted successfully!`;
    toastType.value = "success";
    toastOpened.value = true;

    data.value = data.value?.filter(user => user.id !== deleteId) ?? null;
  }).catch((error) => {
    toastContents.value = error.data.message;
    toastType.value = "error";
    toastOpened.value = true;
  });
}

function rowProps(item: any) {
  return item.item.currentUser ? { class: 'current-user-row' } : {};
}

function selectVRole(item: UserRole) {
  return {
    title: item.name,
  }
}
</script>

<template>
  <Toast :type="toastType" enable-progress v-model:opened="toastOpened" :time="5000">{{ toastContents }}</Toast>
  <v-dialog v-model="addUserDialog" width="auto" :persistent="addUserLoading">
    <div class="dialog-card">
      <h2 style="padding: 2px 10px;">Register User</h2>
      <VDivider/>
      <v-form validate-on="submit lazy" @submit.prevent="addUser" style="padding: 10px">
        <v-text-field label="Username" name="username" v-model="username" required></v-text-field>
        <v-text-field label="Password" name="password" type="password" v-model="password" required></v-text-field>
        <v-checkbox label="Force Password Change" name="forcePasswordChange" v-model="passwordExpired"></v-checkbox>
        <div class="admin-role-container">
          <v-checkbox label="Is Admin" name="isAdmin" v-model="isAdmin"></v-checkbox>
          <v-select
              label="Select Role"
              :items="roles ?? []"
              :item-props="selectVRole"
              variant="outlined"
              max-width="250px"
              v-model="selectedRole"
              :disabled="isAdmin"
              :error-messages="selectedRoleError"
          />
        </div>
        <div class="dialog-button-container">
          <VBtn variant="tonal" :disabled="addUserLoading" @click="addUserDialog = false">Close</VBtn>
          <VBtn variant="tonal" :loading="addUserLoading" type="submit">Register</VBtn>
        </div>
      </v-form>
    </div>
  </v-dialog>

  <div class="full-screen-card card-flex">
    <div class="header-contents">
      <h2>
        Users
      </h2>
      <v-btn variant="tonal" prepend-icon="mdi-account-plus" @click="() => addUserDialog = true">Add User</v-btn>
    </div>

    <VDivider/>
    <v-data-table :items="data ?? []" :headers="headers" class="table" :row-props="rowProps">
      <template v-slot:item.actions="{ item }">
        <div class="action-buttons">
          <v-btn icon="mdi-pencil" variant="tonal" size="small" @click="editUser(item.id)"
                 :disabled="item.currentUser || true"></v-btn>
          <v-btn icon="mdi-delete" variant="tonal" size="small" @click="deleteUser(item.id)"
                 :disabled="item.currentUser"></v-btn>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<style scoped>
.card-flex {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100%;
}

.table {
  height: 100%;
  background: transparent;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.header-contents {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
}


.dialog-card {
  background: var(--dialog-bg-color);
  border-radius: 5px;
  box-shadow: 0 0 5px 1px var(--shadow-color);
  width: 550px;
}

@media (max-width: 600px) {
  .dialog-card {
    width: 80vw;
  }
}

.admin-role-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.dialog-button-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}
</style>