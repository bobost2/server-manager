<script setup lang="ts">
import type { NuxtError } from '#app'

defineProps({
  error: Object as () => NuxtError
});

function goBack() {
  const router = useRouter()
  router.back()
}
</script>

<template>
  <div class="error-pane">
    <h1>
      {{ error!.statusCode }}
    </h1>
    <p>
      {{ error!.statusMessage }}
    </p>
    <div class="button-box">
      <v-btn variant="tonal" @click="goBack">
        Go back
      </v-btn>
      <v-btn variant="tonal" to="/">
        Return to panel
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
  .error-pane {
    height: 100vh;
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
  }

  h1 {
    margin: 0;
    font-size: 60px;
  }

  p {
    margin-bottom: 10px;
    font-weight: 500;
    font-size: 20px;
  }

  .button-box {
    display: inline-flex;
    gap: 20px;
  }
</style>