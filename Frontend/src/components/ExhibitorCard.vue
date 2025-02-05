<script setup lang="ts">

import { IExhibitor } from '@/model/models'
import { useI18n } from 'vue-i18n'

defineProps<{ exhibitor: IExhibitor, favorite: boolean }>()
const emit = defineEmits(['favoriteUpdated', 'showMoreInfo', 'updateFavorite'])

const { t } = useI18n()
</script>

<template>
  <v-card
    :key="exhibitor.name"
    class="ma-2"
  >
    <v-card-title>
      {{ exhibitor.name }}
      <div
        v-if="exhibitor.roomNumber && exhibitor.roomNumber.trim().length > 0"
        style="font-size: small"
      >
        ({{ exhibitor.roomNumber }})
      </div>
    </v-card-title>
    <v-card-subtitle>
      <div
        style="display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; row-gap: 0.5em"
      >
        <div style="display: flex; flex-wrap: wrap; row-gap: 0.5em; justify-content: space-around">
          <div
            v-if="exhibitor.offers.length>0 && exhibitor.offers[0].length > 0 && exhibitor.offers[0][0]!.length > 0"
          >
            <v-chip
              v-for="(offer, offerIndex) in exhibitor.offers"
              :key="offerIndex"
              class="mr-2"
              color="on-surface"
              density="compact"
              :text="offer[0]"
            />
          </div>
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

</style>
