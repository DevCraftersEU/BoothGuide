<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { useAppStore } from '@/stores/app'
import { Ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useTheme } from 'vuetify'

const appStore = useAppStore()
const router = useRouter()

const drawer = ref(false)

console.log(useTheme().themes.value)

const reloading = ref(false)
const error: Ref<string | null> = ref(null)

function reload () {
  reloading.value = true
  const startTime = new Date().getTime()
  new APIRequester().getAllExhibitors().then(res => {
    appStore.saveExhibitors(res.data)
    setTimeout(() => {
      reloading.value = false
    }, new Date().getTime() - startTime + 1500)
  }).catch(reason => {
    error.value = reason.toString()
  })
}

function isStandaloneView () {
  return window.matchMedia('(display-mode: standalone)').matches
}

const showInstallButton = ref(!isStandaloneView())
const showInstallDialog = ref(false)

function install () {
  showInstallDialog.value = true
}

const designChoiceExpanded = ref(false)

const showAdminSideBarEntry = ref(appStore.showAdminEntry)
const showLogoutButton = ref(appStore.userName != null && appStore.userPass != null)
const existingDesigns = ref(['Light', 'Dark'])
if (appStore.getDesign()) {
  existingDesigns.value = ['Default', 'Light', 'Dark']
}

watch(appStore, () => {
  showAdminSideBarEntry.value = appStore.showAdminEntry
  showLogoutButton.value = appStore.userName != null && appStore.userPass != null
  if (appStore.forceReloadAppBar) {
    if (appStore.getDesign()) {
      existingDesigns.value = ['Default', 'Light', 'Dark']
    } else {
      existingDesigns.value = ['Light', 'Dark']
    }
    appStore.forceReloadAppBar = false
  }
})

const { t } = useI18n()
</script>

<template>
  <div>
    <v-app-bar
      :elevation="2"
      :title="(appStore.instanceName ?? 'Undefined').toString() "
    >
      <template #prepend>
        <v-app-bar-nav-icon @click="drawer = !drawer" />
      </template>
      <template #append>
        <v-btn icon="mdi-theme-light-dark">
          <v-icon
            icon="mdi-theme-light-dark"
            style="transition: transform .2s ease-in-out !important;"
            :style="{transform: designChoiceExpanded ? 'rotate(180deg)' : 'rotate(0deg)'}"
          />
          <v-menu
            v-model="designChoiceExpanded"
            activator="parent"
          >
            <v-list>
              <v-list-item
                v-for="item in existingDesigns"
                :key="item"
                :value="item"
                @click="appStore.design = item"
              >
                {{ item }}
              </v-list-item>
            </v-list>
          </v-menu>
        </v-btn>
        <div style="width: 3em; text-align: center">
          <v-fab-transition mode="out-in">
            <v-btn
              v-if="!reloading && !error"
              icon="mdi-reload"
              @click="reload"
            />
            <v-btn
              v-else-if="error!=null"
              icon="mdi-alert"
            >
              <v-icon icon="mdi-alert" />
              <v-tooltip
                activator="parent"
                location="bottom"
                :text="error"
              />
            </v-btn>
            <v-progress-circular
              v-else
              indeterminate
              size="24"
            />
          </v-fab-transition>
        </div>
        <v-btn
          icon="mdi-magnify"
          @click="appStore.showSearchBar = !appStore.showSearchBar"
        />
        <v-btn
          v-if="showLogoutButton"
          icon="mdi-logout"
          @click="appStore.userName=null;appStore.userPass=null;appStore.userRoles=null;router.push('/');"
        />
      </template>
    </v-app-bar>
    <v-navigation-drawer
      v-model="drawer"
      width="300"
    >
      <v-list>
        <v-list-item
          class="list-item-margin"
          prepend-icon="mdi-view-list"
          @click="() => router.push('/overview')"
        >
          <v-list-item-title>{{ t('general.overview') }}</v-list-item-title>
          <v-list-item-subtitle>{{ t('menu.overview') }}</v-list-item-subtitle>
        </v-list-item>
        <v-list-item
          class="list-item-margin"
          prepend-icon="mdi-star"
          @click="() => router.push('/favorites')"
        >
          <v-list-item-title>{{ t('general.favorites') }}</v-list-item-title>
          <v-list-item-subtitle>{{ t('menu.favorites') }}</v-list-item-subtitle>
        </v-list-item>
        <v-list-item
          v-if="showAdminSideBarEntry"
          class="list-item-margin"
          prepend-icon="mdi-security"
          @click="() => router.push('/admin')"
        >
          <v-list-item-title>{{ t('general.administration') }}</v-list-item-title>
          <v-list-item-subtitle>{{ t('menu.administration') }}</v-list-item-subtitle>
        </v-list-item>
        <v-divider style="margin: 1em;" />
        <v-list-item
          class="list-item-margin"
          prepend-icon="mdi-restart-alert"
          @click="router.push('/#resetApplication')"
        >
          <v-list-item-title>{{ t('general.reset-app') }}</v-list-item-title>
          <v-list-item-subtitle>{{ t('menu.reset') }}</v-list-item-subtitle>
        </v-list-item>
        <v-list-item
          v-if="showInstallButton"
          class="list-item-margin"
          prepend-icon="mdi-download"
          @click="install()"
        >
          <v-list-item-title>{{ t('general.install') }}</v-list-item-title>
          <v-list-item-subtitle>{{ t('menu.install') }}</v-list-item-subtitle>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <ShowInstallDialog v-model="showInstallDialog" />
  </div>
</template>

<style>
.list-item-margin {
  margin-top: 0.5em;
  margin-bottom: 1em;
}
</style>
