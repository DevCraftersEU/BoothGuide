import { IApplicationUser, IExhibitor, ISimplifiedDesign } from '@/model/models'
import axios, { AxiosResponse } from 'axios'

export class APIRequester {
  ax = axios.create({
    adapter: 'fetch',
    withCredentials: false,
  })

  auth = {
    username: '',
    password: '',
  }

  setAuth (username: string, password: string) {
    this.auth.username = username
    this.auth.password = password
  }

  getInstanceName () {
    return this.ax.get('/pub/instanceName')
  }

  getInstanceSubtitle () {
    return this.ax.get('/pub/instanceSubtitle')
  }

  checkApiAccess (): Promise<AxiosResponse<string[]>> {
    return this.ax.get('/auth/checkAuth', { auth: this.auth })
  }

  getFooterMessage (): Promise<AxiosResponse<string>> {
    return this.ax.get('/pub/footerMessage')
  }

  getAllExhibitors (): Promise<AxiosResponse<IExhibitor[]>> {
    return this.ax.get('/pub/exhibitors')
  }

  saveExhibitor (exhibitor: IExhibitor): Promise<AxiosResponse<IExhibitor>> {
    return this.ax.post('/auth/exhibitors', exhibitor, { auth: this.auth })
  }

  updateExhibitor (exhibitor: IExhibitor): Promise<AxiosResponse<IExhibitor>> {
    return this.ax.put('/auth/exhibitors', exhibitor, { auth: this.auth })
  }

  deleteExhibitor (exhibitor: IExhibitor): Promise<AxiosResponse<void>> {
    return this.ax.delete('/auth/exhibitors/' + exhibitor.id, { auth: this.auth })
  }

  importExhibitors (exhibitors: IExhibitor[], deleteExisting: boolean): Promise<AxiosResponse<void>> {
    return this.ax.post('/auth/exhibitors/import/' + deleteExisting, exhibitors, { auth: this.auth })
  }

  getDesign (): Promise<AxiosResponse<ISimplifiedDesign>> {
    return this.ax.get('/pub/design')
  }

  setDesign (design: ISimplifiedDesign): Promise<AxiosResponse<ISimplifiedDesign>> {
    return this.ax.post('/auth/design', design, { auth: this.auth })
  }

  getUsers (): Promise<AxiosResponse<IApplicationUser[]>> {
    return this.ax.get('/auth/users', { auth: this.auth })
  }

  saveUser (newUser: IApplicationUser): Promise<AxiosResponse<IApplicationUser>> {
    return this.ax.post('/auth/users', newUser, { auth: this.auth })
  }

  updateUser (user: IApplicationUser): Promise<AxiosResponse<IApplicationUser>> {
    return this.ax.put('/auth/users', user, { auth: this.auth })
  }

  deleteUser (user: IApplicationUser): Promise<AxiosResponse<void>> {
    return this.ax.delete('/auth/users/' + user.id, { auth: this.auth })
  }
}
