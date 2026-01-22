<script setup lang="ts">

import {IExhibitor} from '@/model/models'
import {useI18n} from 'vue-i18n'

defineProps<{ exhibitor: IExhibitor, favorite: boolean }>()
const emit = defineEmits(['favoriteUpdated', 'showMoreInfo', 'updateFavorite'])

const {t} = useI18n()
</script>

<template>
  <v-card
    :key="exhibitor.name"
    class="ma-2"
  >
    <v-card-title>
      <div style="display: flex; align-items: start; justify-content: space-between; text-wrap: auto">
        {{ exhibitor.name }}
        <a
          v-if="exhibitor.httpLink && exhibitor.httpLink.trim().length > 0"
          :href="exhibitor.httpLink"
          class="ml-2"
          rel="noopener"
          target="_blank"
          :title="t('general.open-link')"
          style="font-size: smaller; text-decoration: none"
        >
          <v-icon
            class="pt-3 pr-2"
            icon="mdi-open-in-new"
            size="small"
          />
        </a>
      </div>
      <div
        v-if="exhibitor.roomNumber && exhibitor.roomNumber.trim().length > 0"
        style="font-size: small"
      >
        ({{ exhibitor.roomNumber }})
      </div>
    </v-card-title>
    <v-card-subtitle>
      <div
        style="display: flex; justify-content: space-between; align-items: center; row-gap: 0.5em"
      >
        <div
          v-if="exhibitor.offers.length>0 && exhibitor.offers[0].length > 0 && exhibitor.offers[0][0]!.length > 0"
          style="display: flex; row-gap: 0.5em; overflow-x: auto;  justify-content: space-around; max-width: 100%; -webkit-overflow-scrolling: touch;"
          class="hide-scrollbar"
        >
          <v-chip
            v-for="(offer, offerIndex) in exhibitor.offers"
            :key="offerIndex"
            class="mr-2"
            color="on-surface"
            density="compact"
            :text="offer[0]"
            style="flex: 0 0 auto"
          />
        </div>
      </div>
    </v-card-subtitle>
    <v-card-actions>
      <v-btn
        :text="t('general.more-info')"
        variant="text"
        @click="emit('showMoreInfo')"
      />
      <v-spacer />
      <v-btn
        :color="favorite ? 'yellow' : ''"
        icon="mdi-star"
        @click="emit('updateFavorite')"
      />
    </v-card-actions>
  </v-card>
</template>

<style scoped>
/* Chrome, Edge, Safari */
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}

/* Firefox */
.hide-scrollbar {
  scrollbar-width: none;
}
</style>
