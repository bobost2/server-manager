<script setup lang="ts">
import {nextTick, type PropType} from "vue";

const props = defineProps({
  type: {
    type: String as PropType<"error" | "success" | "warning" | "info">,
    required: false
  },
  title: {
    type: String,
    required: false
  },
  enableProgress: {
    type: Boolean,
    default: false
  },
  time: {
    type: Number,
    default: 3000
  },
  icon: {
    type: String,
    default: null
  },
  color: {
    type: String,
    default: null
  },
  opened: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:opened']);
const insideOpen = ref(false);

let timerId: ReturnType<typeof setInterval> | null = null;
async function startTimer() {
  await nextTick();
  const progressBar = document.querySelector('.progressBar') as HTMLElement | null;
  if (!progressBar) return;

  let width = 100;
  const interval = 10; // ms
  const decrement = (interval / props.time) * 100;

  progressBar.style.width = '100%';

  if (timerId) {
    clearInterval(timerId);
    timerId = null;
  }

  timerId = setInterval(() => {
    width -= decrement;
    if (width <= 0) {
      width = 0;
      insideOpen.value = false;
      if (timerId) {
        clearInterval(timerId);
        timerId = null;
      }
    }
    progressBar.style.width = width + '%';
  }, interval);
}

watch(
    () => props.opened,
    (newVal) => {
      if (newVal && props.enableProgress) {
        insideOpen.value = true;
        emit('update:opened', false);
        startTimer();
      }
    }
);
</script>

<template>
  <div class="bottom-right">
    <v-slide-y-reverse-transition group>
      <div v-if="insideOpen">
        <v-alert :type="type" :title="title" :icon="icon" :color="color">
          <slot/>
        </v-alert>
        <div class="progressBar" v-if="enableProgress"/>
      </div>
    </v-slide-y-reverse-transition>
  </div>
</template>

<style scoped>
  .bottom-right {
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 1000;
  }

  .progressBar {
    background: white;
    height: 3px;
    width: 100%;
    margin-top: -3px;
    position: absolute;
    border-bottom-left-radius: 3px;
    border-bottom-right-radius: 3px;
  }
</style>