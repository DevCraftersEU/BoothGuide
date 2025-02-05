<script setup lang="ts">
  import { useAppStore } from '@/stores/app'
  import { useI18n } from 'vue-i18n'

  const model = defineModel<boolean>({ default: false })
  const appStore = useAppStore()

  const tab = ref('pclaptop')

  const ua = navigator.userAgent
  if (/android/i.test(ua)) {
    tab.value = 'android'
  } else if (/iPad|iPhone|iPod/.test(ua)) {
    tab.value = 'ios'
  }

  const { t } = useI18n()
</script>

<template>
  <v-dialog
    v-model="model"
    style="max-width: 35em"
    width="auto"
  >
    <v-card
      max-width="800"
      min-width="300"
      :title="'Installation von ' + (appStore.instanceName ?? 'Undefined').toString()"
    >
      <v-card-text>
        <v-tabs
          v-model="tab"
          class="mt-3"
        >
          <v-tab value="ios">
            iOS
          </v-tab>
          <v-tab value="android">
            Android
          </v-tab>
          <v-tab value="pclaptop">
            PC / Laptop
          </v-tab>
        </v-tabs>
        <v-tabs-window
          v-model="tab"
          class="pa-2"
          style="text-align: justify"
        >
          <v-tabs-window-item value="ios">
            {{ t('install-dialog.ios.1') }} <v-icon icon="mdi-export-variant" /> {{ t('install-dialog.ios.2') }}
          </v-tabs-window-item>
          <v-tabs-window-item value="android">
            {{ t('install-dialog.android.1') }} <v-icon icon="mdi-dots-vertical" /> {{ t('install-dialog.android.2') }}
          </v-tabs-window-item>
          <v-tabs-window-item value="pclaptop">
            {{ t('install-dialog.pclaptop.1') }} <v-icon icon="mdi-monitor-arrow-down-variant" /> {{ t('install-dialog.pclaptop.2') }} <v-icon icon="mdi-shape-square-plus" /> {{ t('install-dialog.pclaptop.3') }}
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          append-icon="mdi-close"
          @click="model =false"
        >
          {{ t('general.close') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
