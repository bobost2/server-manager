<script setup lang="ts">

definePageMeta({
  middleware: 'auth',
  redirect: '/user-settings/general'
})

useHead({
  title: 'User Settings - Server Manager'
})

const sidebarButtons : SidebarButton[] = [
  {
    id: 'general',
    icon: 'mdi-cog',
    label: 'general',
    link: '/user-settings/general'
  }
];

</script>

<template>
  <div class="page-inside-contents">
    <sidebar :items="sidebarButtons" link-based>
      <h2 class="sidebar-title">User Settings</h2>
    </sidebar>
    <NuxtPage/>
  </div>
</template>

<style scoped>

</style>