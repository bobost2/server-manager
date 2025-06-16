<script setup lang="ts">

const props = defineProps({
  items: {
    type: Array as PropType<SidebarButton[]>,
    required: true
  },
  linkBased: {
    type: Boolean,
    default: false
  },
  // Support for v-model
  modelValue: {
    type: String,
    required: false
  }
})

const emit = defineEmits(['update:modelValue']);

onMounted(() => {
  if (!props.linkBased && props.items.length > 0) {
    emit('update:modelValue', props.items[0].id);
  }
});

function onSelect(itemId: string) {
  emit('update:modelValue', itemId);
}

</script>

<template>
  <div class="sidePanel">
    <div class="width-constraint">
      <slot/>
    </div>

    <VBtn v-if="linkBased" v-for="item in items" variant="text" rounded="0" :prepend-icon="item.icon"
          :to="item.link" class="button-shrink">
      <span class="width-constraint">{{ item.label }}</span>
    </VBtn>

    <VBtn v-else v-for="item in items" variant="text" rounded="0" :prepend-icon="item.icon"
          @click="onSelect(item.id)" :active="modelValue === item.id" class="button-shrink">
      <span class="width-constraint">{{ item.label }}</span>
    </VBtn>
  </div>
</template>

<style scoped>
  .sidePanel {
    background: var(--transparent-bg-color);
    box-shadow: 1px 0 5px 0 var(--shadow-color);
    min-width: 250px;
    width: 250px;

    display: flex;
    flex-direction: column;
  }

  @media (max-width: 700px) {
    .width-constraint {
      display: none;
    }

    .sidePanel {
      --side-panel-min-width: 50px;

      width: var(--side-panel-min-width) !important;
      min-width: var(--side-panel-min-width) !important;
    }

    .button-shrink {
      width: var(--side-panel-min-width) !important;
      max-width: var(--side-panel-min-width) !important;
      min-width: var(--side-panel-min-width) !important;
      padding-left: calc(var(--side-panel-min-width)/2) !important;
    }
  }
</style>