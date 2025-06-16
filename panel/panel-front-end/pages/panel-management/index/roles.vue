<script setup lang="ts">

import Toast from "~/components/Toast.vue";

const { data: roles } = await useFetch<UserRole[]>("http://localhost:8080/management/roles/get", {
  method: 'GET',
  credentials: 'include'
});

const defaultRole: UserRole = {
  id: -1,
  name: "<New Role>",
  permissions: new Map<string, boolean>()
};

const rolesData = computed(() => {
  let rolesList : UserRole[] = roles?.value ?? [];
  rolesList.unshift(defaultRole);
  return rolesList;
});

const { data: perms } = await useFetch<string[]>("http://localhost:8080/management/roles/get-permissions", {
  method: 'GET',
  credentials: 'include'
});
const permsData = ref<boolean[]>(perms.value?.map(() => false) ?? []);

// const tempParams: string[] = [];
// for (let i = 0; i < 150; i++) {
//   tempParams.push("test");
// }

const selectedRole = ref<UserRole | null>(defaultRole);
const isNewRole = computed(() => {
  return selectedRole.value?.id === -1;
});

function selectVRole(item: UserRole) {
  return {
    title: item.name,
  }
}

const selectedRoleName = ref<String>("");
function selectRoleChange() {
  if (selectedRole.value?.id !== -1) {
    selectedRoleName.value = selectedRole.value?.name ?? "";
    permsData.value = perms.value?.map(perm => (selectedRole.value?.permissions as any)[perm] ?? false) ?? [];
  } else {
    selectedRoleName.value = "";
    permsData.value = perms.value?.map(() => false) ?? [];
  }
}

const toastOpened = ref(false);
const toastType = ref<"error" | "success" | "warning" | "info" | undefined>("success");
const toastContents = ref("Role created successfully!");

const deleteLoading = ref(false);
async function deleteRole() {
  if (selectedRole.value?.id === -1) {
    return;
  }
  deleteLoading.value = true;

  await $fetch('http://localhost:8080/management/roles/delete', {
    method: 'DELETE',
    body: {
      roleId: selectedRole.value?.id
    },
    credentials: 'include'
  }).then(() => {
    toastContents.value = "Role deleted successfully!";
    toastType.value = "success";
    toastOpened.value = true;

    if (roles.value) {
      const idx = roles.value.findIndex(r => r.id === selectedRole.value?.id);
      if (idx !== -1) roles.value.splice(idx, 1);
      selectedRole.value = defaultRole;
      selectRoleChange();
    }
  }).catch((error) => {
    toastContents.value = error.data.message;
    toastType.value = "error";
    toastOpened.value = true;
  }).finally(() => {
    deleteLoading.value = false;
  });

}

function createModifyRole() {
  let newRole : UserRole = {
    name: selectedRoleName.value.toString(),
    permissions: new Map<string, boolean>()
  }
  if (selectedRole.value?.id !== -1) {
    newRole.id = selectedRole.value?.id;
  }
  perms.value?.forEach((perm, idx) => {
    (newRole.permissions as any)[perm] = permsData.value[idx];
  });

  if (selectedRole.value?.id !== -1) {
    // Modify existing role
    $fetch('http://localhost:8080/management/roles/update', {
      method: 'PATCH',
      body: newRole,
      credentials: 'include'
    }).then(() => {
      toastContents.value = "Role modified successfully!";
      toastType.value = "success";
      toastOpened.value = true;

      if (roles.value) {
        const idx = roles.value.findIndex(r => r.id === selectedRole.value?.id);
        if (idx !== -1) roles.value[idx] = newRole;
        selectedRole.value = newRole;
      }
    }).catch((error) => {
      toastContents.value = error.data.message;
      toastType.value = "error";
      toastOpened.value = true;
    });
  } else {
    // Create new role
    $fetch('http://localhost:8080/management/roles/create', {
      method: 'POST',
      body: newRole,
      credentials: 'include'
    }).then((response) => {
      toastContents.value = "Role created successfully!";
      toastType.value = "success";
      toastOpened.value = true;

      if (roles.value) {
        const createdRole = { ...newRole, id: response as number };
        roles.value.push(createdRole);
        selectedRole.value = createdRole;
        selectRoleChange();
      }
    }).catch((error) => {
      toastContents.value = error.data.message;
      toastType.value = "error";
      toastOpened.value = true;
    });
  }
}
</script>

<template>
  <Toast :type="toastType" enable-progress v-model:opened="toastOpened" :time="5000">{{ toastContents }}</Toast>
  <div class="full-screen-card card-flex">
    <div class="create-select-role-box">
      <h2>Create/Select Role</h2>
      <VDivider/>
      <div class="create-select-role">
        <v-select
            label="Select Role"
            :items="rolesData ?? []"
            :item-props="selectVRole"
            variant="outlined"
            max-width="250px"
            v-model="selectedRole"
            @update:modelValue="selectRoleChange"
        >
        </v-select>
        <v-text-field label="Name" variant="outlined" max-width="250px" v-model="selectedRoleName"/>
        <v-btn variant="tonal" prepend-icon="mdi-content-save" @click="deleteRole"
               :disabled="isNewRole" :loading="deleteLoading">Delete Role</v-btn>
      </div>
    </div>
    <div class="role-permissions-box">
      <VDivider/>
      <h2>Role Permissions</h2>
      <div class="perms-box">
        <v-checkbox v-for="(perm, idx) in perms"
                    :key="perm"
                    :label="perm"
                    v-model="permsData[idx]"/>
      </div>
    </div>
    <div>
      <VDivider/>
      <div class="bottom-button-box">
        <v-btn @click="createModifyRole" variant="tonal" :disabled="selectedRoleName.length < 2">
          {{ isNewRole ? "Create Role" : "Modify Role" }}</v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card-flex {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
}

h2 {
  padding: 10px 15px;
}

.create-select-role {
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  align-content: center;
  gap: 20px;
  padding: 20px;
}

.perms-box {
  display: flex;
  flex-wrap: wrap;
  padding: 0 20px;
  gap: 30px;
  justify-content: flex-start;
  align-items: flex-start;

  overflow: auto;
  height: calc(100% - 56px);
  /* 56px is h2 with padding */
}

.bottom-button-box {
  display: flex;
  justify-content: flex-end;
  padding: 10px 15px;
}
</style>

<style>
:root {
  --first-win-height: 160px;
}

.create-select-role-box {
  height: var(--first-win-height);
}

.role-permissions-box {
  height: calc(100% - 56px - var(--first-win-height));
}

@media (max-width: 700px) {
  :root {
    --first-win-height: 230px;
  }
}
</style>