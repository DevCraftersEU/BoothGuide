<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { exhibitorSchema, IExhibitor } from '@/model/models'
import { Ajv } from 'ajv'
import { useI18n } from 'vue-i18n'

const model = defineModel<boolean>({ default: false })
const emit = defineEmits(['imported'])
const props = defineProps<{
  apiRequester: APIRequester,
}>()

const file: Ref<null | File> = ref(null)
const result: Ref<null | IExhibitor[]> = ref(null)
const importError: Ref<null | string> = ref(null)
const detailedErrorMessages: Ref<string[]> = ref([])

const ajv = new Ajv()
const validate = ajv.compile(exhibitorSchema)

function reset (resetFile = true) {
  if (resetFile) {
    file.value = null
  }
  result.value = null
  importError.value = null
  detailedErrorMessages.value = []
  showDetailedErrors.value = false
  importRunning.value = false
  deleteExistingData.value = false
}

function readFile () {
  reset(false)
  if (!file.value) return
  file.value!.text().then(res => {
    try {
      result.value = JSON.parse(res)
    } catch (e) {
      console.error(e)
      file.value = null
      importError.value = t('import-dialog.error-parsing-json') + ': ' + e
      return
    }
    if (!Array.isArray(result.value)) {
      importError.value = t('import-dialog.error-no-array')
      return
    }
    for (const item of result.value) {
      if (!validate(item)) {
        console.log('Invalid item: ', item)
        console.log(validate.errors)
        validate.errors?.values().forEach(entry => detailedErrorMessages.value.push(`${t('import-dialog.error-in-item')} #${result.value!.indexOf(item)}: ${entry.message}`))
        importError.value = t('import-dialog.error-generic')
      }
    }
  })
}

const joinedDetailedErrors = computed(() => detailedErrorMessages.value.join('\n\n'))
const showDetailedErrors = ref(false)
const importRunning = ref(false)
const deleteExistingData = ref(false)

function startImport () {
  importRunning.value = true
  props.apiRequester.importExhibitors(result.value!, deleteExistingData.value).then(() => {
    reset(true)
    emit('imported')
    model.value = false
  })
}

const { t } = useI18n()

</script>

<template>
  <v-dialog
    v-model="model"
    width="500"
    persistent
  >
    <v-card :title="t('import-dialog.heading')">
      <v-card-text>
        <div
          v-if="!file || importError"
          class="text-center"
        >
          <v-file-input
            v-model="file"
            accept="application/json"
            :error-messages="importError"
            :hide-details="!importError"
            :label="t('import-dialog.placeholder-import-field')"
            variant="outlined"
            @update:model-value="readFile"
          />
          <div v-if="detailedErrorMessages.length>0">
            <v-btn
              append-icon="mdi-open-in-new"
              class="mt-5"
              color="warning"
              density="comfortable"
              variant="outlined"
              @click="showDetailedErrors=!showDetailedErrors"
            >
              {{ t('import-dialog.details-button') }}
            </v-btn>
            <v-expand-transition>
              <v-textarea
                v-if="showDetailedErrors"
                v-model="joinedDetailedErrors"
                class="mt-5"
                :readonly="true"
                variant="outlined"
              />
            </v-expand-transition>
          </div>
        </div>
        <div
          v-else-if="!result || importRunning"
          class="text-center"
        >
          <v-progress-linear
            class="mb-2"
            indeterminate
          />
          <span
            v-if="!result"
            style="font-size: small"
          >{{ t('import-dialog.loading-data') }}</span>
          <span
            v-else
            style="font-size: small"
          >{{ t('import-dialog.importing-data') }}</span>
        </div>
        <div v-else>
          {{ t('import-dialog.import-analysed-message', result!.length) }}
          <div class="mt-3">
            <li
              v-for="res in result.map(r => r.name)"
              :key="res"
            >
              {{ res }}
            </li>
          </div>
          <div>
            <v-checkbox
              v-model="deleteExistingData"
              color="red"
              :hide-details="true"
              :label="t('import-dialog.delete-existing-data')"
            />
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          v-if="file"
          prepend-icon="mdi-arrow-collapse-left"
          @click="reset"
        >
          {{ t('general.reset') }}
        </v-btn>
        <v-btn
          append-icon="mdi-close"
          @click="model=false"
        >
          {{ t('general.close') }}
        </v-btn>
        <v-btn
          v-if="file && result && !importError"
          append-icon="mdi-import"
          color="success"
          @click="startImport()"
        >
          {{ t('general.import') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
