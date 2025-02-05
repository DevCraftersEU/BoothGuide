<script setup lang="ts">
import { APIRequester } from '@/api/ApiRequests'
import { IApplicationUser } from '@/model/models'
import { Ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { VTextField } from 'vuetify/components'

const { t } = useI18n()

const userToCreate: Ref<IApplicationUser> = ref({
  id: null,
  username: '',
  password: '',
  roles: []
})

const model = defineModel<boolean>({ default: false })
const emit = defineEmits(['created', 'closed'])
const props = defineProps<{
  apiRequester: APIRequester,
  userToEdit: IApplicationUser | null
  otherUserNames: string[]
}>()

function hasUppercase (s: string): boolean {
  return /[A-Z]/.test(s)
}

function hasLowercase (s: string): boolean {
  return /[a-z]/.test(s)
}

function hasDigit (s: string): boolean {
  return /\d/.test(s)
}

function hasSpecialChar (s: string): boolean {
  return /[^a-zA-Z0-9]/.test(s)
}

function getErrorMessageForPasswordComplexity (value: string): string {
  const matchesNeeded = checkPasswordsComplexity(value)
  const errorMessage = 'Please add ' + matchesNeeded + ' of the following complexity characteristics: '
  const additions = []
  if (!hasUppercase(value)) {
    additions.push('upper case character')
  }
  if (!hasLowercase(value)) {
    additions.push('lowercase case character')
  }
  if (!hasDigit(value)) {
    additions.push('digit character')
  }
  if (!hasSpecialChar(value)) {
    additions.push('special character')
  }
  return errorMessage + additions.join(', ') + (value.length < 15 ? ' or choose a longer password' : '')
}

function checkPasswordsComplexity (value: string): number {
  let matchesNeeded = value.length < 15 ? 3 : 2
  if (hasUppercase(value)) {
    matchesNeeded--
  }
  if (hasLowercase(value)) {
    matchesNeeded--
  }
  if (hasDigit(value)) {
    matchesNeeded--
  }
  if (hasSpecialChar(value)) {
    matchesNeeded--
  }
  return matchesNeeded < 0 ? 0 : matchesNeeded
}

const rules = ref([
  (value: string) => value != undefined || 'Password must be set!',
  (value: string) => value.length > 8 || 'Password must be at least 8 characters.',
  (value: string) => value.length <= 4096 || 'Password can be 4096 chars max',
  (value: string) => checkPasswordsComplexity(value) == 0 || getErrorMessageForPasswordComplexity(value)
])

watch(model, () => {
  if (props.userToEdit !== null) {
    userToCreate.value = props.userToEdit
  }
  if (!model.value) {
    resetUserToCreate()
    emit('closed')
  }
})

const api = props.apiRequester

const working = ref(false)

function resetUserToCreate () {
  userToCreate.value = {
    id: null,
    username: '',
    password: '',
    roles: []
  }
  passwordRepeatFieldModel.value = undefined
  showSnackbar.value = false
  snackbarMessage.value = ''
}

function save () {
  working.value = true
  console.debug('Saving pressed. Saving the instance', userToCreate.value)
  let req
  if (!userToCreate.value.id) {
    req = api.saveUser(userToCreate.value)
  } else {
    req = api.updateUser(userToCreate.value)
  }
  req.then(value => {
    model.value = false
    emit('created')
    console.log(value)
  }).catch(error => {
    console.log(error)
    snackbarMessage.value = error.response.data
    showSnackbar.value = true
  }).finally(() => {
    working.value = false
  })
}

const showPasswordField = ref(false)
const showPasswordRepeatField = ref(false)
const passwordRepeatFieldModel = ref(undefined)
const showSnackbar = ref(false)
const snackbarMessage = ref('')

const usernameField: Ref<undefined | VTextField> = ref(undefined)
const passwordField: Ref<undefined | VTextField> = ref(undefined)
const passwordRepeatField: Ref<undefined | VTextField> = ref(undefined)

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
          <v-form>
            <v-text-field
              ref="usernameField"
              v-model="userToCreate.username"
              :label="t('general.username')"
              variant="outlined"
              :rules="[(value: string) => props.otherUserNames.map(u => u.toLowerCase()).indexOf(value.toLowerCase()) === -1 || 'Der Nutzername ist schon vergeben']"
              autocomplete="username"
              class="mb-2"
            />
            <v-text-field
              ref="passwordField"
              v-model="userToCreate.password"
              :append-icon="showPasswordField ? 'mdi-eye' : 'mdi-eye-off'"
              :type="showPasswordField ? 'text' : 'password'"
              :label="t('general.password')"
              variant="outlined"
              :rules="rules"
              autocomplete="new-password"
              class="mb-2"
              @click:append="showPasswordField = !showPasswordField"
            />
            <v-text-field
              ref="passwordRepeatField"
              v-model="passwordRepeatFieldModel"
              :append-icon="showPasswordRepeatField ? 'mdi-eye' : 'mdi-eye-off'"
              :type="showPasswordRepeatField ? 'text' : 'password'"
              :label="t('general.passwordRepeat')"
              variant="outlined"
              :rules="[ (value: string) => value != undefined, (value: string) => value === userToCreate.password || 'Passwords do not match']"
              autocomplete="new-password"
              class="mb-2"
              @click:append="showPasswordRepeatField = !showPasswordRepeatField"
            />
            <v-checkbox
              v-model="userToCreate.roles"
              :hide-details="true"
              label="Designer (manage design)"
              value="DESIGNER"
            />
            <v-checkbox
              v-model="userToCreate.roles"
              :hide-details="true"
              label="Moderator (manage exhibitors)"
              value="MODERATOR"
            />
            <v-checkbox
              v-model="userToCreate.roles"
              :hide-details="true"
              label="Administrator (full accesss)"
              value="ADMIN"
            />
          </v-form>
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
          @click="save()"
        >
          {{ t('general.save') }}
        </v-btn>
      </v-card-actions>
      <v-snackbar
        v-model="showSnackbar"
        :multi-line="true"
      >
        <!-- eslint-disable vue/no-v-html -->
        <span v-html="snackbarMessage" />
      </v-snackbar>
    </v-card>
  </v-dialog>
</template>

<style scoped>

</style>
<!--           :disabled="working || userToCreate.username.trim().length==0 || !usernameField?.isValid || !passwordField?.isValid || !passwordRepeatField?.isValid"
-->
