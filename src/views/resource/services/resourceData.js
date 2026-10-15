import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'
import { resourceFromApi } from '../../../services/apiMappers'

export const resources = ref([])
export async function fetchResources(params = {}) {
  const response = await api.resources.list(params)
  resources.value = responseItems(response).map(resourceFromApi)
  return { ...response, items: resources.value }
}
export async function fetchResource(id) {
  const item = resourceFromApi(await api.resources.detail(id))
  resources.value = [item, ...resources.value.filter(row => String(row.id) !== String(id))]
  return item
}
export async function saveResource(values, id) {
  if (!values.title.trim() || !values.content.trim()) throw new Error('제목과 내용을 입력해 주세요.')
  const files = values.attachment instanceof File ? [values.attachment] : []
  const response = id ? await api.resources.update(id, { title: values.title.trim(), content: values.content.trim() }, files) : await api.resources.create({ title: values.title.trim(), content: values.content.trim() }, files)
  return response?.resourceId ?? id
}
export async function deleteResource(id) {
  await api.resources.remove(id)
  resources.value = resources.value.filter(item => String(item.id) !== String(id))
  return true
}
