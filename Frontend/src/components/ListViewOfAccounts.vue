<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { IApplicationUser } from '@/model/models'
import { Ref } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps<{
  apiRequester: APIRequester
}>()

const api = props.apiRequester
const workingListView = ref(false)
const showSnackbar = ref(false)
const snackBarMessage: Ref<undefined | string> = ref(undefined)

const accountsData: Ref<IApplicationUser[]> = ref([])
const accountToEdit: Ref<null | IApplicationUser> = ref(null)

refreshTable()

function refreshTable () {
  workingListView.value = true
  api.getUsers().then(value => {
    console.log(value)
    accountsData.value = value.data
  })
  accountToEdit.value = null
  workingListView.value = false
}

const showCreationDialog = ref(false)

function deleteAccount (account: IApplicationUser) {
  workingListView.value = true
  api.deleteUser(account).catch(err => {
    console.log(err)
    snackBarMessage.value = err.response.data
    showSnackbar.value = true
  }).finally(() => refreshTable())
}

const { t } = useI18n()
</script>

<template>
  <div>
    <PleaseWaitDialog v-model="workingListView" />
    <div class="text-center">
      <h1>Administration</h1>
      {{ t('admin.count-of-exhibitors', { count: accountsData.length > 1 ? accountsData.length : '' }) }}:
    </div>
    <p
      class="ma-4 mb-10"
      style="display: flex; flex-wrap: wrap; gap: 1em; justify-content: space-evenly"
    >
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="showCreationDialog=true"
      >
        {{ t('admin.add-user') }}
      </v-btn>
    </p>
    <v-table
      v-if="accountsData.length > 0"
      density="comfortable"
    >
      <thead>
        <tr>
          <th class="text-left">
            {{ t('general.id') }}
          </th>
          <th class="text-left">
            {{ t('general.username') }}
          </th>
          <th class="text-left">
            {{ t('general.role', 2) }}
          </th>
          <th class="text-left">
            {{ t('general.action', 2) }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="account in accountsData.sort((a, b) => (a.id ?? 0) - (b.id ?? 0))"
          :key="account.username"
        >
          <td>{{ account.id }}</td>
          <td>{{ account.username }}</td>
          <td>{{ account.roles.join(', ') }}</td>
          <td>
            <v-btn
              color="blue-darken-2"
              density="comfortable"
              icon="mdi-pencil"
              @click="accountToEdit=account; showCreationDialog=true"
            />
            <v-btn
              class="ml-3"
              color="red"
              density="comfortable"
              icon="mdi-trash-can"
              @click="deleteAccount(account)"
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
  <v-snackbar
    v-model="showSnackbar"
    color="error"
  >
    {{ snackBarMessage }}
  </v-snackbar>
  <AddUserDialog
    v-model="showCreationDialog"
    :other-user-names="accountsData.filter((account: IApplicationUser) => account.id !== (accountToEdit?.id??-1)).map((account: IApplicationUser) => account.username)"
    :api-requester="api"
    :user-to-edit="accountToEdit"
    @closed="accountToEdit=null; refreshTable()"
    @created="refreshTable"
  />
</template>

<style scoped>

</style>
