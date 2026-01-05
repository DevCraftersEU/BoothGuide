export interface IExhibitor {
  id: number | null | undefined,
  name: string,
  offers: Array<Array<string|undefined>>,
  roomNumber: string | undefined,
  httpLink?: string | undefined,
}

export interface ISimplifiedDesign {
  background: string,
  surface: string,
  primary: string,
  version: number | null | undefined,
}

export interface IApplicationUser {
  id: number | undefined | null,
  username: string,
  password: string | undefined
  roles: string[]
}

export const exhibitorSchema = {
  type: 'object',
  properties: {
    id: { type: 'number' },
    name: { type: 'string' },
    offers: {
      type: 'array',
      items: {
        type: 'array',
        items: { type: 'string' },
      },
    },
    roomNumber: { type: 'string' },
    httpLink: { type: 'string' },
  },
  required: ['id', 'name', 'offers', 'roomNumber'],
}
