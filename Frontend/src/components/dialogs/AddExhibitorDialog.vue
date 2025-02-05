<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { IExhibitor } from '@/model/models'
import { useAppStore } from '@/stores/app'
import { Ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const exhibitorToCreate: Ref<IExhibitor> = ref({
  id: null,
  name: '',
  offers: [],
  roomNumber: '',
})

const model = defineModel<boolean>({ default: false })
const emit = defineEmits(['created', 'closed'])
const props = defineProps<{
  apiRequester: APIRequester,
  exhibitorToEdit: IExhibitor | null
}>()

const api = props.apiRequester

const appStore = useAppStore()

const searchType: Ref<[string | undefined]> = ref([undefined])
const searchOffer: Ref<Array<Array<string | undefined>>> = ref([])
const currentFocusedOfferType: Ref<undefined | string> = ref(undefined)
const currentFocusedOfferIndex: Ref<number> = ref(0)
const working = ref(false)

watch(model, () => {
  if (props.exhibitorToEdit != null) {
    exhibitorToCreate.value = props.exhibitorToEdit
    exhibitorToCreate.value.offers.forEach(() => searchOffer.value.push([]))
    if (exhibitorToCreate.value.offers.length === 1 && exhibitorToCreate.value.offers[0].length === 1 && (exhibitorToCreate.value.offers[0][0] ?? '').trim().length === 0) {
      exhibitorToCreate.value.offers.splice(0, 1)
    }
    offersChanged()
  }
  if (!model.value) {
    resetExhibitorToCreate()
    searchType.value = [undefined]
    searchOffer.value = []
    emit('closed')
  }
})

function offersChanged () {
  for (const offerIndex in exhibitorToCreate.value.offers) {
    for (const subOfferIndex in exhibitorToCreate.value.offers[offerIndex]) {
      if (Number(subOfferIndex) === 0) {
        continue
      } else if (Number(subOfferIndex) === exhibitorToCreate.value.offers[offerIndex].length - 1) {
        if ((exhibitorToCreate.value.offers[offerIndex][Number(subOfferIndex)] ?? []).length !== 0) {
          exhibitorToCreate.value.offers[offerIndex].push(undefined)
        }
      } else if ((exhibitorToCreate.value.offers[offerIndex][subOfferIndex] ?? []).length === 0) {
        exhibitorToCreate.value.offers[offerIndex].splice(Number(subOfferIndex), 1)
      }
    }
  }
}

function resetExhibitorToCreate () {
  exhibitorToCreate.value = {
    id: null,
    name: '',
    offers: [],
    roomNumber: '',
  }
  currentFocusedOfferType.value = undefined
  currentFocusedOfferIndex.value = 0
}

function save () {
  working.value = true
  console.debug('Saving pressed. Saving the instance', exhibitorToCreate.value)
  exhibitorToCreate.value.offers.forEach(o => {
    if ((o[o.length - 1] ?? '').trim().length === 0) {
      o.splice(o.length - 1, 1)
    }
  })
  let req
  if (!exhibitorToCreate.value.id) {
    req = api.saveExhibitor(exhibitorToCreate.value)
  } else {
    req = api.updateExhibitor(exhibitorToCreate.value)
  }
  req.then(value => {
    working.value = false
    model.value = false
    emit('created')
    console.log(value)
  })
}

const autoFillTypes = computed(() => {
  const offers = appStore.getExhibitors().flatMap(exhibitor => exhibitor.offers.map(offer => offer[0]))
  searchType.value.filter(s => s !== undefined && s.trim().length !== 0).forEach(s => offers.push(s!))
  const unique = [...new Set(offers)]
  return unique.filter(u => exhibitorToCreate.value.offers.flatMap(offers => offers[0]).indexOf(u) === -1)
})

const autoFillOffers = computed(() => {
  if (currentFocusedOfferType.value === undefined) return []
  const offers = appStore.getExhibitors().flatMap(exhibitor => exhibitor.offers.filter(offer => offer[0] !== undefined).filter(offer => offer[0]!.indexOf(currentFocusedOfferType.value!) > -1).flatMap(offer => offer.slice(1)))
  if (!searchOffer.value[currentFocusedOfferIndex.value]) {
    console.log(currentFocusedOfferIndex.value, searchOffer.value[currentFocusedOfferIndex.value])
  }
  searchOffer.value[currentFocusedOfferIndex.value].filter(i => i !== undefined && i.trim().length > 0).forEach(i => offers.push(i))
  const unique = [...new Set(offers)]
  return unique.filter(u => !exhibitorToCreate.value.offers[currentFocusedOfferIndex.value].slice(1).find(o => o === u))
})
</script>

<template>
  <v-dialog
    v-model="model"
    :persistent="true"
    width="40em"
  >
    <v-card
      max-width="800"
      min-width="300"
      :subtitle="t('exhibitor-create.subtitle')"
      :title="t('exhibitor-create.title')"
    >
      <v-card-text>
        <div v-if="!working">
          <v-text-field
            v-model="exhibitorToCreate.name"
            :label="t('exhibitor-create.name-of-exhibitor')"
            variant="outlined"
          />
          <v-text-field
            v-model="exhibitorToCreate.roomNumber"
            :label="t('exhibitor-create.location-of-exhibitor')"
            variant="outlined"
          />
          <div style="display: flex; justify-content: space-around">
            <span>{{ t('exhibitor-create.message-offers') }}:</span>
          </div>
          <div
            v-for="(offer, index) in exhibitorToCreate.offers"
            :key="index"
            class="mt-3"
          >
            <hr class="mb-3">
            <div style="display: flex; justify-content: space-between">
              <v-autocomplete
                v-model="offer[0]"
                v-model:search="searchType[index]"
                :auto-select-first="true"
                density="compact"
                :hide-details="true"
                :hide-no-data="true"
                :hide-selected="true"
                :items="autoFillTypes"
                :placeholder="t('exhibitor-create.placeholder-type')"
                style="font-weight: bold"
                variant="plain"
              />
              <v-btn
                color="red"
                density="compact"
                icon="mdi-minus"
                tabindex="-1"
                @click="searchOffer.splice(index,1); exhibitorToCreate.offers.splice(index,1)"
              />
            </div>
            <div>
              <v-autocomplete
                v-for="(offerDetail,index2) in offer.slice(1)"
                :key="index2"
                v-model="offer[index2+1]"
                v-model:search="searchOffer[index][index2]"
                :auto-select-first="true"
                :disabled="offer[0] == undefined"
                class="pl-2"
                density="compact"
                :hide-details="true"
                :hide-no-data="true"
                :hide-selected="true"
                :ignored="offerDetail"
                :items="autoFillOffers"
                :placeholder="t('exhibitor-create.placeholder-offer')"
                variant="plain"
                @update:focused="focused => {if(focused){currentFocusedOfferIndex = index; currentFocusedOfferType = offer[0]; console.log(index,index2)}}"
                @update:model-value="offersChanged"
              />
            </div>
          </div>
          <hr class="mt-3 mb-3">
          <div style="text-align: center">
            <v-btn
              color="secondary"
              density="compact"
              icon="mdi-plus"
              @click="searchOffer.push([]); exhibitorToCreate.offers.push([undefined,undefined])"
            />
          </div>
        </div>
        <div
          v-else-if="working"
          style="margin: 0 auto; text-align: center"
        >
          <v-progress-circular
            indeterminate
            size="40"
          />
          <p>{{ t('exhibitor-create.exhibitor-creation-process') }}</p>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          append-icon="mdi-close"
          :disabled="working"
          @click="model=false"
        >
          {{ t('general.close') }}
        </v-btn>
        <v-btn
          append-icon="mdi-content-save"
          color="green-lighten-1"
          :disabled="working || exhibitorToCreate.name.trim().length==0"
          @click="save()"
        >
          {{ t('general.save') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
