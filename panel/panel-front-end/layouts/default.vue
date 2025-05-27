<script setup lang="ts">

</script>

<template>
  <div class="page-contents">
    <app-header/>
    <slot/>
  </div>
</template>

<style scoped>
  .page-contents {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
</style>