<script setup lang="ts">
  import type {UserInfo} from "~/types/user-info";

  async function onLogout() {
    await $fetch('http://localhost:8080/user/logout', {
      method: 'POST',
      credentials: 'include'
    }).catch(() => {
    }).finally(() => {
      navigateTo("/login");
    })
  }

  const { data } = await useFetch<UserInfo>("http://localhost:8080/user/info", {
    method: 'GET',
    credentials: 'include'
  })
</script>

<template>
  <header>
    <div class="logoTitle">
      <v-icon icon="mdi-server-network" size="35"/>
      <h1 style="margin: 0 5px 0 10px;">
        Server Manager
      </h1>
      <h4 style="margin-bottom: 15px;">
        ALPHA
      </h4>
    </div>
    <nav>
      <v-btn variant="text" to="/" exact>
        Instances
      </v-btn>
      <v-btn variant="text" to="/repository" exact>
        Repository
      </v-btn>
      <v-btn v-if="data?.permissions?.includes('ADMIN')" variant="text" to="/panel-management" exact>
        Panel Management
      </v-btn>
      <v-btn variant="text">
        {{data?.username}}
        <v-menu activator="parent">
          <v-list style="background: var(--green-darker); border: 1px var(--border-color) solid;">
            <v-list-item to="/user-settings" exact key="settings" value="settings">
              <div class="list-item">
                <v-icon icon="mdi-cog"/>
                <v-list-item-title>Settings</v-list-item-title>
              </div>
            </v-list-item>

            <v-list-item @click="onLogout" key="logout" value="logout">
              <div class="list-item">
                <v-icon icon="mdi-logout"/>
                <v-list-item-title>Logout</v-list-item-title>
              </div>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-btn>
    </nav>
  </header>
  <slot/>
</template>

<style scoped>
  header {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    background: var(--green-darker);
    padding: 5px 15px;
    box-shadow: 0 2px 5px 0 #000000a1;
    position: sticky;
    top: 0;
  }

  .logoTitle {
    display: flex;
    align-items: center;
    flex-direction: row;
  }

  .list-item {
    display: flex;
    align-items: center;
    gap: 10px;
    color: var(--text-color-1);
  }
</style>