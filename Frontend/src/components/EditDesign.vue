<script setup lang="ts">
import { ISimplifiedDesign } from '@/model/models'
import { useI18n } from 'vue-i18n'
import { useTheme } from 'vuetify'

const emit = defineEmits<{ (e: 'saveTheme', colors: ISimplifiedDesign): void }>()

const { t } = useI18n()

const isFavorite = ref(true)
const exampleExhibitor = {
  id: 171,
  name: t('general.exhibitor') + ' 1',
  offers: [['Offer 1', 'Detailed Offer 1']],
  roomNumber: '713',
}

const theme = useTheme()
const design = theme.themes.value.preview

design.dark = false

const colorsToEdit = ref(['background', 'surface', 'primary'])

design.colors.background = theme.themes.value.default.colors.background
design.colors.surface = theme.themes.value.default.colors.surface
design.colors.primary = theme.themes.value.default.colors.primary

const currentSelectedPanel: Ref<string | undefined> = ref(undefined)

function saveDesign () {
  currentSelectedPanel.value = undefined
  emit('saveTheme', {
    background: design.colors.background,
    surface: design.colors.surface,
    primary: design.colors.primary,
    version: null,
  })
}
</script>

<template>
  <div>
    <div class="text-center">
      <h1>{{ t('edit-design-screen.heading') }}</h1>
      <h4>{{ t('edit-design-screen.subheading') }}</h4>
    </div>
    <div
      class="mt-10"
      style="display: flex; flex-direction: row; justify-content: center; gap: 5em; flex-wrap: wrap"
    >
      <div style="display: flex; flex-direction: column; gap: 1em">
        <v-checkbox
          v-model="design.dark"
          label="Is dark design"
          style="margin-left: 1em"
        />
        <v-expansion-panels
          v-model="currentSelectedPanel"
          style="width: 22em; max-width: 90vw;"
          variant="popout"
        >
          <v-expansion-panel
            v-for="c in colorsToEdit"
            :key="c"
          >
            <v-expansion-panel-title>
              {{ c }}
              <v-icon
                class="ml-3"
                :color="design.colors[c]"
                icon="mdi-circle"
              />
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-color-picker
                v-model="design.colors[c]"
                mode="hex"
              />
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </div>
      <div>
        <v-theme-provider theme="preview">
          <div>
            <v-container
              class="pa-8 v-theme--preview"
              style="background-color: rgba(var(--v-theme-background)); border-radius: 1em;"
            >
              <h1 style="color: rgba(var(--v-theme-on-background))">
                {{ t('exhibitor-overview.heading') }}
              </h1>
              <ExhibitorCard
                :exhibitor="exampleExhibitor"
                :favorite="isFavorite"
                @update-favorite="isFavorite=!isFavorite"
              />
              <div class="text-center">
                <v-btn
                  class="mt-5"
                  color="primary"
                >
                  {{ t('general.button') }}
                </v-btn>
              </div>
            </v-container>
          </div>
        </v-theme-provider>
      </div>
    </div>
    <div class="text-center mt-5">
      <v-btn
        color="primary"
        density="default"
        @click="saveDesign()"
      >
        {{ t('general.save') }}
      </v-btn>
    </div>
  </div>
</template>

<style scoped>

</style>
