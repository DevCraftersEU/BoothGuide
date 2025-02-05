<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { IExhibitor } from '@/model/models'
import { useAppStore } from '@/stores/app'
import { Ref } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps<{
  apiRequester: APIRequester
}>()

const api = props.apiRequester
const workingListView = ref(false)

const exhibitorsData: Ref<IExhibitor[]> = ref([])
const exhibitorToEdit: Ref<null | IExhibitor> = ref(null)

refreshTable()
const appStore = useAppStore()

function refreshTable () {
  workingListView.value = true
  api.getAllExhibitors().then(value => {
    console.log(value)
    appStore.saveExhibitors(value.data)
    exhibitorsData.value = value.data
  })
  exhibitorToEdit.value = null
  workingListView.value = false
}

const showCreationDialog = ref(false)
const showImportDialog = ref(false)

function deleteExhibitor (exh: IExhibitor) {
  workingListView.value = true
  api.deleteExhibitor(exh).then(() => refreshTable())
}

const { t } = useI18n()
</script>

<template>
  <div>
    <PleaseWaitDialog v-model="workingListView" />
    <ImportExhibitorsDialog
      v-model="showImportDialog"
      :api-requester="api"
      @imported="refreshTable"
    />
    <div class="text-center">
      <h1>Administration</h1>
      {{ t('admin.count-of-exhibitors', { count: exhibitorsData.length > 1 ? exhibitorsData.length : '' }) }}:
    </div>
    <p
      class="ma-4 mb-10"
      style="display: flex; flex-wrap: wrap; gap: 1em; justify-content: space-evenly"
    >
      <v-btn
        color="secondary"
        prepend-icon="mdi-import"
        @click="showImportDialog=true"
      >
        {{ t('admin.data-import') }}
      </v-btn>
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="showCreationDialog=true"
      >
        {{ t('admin.add-exhibitor') }}
      </v-btn>
      <v-btn
        color="secondary"
        download="exhibitors-export.json"
        href="/pub/exhibitors"
        prepend-icon="mdi-export"
      >
        {{ t('admin.data-export') }}
      </v-btn>
    </p>
    <v-table
      v-if="exhibitorsData.length > 0"
      density="comfortable"
    >
      <thead>
        <tr>
          <th class="text-left">
            {{ t('general.exhibitor') }}
          </th>
          <th class="text-left">
            {{ t('general.location') }}
          </th>
          <th class="text-left">
            {{ t('general.offer', 2) }}
          </th>
          <th class="text-left">
            {{ t('general.action', 2) }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="exh in exhibitorsData.sort((a, b) => a.name.localeCompare(b.name))"
          :key="exh.name"
        >
          <td>{{ exh.name }}</td>
          <td>{{ exh.roomNumber }}</td>
          <td>{{ exh.offers.map(v => v[0]).sort((a, b) => (a ?? '').localeCompare(b ?? '')).join(', ') }}</td>
          <td>
            <v-btn
              color="blue-darken-2"
              density="comfortable"
              icon="mdi-pencil"
              @click="exhibitorToEdit=exh; showCreationDialog=true"
            />
            <v-btn
              class="ml-3"
              color="red"
              density="comfortable"
              icon="mdi-trash-can"
              @click="deleteExhibitor(exh)"
            />
          </td>
        </tr>
      </tbody>
    </v-table>
    <div
      v-else
      class="text-center"
    >
      {{ t('admin.no-exhibitors-exists-message') }}
    </div>
  </div>
  <AddExhibitorDialog
    v-model="showCreationDialog"
    :api-requester="api"
    :exhibitor-to-edit="exhibitorToEdit"
    @closed="exhibitorToEdit=null; refreshTable()"
    @created="refreshTable"
  />
</template>

<style scoped>

</style>
