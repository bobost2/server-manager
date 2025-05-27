<script setup lang="ts">
  definePageMeta({
    middleware: 'auth'
  })

  useHead({
    title: 'Instances - Server Manager'
  })
</script>

<template>
  <div style="padding: 10px;">
    <h1>
      Main Page
    </h1>
    <v-btn variant="tonal">
      Example Button
    </v-btn>
  </div>
</template>

<style scoped>

</style>