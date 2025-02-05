<template>
  <v-app>
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
  import { APIRequester } from '@/api/ApiRequests'
  import { useAppStore } from '@/stores/app'
  import { HttpStatusCode } from 'axios'
  import { useI18n } from 'vue-i18n'
  import { useTheme } from 'vuetify'

  const appStore = useAppStore()
  const globalTheme = useTheme()
  const i18n = useI18n()

  i18n.locale.value = navigator.language.split('-')[0]

  const api = new APIRequester()
  api.getInstanceName().then(res => {
    appStore.instanceName = res.data
    document.title = res.data
  }).catch(() => {
    document.title = (appStore.instanceName ?? 'Undefined').toString()
  })

  api.getInstanceSubtitle().then(res => {
    appStore.instanceSubtitle = res.data
  }).catch(() => console.error('Could not receive subtitle!'))

  api.getFooterMessage().then(res => {
    if(res.status === HttpStatusCode.NoContent) {
      appStore.showFooter = false
      appStore.footerMessage = undefined
    } else {
      appStore.showFooter = true
      appStore.footerMessage = res.data
    }
  })

  function updateDesign () {
    console.log(globalTheme.themes.value)
    const design = appStore.getDesign()
    if (design) {
      globalTheme.themes.value.default.colors.background = design.background
      globalTheme.themes.value.default.colors.surface = design.surface
      globalTheme.themes.value.default.colors.primary = design.primary
    }
  }

  updateDesign()

  new APIRequester().getDesign().then(res => {
    if (res.status === 200 && (!appStore.getDesign() || appStore.getDesign()!.version !== res.data.version)) {
      console.log('Updating theme')
      if (appStore.getDesign() == null) { appStore.design = 'Default' }
      appStore.saveDesign(res.data)
      updateDesign()
    } else if (res.status === 204 && appStore.getDesign()) {
      appStore.deleteDesign()
      appStore.forceReloadAppBar = true
      appStore.design = 'Light'
    }
  })

  globalTheme.global.name.value = appStore.design.toLowerCase()

  watch(appStore, () => {
    if (appStore.design.toLowerCase() !== globalTheme.global.name.value) {
      console.debug('Switching design to', appStore.design.toLowerCase())
      globalTheme.global.name.value = appStore.design.toLowerCase()
      console.log(globalTheme.global)
    }
  })

</script>
