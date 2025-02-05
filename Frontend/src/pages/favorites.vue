<script setup lang="ts">
  import { useAppStore } from '@/stores/app'

  const appStore = useAppStore()
  const favorites = ref(appStore.getFavorites())

  function favoritesUpdated () { favorites.value = appStore.getFavorites() }

</script>

<template>
  <ExhibitorOverview
    :exhibitors="appStore.getExhibitors().filter(e1 => favorites.find(e2 => e1.name == e2))"
    style="max-width: 40em; margin: 0 auto;"
    @favorite-updated="favoritesUpdated()"
  />
</template>

<style scoped>

</style>
