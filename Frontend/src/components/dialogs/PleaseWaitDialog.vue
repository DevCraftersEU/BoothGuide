<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const model = defineModel<boolean>({ default: false })

defineProps<{
  message?: string | null
}>()

const { t } = useI18n()
</script>

<template>
  <v-dialog
    v-model="model"
    persistent
    width="auto"
  >
    <v-card
      max-width="800"
      min-width="300"
      title="Bitte warten..."
    >
      <v-card-text>
        <v-progress-linear
          class="mb-4"
          indeterminate
        />
        {{ message ?? t('general.please-wait') }}
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
