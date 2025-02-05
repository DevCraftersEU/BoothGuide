<script setup lang="ts">
import { IExhibitor } from '@/model/models'
import { useI18n } from 'vue-i18n'

const model = defineModel<boolean>({ default: false })
defineProps<{
  exhibitor: IExhibitor | null
  searchTerm: string | null
}>()

const highlightTextColor = ref('rgb(255,255,0,0.3)')

watch(model, () => {
  if (model.value) {
    highlightTextColor.value = 'rgb(255,255,0,0.3)'
    setTimeout(() => highlightTextColor.value = '', 1000)
  }
})

const { t } = useI18n()
</script>

<template>
  <v-dialog
    v-model="model"
    width="auto"
  >
    <v-card
      max-width="600"
      min-width="300"
      :subtitle="exhibitor?.roomNumber"
      :title="exhibitor?.name"
    >
      <v-card-text>
        <div
          v-if="exhibitor && exhibitor!.offers && exhibitor!.offers.length > 0 && exhibitor!.offers[0].length>0&& exhibitor!.offers[0][0]!.length>0"
        >
          {{
            t('exhibitor-details.message-given-options', {
              ex: exhibitor?.name,
              exRoom: (exhibitor?.roomNumber && exhibitor?.roomNumber.trim().length > 0 ? '(' + exhibitor?.roomNumber + ')' : '')
            })
          }}
          <div
            v-for="offer in exhibitor?.offers"
            :key="offer[0]"
            style="margin-top: 1em"
          >
            <h3>{{ offer[0] }}</h3>
            <ul class="ml-5">
              <li
                v-for="(n, index) in offer.slice(1)"
                :key="index"
                style="hyphens: auto; line-height: 1.1"
                :style="{marginTop: index != 0 ? '0.4em':'0.3em'}"
              >
                <span
                  :style="{backgroundColor: (searchTerm && (n ?? '').toLowerCase().includes(searchTerm.toLowerCase()) ? highlightTextColor : '')}"
                >{{ n }}</span>
              </li>
            </ul>
          </div>
        </div>
        <div v-else>
          Schau bei {{ exhibitor?.name }} <span v-if="exhibitor?.roomNumber && exhibitor?.roomNumber.trim().length > 0"> ({{
            exhibitor?.roomNumber
          }}) </span>vorbei, um alles über die weiteren Ausbildungsmöglichkeiten erfahren.
        </div>
        <v-card-actions>
          <v-spacer />
          <v-btn
            append-icon="mdi-close"
            @click="model =false"
          >
            {{ t('general.close') }}
          </v-btn>
        </v-card-actions>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
