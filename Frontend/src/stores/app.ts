// Utilities
import { defineStore } from 'pinia'
import { RemovableRef, useLocalStorage, useSessionStorage } from '@vueuse/core'

import { IExhibitor, ISimplifiedDesign } from '@/model/models'

export const useAppStore = defineStore('app', () => {
  const showSearchBar = ref(false)
  const design = useLocalStorage('app/design', 'Light')
  const instanceName: RemovableRef<string | undefined> = useLocalStorage('app/instanceName', undefined)
  const instanceSubtitle: RemovableRef<string | undefined> = useLocalStorage('app/instanceSubtitle', undefined)
  const showFooter: RemovableRef<boolean> = useLocalStorage('app/showFooter', false)
  const footerMessage: RemovableRef<string | undefined> = useLocalStorage('app/footerMessage', undefined)
  const exhibitorKey = 'app/exhibitors'
  const updateCount = ref(0)
  const showAdminEntry = useLocalStorage('app/showAdminEntry', false)
  const userName: RemovableRef<string | null> = useSessionStorage('app/admin/user', null)
  const userPass: RemovableRef<string | null> = useSessionStorage('app/admin/pass', null)
  const userRoles: RemovableRef<string[] | null> = useSessionStorage('app/roles', null)
  const forceReloadAppBar = ref(false)

  function changeFavorite (fav: string) {
    const favorites: string[] = JSON.parse(localStorage.getItem('app/favorites') ?? '[]')
    if (favorites.find(value => value === fav)) {
      localStorage.setItem('app/favorites', JSON.stringify(favorites.filter(value => value !== fav)))
    } else {
      favorites.push(fav)
      localStorage.setItem('app/favorites', JSON.stringify(favorites))
    }
    return getFavorites()
  }

  function getFavorites (): string[] {
    return JSON.parse(localStorage.getItem('app/favorites') ?? '[]')
  }

  function getExhibitors (): IExhibitor[] {
    return JSON.parse(localStorage.getItem(exhibitorKey) ?? '[]')
  }

  function saveExhibitors (exhibitors: IExhibitor[]) {
    console.log('Saving exhibitors')
    localStorage.setItem(exhibitorKey, JSON.stringify(exhibitors))
    updateCount.value = updateCount.value + 1
  }

  function saveDesign (design: ISimplifiedDesign) {
    localStorage.setItem('config/design', JSON.stringify(design))
  }

  function getDesign (): ISimplifiedDesign | null {
    if (localStorage.getItem('config/design')) {
      return JSON.parse(localStorage.getItem('config/design')!)
    }
    return null
  }

  function deleteDesign () {
    localStorage.removeItem('config/design')
  }

  function resetLocalStorage () {
    localStorage.clear()
    sessionStorage.clear()
  }

  return {
    showSearchBar,
    design,
    userName,
    userPass,
    userRoles,
    showFooter,
    footerMessage,
    changeFavorite,
    getFavorites,
    getExhibitors,
    saveExhibitors,
    resetLocalStorage,
    saveDesign,
    getDesign,
    deleteDesign,
    updateCount,
    instanceName,
    instanceSubtitle,
    showAdminEntry,
    forceReloadAppBar,
  }
})
