<script setup lang="ts">
  import { APIRequester } from '@/api/ApiRequests'
  import { useAppStore } from '@/stores/app'

  const appStore = useAppStore()
  const data = ref(appStore.getExhibitors())
  new APIRequester().getAllExhibitors().then(res => {
    appStore.saveExhibitors(res.data)
  })

  let lastUpdateCount = appStore.updateCount
  watch(appStore, () => {
    if (lastUpdateCount !== appStore.updateCount) {
      console.debug('Exhibitors in local storage changed')
      data.value = appStore.getExhibitors()
      lastUpdateCount = appStore.updateCount
    }
  })
</script>

<template>
  <div>
    <ExhibitorOverview
      :exhibitors="data"
      style="max-width: 40em; margin: 0 auto;"
    />
  </div>
</template>

<style scoped>

</style>
