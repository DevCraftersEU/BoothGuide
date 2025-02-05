<script setup lang="ts">

  import { APIRequester } from '@/api/ApiRequests'
  import { ISimplifiedDesign } from '@/model/models'
  import { useAppStore } from '@/stores/app'
  import { useI18n } from 'vue-i18n'
  import { useTheme } from 'vuetify'

  const authenticated = ref(false)
  const working = ref(false)

  const authUser = ref('')
  const authPass = ref('')
  const errorMessage = ref(false)

  const appStore = useAppStore()
  const apiRequester = new APIRequester()

  if (appStore.userName && appStore.userPass) {
    runAuthCheck(appStore.userName.toString(), appStore.userPass.toString())
  }

  function loginPressed () {
    runAuthCheck(authUser.value, authPass.value)
  }

  function runAuthCheck (userInput: string, passInput: string) {
    if (!userInput || !passInput) {
      return
    }
    working.value = true
    apiRequester.setAuth(userInput!, passInput!)
    apiRequester.checkApiAccess().then(value => {
      console.debug('Authenticated: ', value)
      authenticated.value = true
      appStore.userName = userInput!
      appStore.userPass = passInput!
      appStore.userRoles = value.data
      console.log('Roles in storage:', appStore.userRoles.length)
      appStore.showAdminEntry = true
    }).catch(() => {
      errorMessage.value = true
      appStore.userName = null
      appStore.userPass = null
    }).finally(() => working.value = false)
  }

  function onEnter (keyEvent: KeyboardEvent) {
    if (keyEvent.key === 'Enter') {
      loginPressed()
    }
  }

  const theme = useTheme()

  function updateTheme (newTheme : ISimplifiedDesign) {
    working.value = true
    apiRequester.setDesign(newTheme).then(() => {
      theme.themes.value.default.colors.background = newTheme.background
      theme.themes.value.default.colors.surface = newTheme.surface
      theme.themes.value.default.colors.primary = newTheme.primary
      appStore.design = 'Default'
      apiRequester.getDesign().then(res => {
        appStore.saveDesign(res.data)
        appStore.forceReloadAppBar = true
      })
    }).catch(e => console.error(e)).finally(() => working.value = false)
  }

  const defaultMessage = computed(() => {
    if((appStore.userRoles??[]).length > 0) {
      return t('admin.default-message-select-option')
    }
    return t('admin.no-roles-exists-error')
  })

  const currentView = ref('default')
  const { t } = useI18n()
</script>

<template>
  <div>
    <div
      v-if="!authenticated && !working"
      class="ma-5 text-center"
    >
      <h1>{{ t('admin.login-message') }}</h1>
      {{ t('admin.login-description') }}
      <div style="display: flex; align-items: center">
        <v-form style="width: 90%; max-width: 35em; margin: 0 auto;">
          <v-text-field
            v-model="authUser"
            autocomplete="on"
            class="mt-3"
            hide-details
            :label="t('general.username')"
            name="username"
            @keydown="onEnter"
          />
          <v-text-field
            v-model="authPass"
            autocomplete="on"
            class="mt-3"
            hide-details
            :label="t('general.password')"
            name="password"
            type="password"
            @keydown="onEnter"
          />
          <v-label
            v-if="errorMessage"
            class="mt-3"
            style="color: red"
            :text="t('admin.login-error-message')"
          />
          <p>
            <v-btn
              class="mt-3"
              :text="t('general.login')"
              @click="loginPressed"
            />
          </p>
        </v-form>
      </div>
    </div>
    <div
      v-else-if="!authenticated && working"
      class="ma-5"
    >
      {{ t('general.please-wait') }}
      <v-progress-circular :indeterminate="true" />
    </div>
    <div v-else>
      <v-scroll-x-transition mode="out-in">
        <div
          v-if="currentView=='exhibitors'"
          class="ma-5"
        >
          <ListViewOfExhibitors :api-requester="apiRequester" />
        </div>
        <div
          v-else-if="currentView=='design'"
          class="ma-5"
        >
          <EditDesign @save-theme="updateTheme" />
        </div>
        <div
          v-else-if="currentView=='accounts'"
          class="ma-5"
        >
          <ListViewOfAccounts :api-requester="apiRequester" />
        </div>
        <div v-else>
          <div style="display: flex; align-items: center; flex-direction: column; margin: 2em">
            <h1>Administration Panel</h1>
            <span class="text-center">{{ defaultMessage }}</span>
          </div>
        </div>
      </v-scroll-x-transition>
    </div>
    <v-bottom-navigation
      v-if="authenticated"
      v-model="currentView"
      mandatory
      style="position: fixed;"
    >
      <v-btn
        v-if="(appStore.userRoles ?? []).indexOf('ROLE_MODERATOR') > -1 || (appStore.userRoles ?? []).indexOf('ROLE_ADMIN') > -1"
        value="exhibitors"
      >
        <v-icon icon="mdi-account-group" />
        <span>{{ t('general.exhibitor' ) }}</span>
      </v-btn>
      <v-btn
        v-if="(appStore.userRoles ?? []).indexOf('ROLE_DESIGNER') > -1 || (appStore.userRoles ?? []).indexOf('ROLE_ADMIN') > -1"
        value="design"
      >
        <v-icon icon="mdi-palette" />
        <span>{{ t('general.design' ) }}</span>
      </v-btn>
      <v-btn
        v-if="(appStore.userRoles ?? []).indexOf('ROLE_ADMIN') > -1"
        value="accounts"
      >
        <v-icon icon="mdi-security" />
        <span>{{ t('general.accounts') }}</span>
      </v-btn>
    </v-bottom-navigation>
  </div>
</template>

<style scoped>

</style>
