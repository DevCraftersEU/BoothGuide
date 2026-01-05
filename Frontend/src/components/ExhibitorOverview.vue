<script setup lang="ts">
import {IExhibitor} from '@/model/models'
import {useAppStore} from '@/stores/app'
import {computed, Ref} from 'vue'
import {useI18n} from 'vue-i18n'
import DOMPurify from 'dompurify';

const props = defineProps<{
  exhibitors: IExhibitor[]
}>()

const emit = defineEmits(['favoriteUpdated'])

const pageCount = 10

const appStore = useAppStore()
const searchValue: Ref<null | string> = ref(null)
const favorites: Ref<string[]> = ref(appStore.getFavorites())

const filteredValue = computed(() => {
  // eslint-disable-next-line vue/no-side-effects-in-computed-properties
  page.value = 1
  let ex: IExhibitor[]
  if (searchValue.value == null || !appStore.showSearchBar) {
    ex = props.exhibitors
  } else {
    ex = props.exhibitors.filter(exhibitor => exhibitor.name.toLowerCase().includes((searchValue.value ?? '').toLowerCase()) ||
      exhibitor.offers.find(of => of.filter(of2 => of2 !== undefined).find(of2 => of2.toLowerCase().includes((searchValue.value ?? '').toLowerCase()))))
  }
  if (offerSelection.value.length > 0) {
    ex = ex.filter(ex => ex.offers.map(o => o[0]).find(o2 => offerSelection.value.find(o3 => o2 === o3)))
  }
  return ex.sort((a, b) => {
    if (sortingSelection.value === 'A..Z') {
      return a.name.localeCompare(b.name)
    } else if (sortingSelection.value === 'Z..A') {
      return b.name.localeCompare(a.name)
    } else {
      return a.name.localeCompare(b.name)
    }
  })
})

function isInFavorites(name: string): boolean {
  return favorites.value.find(value => value === name) !== undefined
}

function updateFavorite(name: string) {
  favorites.value = appStore.changeFavorite(name)
  emit('favoriteUpdated')
}

const chosenExhibitor: Ref<null | IExhibitor> = ref(null)
const showMoreInfos = ref(false)
const page = ref(1)

const sortingSelection = ref('A..Z')
const sortingOptions = ['A..Z', 'Z..A']

const offerSelection = ref([])
const offerOptions = computed(() => {
  const offers = props.exhibitors.flatMap(e => e.offers.map(o => o[0])).filter(value => value != null).filter(value => value.trim().length > 0)
  return [...new Set(offers)].sort((a, b) => a.localeCompare(b))
})

const {t,} = useI18n()
</script>

<template>
  <div :id="'container'">
    <h2
      class="pa-2 pt-4"
      style="text-align: center"
    >
      {{ t('exhibitor-overview.heading') }}
    </h2>
    <!-- eslint-disable vue/no-v-html -->
    <h4
      v-if="appStore.instanceSubtitle && appStore.instanceSubtitle.trim().length > 0"
      class="mb-3"
      style="text-align: center"
      v-html="DOMPurify.sanitize(appStore.instanceSubtitle)"
    />
    <!--eslint-enable-->
    <v-expand-transition>
      <v-text-field
        v-if="appStore.showSearchBar"
        v-model="searchValue"
        class="ma-4"
        clearable
        hide-details
        :placeholder="t('general.search')"
        variant="outlined"
      />
    </v-expand-transition>
    <div
      class="pb-2 pl-2 pr-2"
      style="display: flex; flex-wrap: wrap; justify-content: space-around; gap: 0.5em"
    >
      <v-select
        v-model="offerSelection"
        :clearable="true"
        density="compact"
        hide-details
        :items="offerOptions"
        :multiple="true"
        :placeholder="t('exhibitor-overview.types-of-apprenticeships')"
        style="max-width: 15em"
        variant="underlined"
      >
        <template #selection="{item, index}">
          <v-chip
            v-if="index < 1"
            density="comfortable"
          >
            <span>{{ item.title }}</span>
          </v-chip>
          <span
            v-if="index === 1"
            class="text-grey text-caption align-self-center"
          >
            (+{{ offerSelection.length - 1 }})
          </span>
        </template>
      </v-select>
      <v-select
        v-model="sortingSelection"
        density="compact"
        hide-details
        :items="sortingOptions"
        style="max-width: 5em;"
        variant="underlined"
      />
    </div>
    <v-expand-transition>
      <div
        v-if="filteredValue.length != exhibitors.length"
        class="text-caption text-grey pb-2 pl-4 pr-4"
        style="text-align: center"
      >
        ({{ filteredValue.length }} {{ t('general.exhibitor', filteredValue.length) }}
        {{ t('exhibitor-overview.search-info-suffix', filteredValue.length) }})
      </div>
    </v-expand-transition>
    <ExhibitorCard
      v-for="exhibitor in filteredValue.slice((page - 1) * pageCount, page * pageCount)"
      :key="(exhibitor.id ?? -1).toString()"
      :exhibitor="exhibitor"
      :favorite="isInFavorites(exhibitor.name)"
      @show-more-info="chosenExhibitor=exhibitor; showMoreInfos=true;"
      @update-favorite="updateFavorite(exhibitor.name)"
    />
    <v-pagination
      v-if="Math.ceil(filteredValue.length / pageCount) > 1"
      v-model="page"
      :length="Math.ceil(filteredValue.length / pageCount)"
    />
    <MoreInfoDialog
      v-model="showMoreInfos"
      :exhibitor="chosenExhibitor"
      :search-term="searchValue"
    />
  </div>
</template>

<style scoped>

</style>
