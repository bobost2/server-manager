<script setup lang="ts">

definePageMeta({
  middleware: 'auth-panel',
  redirect: '/panel-management/users'
})

useHead({
  title: 'Panel Management - Server Manager'
})


const sidebarButtons : SidebarButton[] = [
  {
    id: 'users',
    icon: 'mdi-account-group',
    label: 'Users',
    link: '/panel-management/users'
  },
  {
    id: 'roles',
    icon: 'mdi-shield-account',
    label: 'Roles',
    link: '/panel-management/roles'
  },
  {
    id: 'api-keys',
    icon: 'mdi-key',
    label: 'API Keys',
    link: '/panel-management/api-keys'
  },
  {
    id: 'site-settings',
    icon: 'mdi-cog',
    label: 'Site Settings',
    link: '/panel-management/site-settings'
  }
];
</script>

<template>
  <div class="page-inside-contents">
    <sidebar :items="sidebarButtons" link-based>
      <h2 class="sidebar-title">Panel Management</h2>
    </sidebar>
    <NuxtPage/>
  </div>
</template>

<style scoped>

</style>