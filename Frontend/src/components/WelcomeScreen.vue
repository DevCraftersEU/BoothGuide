<template>
  <v-container class="fill-height">
    <v-responsive
      class="align-center fill-height mx-auto"
    >
      <div
        v-if="show"
        class="loading-div"
      >
        <h1 style="margin-bottom: 0.5em">
          {{ text }}
        </h1>
        <v-progress-linear
          indeterminate
          style="max-width: 30em; margin: 0 auto;"
        />
        <p style="font-size: smaller; color: gray; padding-top: 1em">
          {{ subText }}
        </p>
      </div>
    </v-responsive>
  </v-container>
</template>

<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import router from '@/router'
import { useAppStore } from '@/stores/app'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'

const appStore = useAppStore()
const route = useRoute()
const { t } = useI18n()

const show = ref(appStore.getExhibitors().length === 0 || route.hash === '#resetApplication')
const text = ref(appStore.instanceName ? appStore.instanceName : t('welcome-screen.message'))
const subText = ref(t('welcome-screen.loading'))

if (route.hash === '#resetApplication') {
  console.log(route.hash)
  subText.value = t('welcome-screen.reset')
  appStore.resetLocalStorage()
  router.push('/')
  setTimeout(() => {
    window.location.reload()
  }, 3000)
} else if (appStore.getExhibitors().length > 0) {
  router.push('/overview')
} else {
  console.debug('Getting all exhibitors from server')
  const startTime = new Date().getTime()
  new APIRequester().getAllExhibitors().then(value => {
    console.log(value.data)
    appStore.saveExhibitors(value.data)
    setTimeout(() => router.push('/overview'), new Date().getTime() - startTime + 1000)
  })
}
// setTimeout(() => router.push('/overview'), 3000)
</script>

<style>
.loading-div {
  text-align: center;
}
</style>
