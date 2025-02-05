<template>
  <v-app>
    <v-fade-transition mode="out-in">
      <AppBar
        v-if="useRoute().path != '/'"
        style="z-index: 99"
      />
    </v-fade-transition>

    <v-main>
      <router-view v-slot="{ Component }">
        <v-fade-transition mode="out-in">
          <component
            :is="Component"
            style="margin-bottom: 7em"
          />
        </v-fade-transition>
      </router-view>
      <v-slide-y-reverse-transition>
        <v-footer
          v-if="appStore.showFooter && route.path != '/admin'"
          class="sponsor-footer"
        >
          <div style="display: flex; flex-direction: row; width: 100%; justify-content: center; gap: 1em;">
            <div
              v-if="showFooterImage"
              style="display: flex; flex-direction: column; justify-content: center"
            >
              <img
                :src="imagePath"
                style="max-height: 3em; max-width: 100%"
                alt="footer image"
                @error="showFooterImage=false"
              >
            </div>
            <div
              v-if="appStore.footerMessage!.trim().length > 0"
              style="display: flex; flex-direction: column; justify-content: center"
            >
              <!-- eslint-disable vue/no-v-html -->
              <span
                style="font-weight: bold"
                v-html="appStore.footerMessage"
              />
            </div>
          </div>
        </v-footer>
      </v-slide-y-reverse-transition>
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app'
import { useDisplay } from 'vuetify'

const appStore = useAppStore()
const showFooterImage = ref(true)

const display = useDisplay()
const route = useRoute()

const imagePath = ref('/api/footerImage?deviceSize=' + display.name.value)

watch(display.name, (value) => {
  imagePath.value = '/api/footerImage?deviceSize=' + value
  showFooterImage.value = true
})
</script>

<style scoped>
.sponsor-footer {
  border-top: 0.1em solid black;
  position: fixed;
  bottom: 0;
  left: 0;
  min-height: 5em;
  width: 100%;
  z-index: 100;
  text-align: center;
}
</style>
