<script setup lang="ts">

definePageMeta({
  middleware: 'auth-repo',
  redirect: '/repository/server-software'
})

useHead({
  title: 'Repository - Server Manager'
})


const sidebarButtons : SidebarButton[] = [
  {
    id: 'server-software',
    icon: 'mdi-server-network',
    label: 'Server Software',
    link: '/repository/server-software'
  },
  {
    id: 'custom-extensions',
    icon: 'mdi-puzzle',
    label: 'Custom Extensions',
    link: '/repository/custom-extensions'
  },
  {
    id: 'cached-extensions',
    icon: 'mdi-database',
    label: 'Cached Extensions',
    link: '/repository/cached-extensions'
  }
];
</script>

<template>
  <div class="page-inside-contents">
    <sidebar :items="sidebarButtons" link-based>
      <h2 class="sidebar-title">Repository</h2>
    </sidebar>
    <NuxtPage/>
  </div>
</template>

<style scoped>

</style>